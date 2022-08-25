package ds.core;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.connection.events.LpTerminatedEvent;
import ddb.dsz.core.connection.events.OperationChanged;
import ddb.dsz.core.connection.events.StatisticsEvent;
import ddb.dsz.core.connection.events.ThrottleEvent;
import ddb.dsz.core.controller.CommandSet;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.controller.CoreController.OperationState;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.Plugin;
import ddb.imagemanager.ImageManager;
import ddb.splash.SplashScreen3;
import ddb.util.GeneralUtilities;
import ddb.util.JaxbCache;
import ddb.util.LogFileManager;
import ddb.util.LpConfigWriter;
import ddb.util.PluginInitInfo;
import ddb.util.StartupConfigParser;
import ddb.util.jar.JarFileClassInspector;
import ds.core.controller.AbstractCoreController;
import ds.core.controller.MutableCoreController;
import ds.core.impl.HostInfoImpl;
import ds.gui.PluginWorkbench;
import ds.gui.QuitAction;
import ds.jaxb.guiconfig.GuiConfig;
import ds.jaxb.guiconfig.ObjectFactory;
import ds.jaxb.ipc.ConnectionInfoType;
import ds.jaxb.ipc.HostInfoType;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.ThrottleInfoType;
import ds.plugin.peer.Peer;
import ds.plugin.peer.PeerServer;
import ds.plugin.replay.ReplayTableModel;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class DSClient extends AbstractCoreController implements CoreController, MutableCoreController {
//   Runnable Repaint = new Runnable() {
//      @Override
//      public void run() {
//      }
//   };
   private CommandSet commands = new CommandSet();
   private LogFileManager logManager;
   private int dispatcherPort;
   private SplashScreen3 splash;
   private int remoteAddress = -1;
   private Operation liveOperation;
   private Thread connectionThread = null;
   private ConnectionEstablisher establisher = null;
   private boolean quitHappened = false;
   private JCheckBoxMenuItem debugItem;
   private JCheckBoxMenuItem autoFocus;
   private OperationState opState;
   private final ButtonGroup lnfButtons;
   private final int SPLASH_MAJOR_STAGES;
   private int SPLASH_MAJOR_CURRENT;
   private int SPLASH_MINOR_STAGES;
   private int SPLASH_MINOR_CURRENT;
   private boolean startedDirectoryMonitor;
   private WindowListener windowListener;
   static final AtomicBoolean success = new AtomicBoolean(false);

   public DSClient(SplashScreen3 splashScreen3, String[] args) {
      super(args);
      this.opState = OperationState.NotConnected;
      this.lnfButtons = new ButtonGroup();
      this.SPLASH_MAJOR_STAGES = 5;
      this.SPLASH_MAJOR_CURRENT = 0;
      this.SPLASH_MINOR_STAGES = 0;
      this.SPLASH_MINOR_CURRENT = 0;
      this.startedDirectoryMonitor = false;
      this.windowListener = new WindowAdapter() {
         @Override
         public void windowOpened(WindowEvent var1) {
            DSClient.this.setFocusOnDefaultElement();
         }

         @Override
         public void windowClosing(WindowEvent var1) {
            DSClient.this.requestShutdown();
         }

         @Override
         public void windowActivated(WindowEvent var1) {
            if (DSClient.this.autoFocus != null && DSClient.this.autoFocus.isSelected()) {
               DSClient.this.setFocusOnDefaultElement();
            }

         }
      };
      this.splash = splashScreen3;
      splashScreen3.setDisplayedText("Initializing internal data");
      splashScreen3.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      splashScreen3.setStageIcon(DSConstants.StageIcon.INTERNAL_DATA.getPath());
      this.systemLogger = Logger.getLogger("ds.core");
      this.systemLogger.setLevel(Level.ALL);
      this.systemLogger.setUseParentHandlers(false);
      File guiSystemLog = new File(this.getLogDir(), "GuiSystemLog");
      guiSystemLog.mkdirs();

      FileHandler fileHandler;
      try {
         fileHandler = new FileHandler(String.format("%s/%s-%%u.xml", guiSystemLog.getAbsolutePath(), GeneralUtilities.CalendarToStringFile(Calendar.getInstance())));
         fileHandler.setLevel(Level.WARNING);
         this.systemLogger.addHandler(fileHandler);
      } catch (Exception var19) {
         this.logEvent(Level.WARNING, var19.getMessage(), var19);
      }

      if (this.isDebugMode()) {
         try {
            fileHandler = new FileHandler(String.format("%s/%s-%%u.all.xml", guiSystemLog.getAbsolutePath(), GeneralUtilities.CalendarToStringFile(Calendar.getInstance())));
            fileHandler.setLevel(Level.ALL);
            this.systemLogger.addHandler(fileHandler);
         } catch (Exception var18) {
            this.logEvent(Level.WARNING, var18.getMessage(), var18);
         }
      }

      this.logManager = new LogFileManager(this.getLogDir() + "/Logs/", "dsz", this.systemLogger);
      splashScreen3.setDisplayedText("Loading GUI configuration");
      splashScreen3.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      splashScreen3.setStageIcon(DSConstants.StageIcon.STARTUP_CONFIG.getPath());
      JAXBContext jaxbContext = JaxbCache.getContext(ObjectFactory.class);
      String[] var7;
      if (jaxbContext != null) {
         try {
            Unmarshaller var6 = jaxbContext.createUnmarshaller();
            var7 = new String[]{"Ops", "Dsz", "."};

            for(String s: var7) {
               File var12 = new File(String.format("%s/%s/Gui/Config/guiConfig.xml", this.getResourceDirectory(), s));
               if (var12.exists()) {
                  Object var13 = var6.unmarshal(var12);
                  if (var13 instanceof JAXBElement) {
                     var13 = ((JAXBElement)JAXBElement.class.cast(var13)).getValue();
                  }

                  if (var13 instanceof GuiConfig) {
                     GuiConfig var14 = (GuiConfig)GuiConfig.class.cast(var13);
                     DSConstants.WINDOW_WIDTH = var14.getWindowSize().getWidth().intValue();
                     DSConstants.WINDOW_HEIGHT = var14.getWindowSize().getHeight().intValue();
                     DSConstants.FRAME_WIDTH = var14.getFrameSize().getWidth().intValue();
                     DSConstants.FRAME_HEIGHT = var14.getFrameSize().getHeight().intValue();
                     break;
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      splashScreen3.setDisplayedText("Loading plugin configuration");
      splashScreen3.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      splashScreen3.setStageIcon(DSConstants.StageIcon.PLUGIN.getPath());
      List systemStartup = DSConstants.getStartupConfigurationFiles(new File(this.getResourceDirectory()), "systemStartup");
      Iterator iterator = systemStartup.iterator();

      File file;
      while(iterator.hasNext()) {
         file = (File)iterator.next();

         try {
            this.autoloadPlugins.addAll(StartupConfigParser.parse((InputStream)(new FileInputStream(file)), this.getLogDir(), String.format("%s/%s", this.getResourceDirectory(), this.getDefaultPackage()), this.getBuildType()));
         } catch (Exception var17) {
            this.logEvent(Level.SEVERE, String.format("Exception while parsing config XML file\n%s\n%s", file.getAbsolutePath(), var17.getMessage()), var17);
            return;
         }
      }

      splashScreen3.setDisplayedText("Creating socket for dispatcher IPC");
      splashScreen3.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      splashScreen3.setStageIcon(DSConstants.StageIcon.SOCKET.getPath());

      ServerSocket serverSocket;
      try {
         serverSocket = new ServerSocket(0);
      } catch (Exception e) {
         this.logEvent(Level.SEVERE, e.getMessage(), e);
         return;
      }

      this.dispatcherPort = serverSocket.getLocalPort();
      this.establisher = new ConnectionEstablisher(serverSocket, this);
      this.connectionThread = this.newThread(this.establisher);
      this.connectionThread.start();
      file = new File(this.getLogDir(), "config.xml");
      file.getParentFile().mkdirs();

      try {
         if (this.remoteAddress > -1) {
            LpConfigWriter.writeConfig(this.dispatcherPort, this.getLogDir(), String.format("%s/%s", this.getResourceDirectory(), this.getDefaultPackage()), (long)this.remoteAddress, file, this.getLocalhostAddress());
         } else {
            LpConfigWriter.writeConfig(this.dispatcherPort, this.getLogDir(), String.format("%s/%s", this.getResourceDirectory(), this.getDefaultPackage()), file, this.getLocalhostAddress());
         }

      } catch (Exception e) {
         this.logEvent(Level.SEVERE, e.getMessage(), e);
      }
   }

   @Override
   public void initialize2() {
      this.getWorkbench().addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent var1) {
            StatusBar statusBar = DSClient.this.getStatusBar();
            PluginWorkbench workbench = DSClient.this.getWorkbench();
            if (statusBar != null && workbench != null) {
               statusBar.setStatus(workbench.getStatus());
            }

            DSClient.this.addConnectionChangeListener(statusBar);
         }
      });
      this.logEvent(Level.INFO, "logDir = " + this.getLogDir());
      this.logEvent(Level.INFO, "resourceDir = " + this.getResourceDirectory());
      this.setupMenuBar();
      this.getWorkbench().setTabPlacement(DSConstants.MAIN_TAB_ALIGNMENT);
      this.splash.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      this.SPLASH_MINOR_STAGES = this.autoloadPlugins.size();
      EventQueue.invokeLater(new DSClient.InitializeStep(this.autoloadPlugins));
   }

   public void establishConnection() {
      this.splash.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
      this.splash.setStageIcon(DSConstants.StageIcon.WAITING.getPath());

      try {
         String var1 = "Waiting for connection";
         byte total = 15;

         for(int i = 0; i < total; ++i) {
            this.splash.setDisplayedText(String.format("%s:  %d seconds left", var1, total - i));
            this.splash.setMinorProgress(0, total, i);

            try {
               this.connectionThread.join(TimeUnit.SECONDS.toMillis(1L));
               if (!this.connectionThread.isAlive()) {
                  success.set(true);
                  break;
               }
            } catch (Exception var12) {
            }
         }

         if (!success.get()) {
            this.splash.setMajorProgress(0, 5, this.SPLASH_MAJOR_CURRENT++);
            this.splash.setDisplayedText(String.format("Press 'skip' to continue"));
            this.splash.setMinorIndeterminate(true);
            this.splash.showSkip(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent var1) {
                  DSClient.success.set(true);
               }
            });

            while(!success.get()) {
               try {
                  this.connectionThread.join(TimeUnit.SECONDS.toMillis(1L));
                  if (!this.connectionThread.isAlive()) {
                     success.set(true);
                  }
               } catch (Exception var11) {
               }
            }
         }

         Socket connection = this.establisher.getConnection();
         if (connection == null) {
            StartupException startupException = new StartupException("No connection established with LP dispatcher");
            startupException.fillInStackTrace();
            throw startupException;
         }

         this.dispatcherClient.addLiveCommandDispatcher(connection);
      } catch (Exception e) {
         this.logEvent(Level.SEVERE, e.getMessage(), e);
         return;
      } finally {
         this.establisher = null;
         this.connectionThread = null;
      }

   }

   @Override
   protected void parseArg(String arg, String val) {
      if (arg.equals("-build")) {
         super.setBuildType(val);
      } else if (arg.equals("-remoteAddress")) {
         this.remoteAddress = Integer.parseInt(val);
      } else if (arg.equals("-loadPrevious")) {
         ReplayTableModel.getReplayModel().setAutoLoad(Boolean.parseBoolean(val));
      } else {
         super.parseArg(arg, val);
      }

   }

   public void setupMaintenanceTasks() {
      this.scheduleWithFixedDelay(new Runnable() {
         boolean failed = false;

         @Override
         public void run() {
            if (!this.failed) {
               if (OperationState.Inactive.equals(DSClient.this.getOperationState())) {
                  this.failed = true;
               } else {
                  try {
                     DSClient.this.dispatcherClient.requestCommandListUpdate();
                  } catch (Exception var2) {
                     DSClient.this.logEvent(Level.WARNING, "commandListUpdateTask exception", var2);
                     this.failed = true;
                  }

               }
            }
         }
      }, 30L, 30L, TimeUnit.SECONDS);
   }

   @Override
   public void updateConnectionInfo(Message message) {
      ConnectionInfoType connectionInfoType = message.getInfo().getConnectionInfo();
      if (connectionInfoType != null) {
         TaskId taskId = TaskId.GenerateTaskId(message.getInfo().getCmdId(), this.getOperation());
         Iterator iterator = connectionInfoType.getHost().iterator();

         while(iterator.hasNext()) {
            HostInfoType var5 = (HostInfoType)iterator.next();
            this.handleHostInfo(var5, taskId);
         }

         try {
            this.dispatcherClient.requestCommandListUpdate();
         } catch (Exception e) {
            this.logEvent(Level.SEVERE, "Unable to update command list", e);
         }
      }

      ThrottleInfoType throttleInfoType = message.getInfo().getThrottleInfo();
      if (throttleInfoType != null) {
         this.fireConnectionChangeEvent(new ThrottleEvent(this, throttleInfoType.getAddress(), throttleInfoType.getBytesPerSecond()));
      }

   }

   private void handleHostInfo(HostInfoType hostInfoType, TaskId taskId) {
      if (hostInfoType != null) {
         Object hostInfo = new HostInfoImpl(hostInfoType.getAddress(), hostInfoType.getHostname(), hostInfoType.getVersion(), hostInfoType.getArch(), hostInfoType.getPlatform(), hostInfoType.getImplantType(), hostInfoType.isLocal(), hostInfoType.isConnected());
         boolean found = false;
         synchronized(this.hosts) {
            Iterator iterator = this.hosts.iterator();

            while(iterator.hasNext()) {
               HostInfo info = (HostInfo)iterator.next();
               if (info.sameHost((HostInfo)hostInfo)) {
                  info.copyFromHost((HostInfo)hostInfo);
                  hostInfo = info;
                  found = true;
                  break;
               }
            }
         }

         ((HostInfo)hostInfo).setTask(taskId);
         if (!found) {
            this.hosts.add((HostInfo) hostInfo);
         }

         if (((HostInfo)hostInfo).isConnected()) {
            this.fireConnectionNewHost((HostInfo)hostInfo);
         } else {
            this.fireDisconnectedHost((HostInfo)hostInfo);
         }

         if (!this.startedDirectoryMonitor) {
            this.createDirectoryMonitor();
         }

      }
   }

   @Override
   public void updateStatistics(StatisticsEvent statisticsEvent) {
      this.fireStatisticsChanged(statisticsEvent);
   }

   @Override
   public void setOwningFrame(JFrame owningFrame) {
      super.setOwningFrame(owningFrame);
      owningFrame.addWindowListener(this.windowListener);
   }

   @Override
   public final void setTitle(String title) {
      super.setTitle(title);
//      title = "网络零元购";
      super.setTitle(title);

      if (this.mPeer != null) {
         title = String.format("%d: %s", (PeerServer.class.cast(this.mPeer)).getServerPort(), title);
      }

      this.title = title;
      EventQueue.invokeLater(new DSClient.Title());
   }

   private void setupMenuBar() {
      JMenu jMenu = new JMenu("File");
      ImageIcon var2 = ImageManager.getIcon(DSConstants.Icon.QUIT.getPath(), ImageManager.SIZE22);
      JMenuItem var3 = new JMenuItem("Quit", var2);
      JMenuItem var4 = new JMenuItem("Show Running Plugins");
      JMenuItem var5 = new JMenuItem("Rename Window");
      jMenu.add(var4);
      jMenu.add(var5);
      jMenu.addSeparator();
      jMenu.add(var3);
      this.getMenuBar().add(jMenu);
      var5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            String var2 = JOptionPane.showInputDialog(DSClient.this.getOwningFrame(), "Enter new title", "Rename Window", 3);
            if (var2 != null) {
               EventQueue.invokeLater(DSClient.this.new Title());
               DSClient.this.userTitle = var2;
            }

         }
      });
      JMenu var6 = new JMenu("Options");
      this.autoFocus = new JCheckBoxMenuItem("Focus On Activation");
      this.autoFocus.setSelected(true);
      this.debugItem = new JCheckBoxMenuItem("Debug mode", ImageManager.getIcon(DSConstants.Icon.DEBUG.getPath(), this.getLabelImageSize()), false);
      this.debugItem.setSelected(super.isDebugMode());
      var6.add(this.autoFocus);
      var6.addSeparator();
      var6.add(this.debugItem);
      this.getMenuBar().add(var6);
      var3.addActionListener(new QuitAction(this));
      var4.addActionListener(new DSClient.ShowRunningPluginsListAction());
      if (this.isDebugMode()) {
         final JMenu jMenu1 = new JMenu("Look And Feel");
         this.getMenuBar().add(jMenu1);
         SwingWorker var8 = new SwingWorker<List<Class<? extends LookAndFeel>>, Void>() {
            @Override
            protected List<Class<? extends LookAndFeel>> doInBackground() throws Exception {
               Vector vector = new Vector();
               vector.add(NimbusLookAndFeel.class);
               vector.add(MotifLookAndFeel.class);
               vector.add(WindowsLookAndFeel.class);
               vector.add(MetalLookAndFeel.class);
               vector.add(BasicLookAndFeel.class);
               vector.add(SynthLookAndFeel.class);
               Iterator iterator = JarFileClassInspector.getInspectors(DSClient.class.getClassLoader()).iterator();

               while(iterator.hasNext()) {
                  JarFileClassInspector var3 = (JarFileClassInspector)iterator.next();

                  try {
                     System.out.println(var3.toString());
                     vector.addAll(var3.getAllClassesExtendingClass(LookAndFeel.class));
                  } catch (Exception var5) {
                     var5.printStackTrace();
                  }
               }

               return vector;
            }

            @Override
            protected void done() {
               try {
                  List list = this.get();
                  Iterator iterator = list.iterator();

                  while(iterator.hasNext()) {
                     Class clazz = (Class)iterator.next();
                     JMenuItem jMenuItem = DSClient.this.createLookAndFeelSelector(clazz);
                     if (jMenuItem != null) {
                        jMenu1.add(jMenuItem);
                     }
                  }
               } catch (Throwable throwable) {
                  Logger.getLogger(DSClient.class.getName()).log(Level.SEVERE, null, throwable);
                  throwable.printStackTrace();
               }

            }
         };
         var8.execute();
      }

   }

   private JMenuItem createLookAndFeelSelector(final Class<? extends LookAndFeel> lf) {
      String lfSimpleName = lf.getSimpleName();

      try {
         LookAndFeel lookAndFeel = (LookAndFeel)lf.newInstance();
         lfSimpleName = lookAndFeel.getName();
         if (!lookAndFeel.isSupportedLookAndFeel()) {
            return null;
         }
      } catch (Exception e) {
         System.err.printf("Cannot create %s:  %s\n", lf.getSimpleName(), e.getMessage());
         return null;
      }

      final JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem(lfSimpleName);
      this.lnfButtons.add(jRadioButtonMenuItem);
      if (lf.equals(UIManager.getLookAndFeel().getClass())) {
         jRadioButtonMenuItem.setSelected(true);
      }

      jRadioButtonMenuItem.addActionListener(var1x -> {
         try {
            UIManager.setLookAndFeel(lf.getName());
            SwingUtilities.updateComponentTreeUI(DSClient.this.getOwningFrame());
         } catch (Exception var3) {
            var3.printStackTrace();
            DSClient.this.logEvent(Level.SEVERE, var3.getMessage(), var3);
         }

         jRadioButtonMenuItem.setSelected(true);
      });
      return jRadioButtonMenuItem;
   }

   public String getSystemLoggerName() {
      return "ds.core";
   }

   @Override
   public CommandSet getCommandSet() {
      return this.commands;
   }

   @Override
   public Logger getSystemLogger() {
      return this.systemLogger;
   }

   public void processCommandLine(String fullCommand, IdCallback idCallback, Object o, HostInfo hostInfo) throws DispatcherException {
      try {
         this.getDispatcherClient().startCommand(fullCommand, idCallback, o, hostInfo);
      } catch (DispatcherException dispatcherException) {
         this.logEvent(Level.SEVERE, "DispatcherException caught while formatting outgoing command", dispatcherException);
         throw dispatcherException;
      } catch (JAXBException jaxbException) {
         this.logEvent(Level.SEVERE, "JAXBException caught while formatting outgoing command", jaxbException);
         throw new DispatcherException("XML message formatting failed");
      } catch (IOException ioException) {
         this.logEvent(Level.SEVERE, "IOException caught while sending command", ioException);
         throw new DispatcherException("I/O exception");
      }
   }

   public String disconnect() {
      this.dispatcherClient.disconnect();
      return "Disconnecting...\n";
   }

   @Override
   public void requestShutdown() {
      if (this.isConnected()) {
         ConfirmExit var1 = new ConfirmExit(this.getOwningFrame(), true, this.executor);
         if (!var1.query()) {
            return;
         }
      }

      this.fullStop = true;

      try {
         this.dispatcherClient.sendShutdownNotification();
      } catch (DispatcherException var2) {
         this.shutdown(true);
      } catch (Exception e) {
         this.logEvent(Level.WARNING, "Exception while notifying Lp of shutdown\n" + e.getMessage(), e);
      }

   }

   @Override
   public void shutdown(boolean id) {
      this.quitHappened = true;
      if (!id) {
         this.setTitle(String.format("%s (Unexpected termination)", "DanderSpritz"));
      }

      if (this.fullStop) {
         this.execute(new DSClient.DoFullQuit());
      }

      super.stop();
   }

   public String status() {
      String s;
      if (this.dispatcherClient.isConnected()) {
         s = "Connected to " + this.dispatcherClient.toString() + "\n";
      } else {
         s = "Not connected\n";
      }

      return s;
   }

   @Override
   public void addPrefixesToTask(TaskId taskId, List<String> prefixes) throws DispatcherException {
      try {
         this.dispatcherClient.addPrefixes(taskId, prefixes);
      } catch (DispatcherException var4) {
         throw var4;
      } catch (JAXBException var5) {
         throw new DispatcherException("Command formatting error while trying to add prefixes to task " + taskId);
      } catch (IOException var6) {
         throw new DispatcherException("I/O Error while trying to add prefixes to task " + taskId);
      }
   }

   @Override
   public void stopCommandOutput(Task task) throws DispatcherException {
      try {
         this.dispatcherClient.stopCommandOutput(task.getId());
      } catch (DispatcherException var3) {
         throw var3;
      } catch (JAXBException var4) {
         throw new DispatcherException("Command formatting error while trying to stop task " + task.getId());
      } catch (IOException var5) {
         throw new DispatcherException("I/O Error while trying to stop command " + task.getId());
      }
   }

   @Override
   public void restartCommandOutput(Task task) throws DispatcherException {
      try {
         this.dispatcherClient.restartCommandOutput(task.getId());
      } catch (DispatcherException var3) {
         throw var3;
      } catch (JAXBException var4) {
         throw new DispatcherException("Command formatting error while trying to stop task " + task.getId());
      } catch (IOException var5) {
         throw new DispatcherException("I/O Error while trying to stop command " + task.getId());
      }
   }

   @Override
   public void killCommand(Task task) throws DispatcherException {
      this.killOrInterruptCommand(task, true);
   }

   @Override
   public void interruptCommand(Task task) throws DispatcherException {
      this.killOrInterruptCommand(task, false);
   }

   public void killOrInterruptCommand(Task task, boolean success) throws DispatcherException {
      try {
         if (success) {
            this.dispatcherClient.stopCommand(task.getId());
         } else {
            this.dispatcherClient.interruptCommand(task.getId());
         }

      } catch (DispatcherException var4) {
         throw var4;
      } catch (JAXBException var5) {
         throw new DispatcherException("Command formatting error while trying to kill or interrupt task " + task.getId());
      } catch (IOException var6) {
         throw new DispatcherException("I/O Error while trying to kill or interrupt command " + task.getId());
      }
   }

   @Override
   public void startCommand(String fullCommand, IdCallback idCallback, Object o, HostInfo hostInfo) throws DispatcherException {
      while(this.hosts.size() == 0) {
         try {
            TimeUnit.MILLISECONDS.sleep(20L);
         } catch (InterruptedException var6) {
            Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, var6);
         }
      }

      this.processCommandLine(fullCommand, idCallback, o, hostInfo);
   }

   @Override
   public void sendPromptReply(int reqId, TaskId taskId, String cmdValue) {
      try {
         this.dispatcherClient.sendPromptReply(reqId, taskId, cmdValue);
      } catch (DispatcherException var5) {
         this.logEvent(Level.WARNING, "DispatcherException caught while replying to prompt", var5);
      } catch (IOException var6) {
         this.logEvent(Level.WARNING, "DispatcherException caught while replying to prompt", var6);
      } catch (JAXBException var7) {
         this.logEvent(Level.WARNING, "DispatcherException caught while replying to prompt", var7);
      }

   }

   @Override
   public void requestHelpStatement(String helpType, HostInfo hostInfo) throws DispatcherException {
      try {
         this.dispatcherClient.requestHelpStatement(helpType, hostInfo);
      } catch (JAXBException var4) {
         throw new DispatcherException("Unable to create XML message for IPC with dispatcher");
      } catch (IOException var5) {
         throw new DispatcherException("Request for command help caused IOException");
      }
   }

   @Override
   public File createLogFile(String filenamePrefix) {
      return this.logManager.createLogFile(filenamePrefix);
   }

   @Override
   public boolean isLiveOperation() {
      return true;
   }

   @Override
   public void exceptionOccurredInPlugin(Plugin plugin) {
      this.logEvent(Level.SEVERE, "Fatal error in plugin: " + plugin.toString());
      this.getWorkbench().enqueAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{plugin});
   }

   @Override
   public <E extends Serializable> E getObject(Class<? extends Plugin> plugin, String var2, Class<? extends E> var3) {
      return null;
   }

   @Override
   public void lpConnectionTerminated() {
      super.lpConnectionTerminated();
   }

   @Override
   public Operation getOperation() {
      return this.liveOperation;
   }

   @Override
   public void offerOperation(Operation operation) {
      this.liveOperation = operation;
      this.fireConnectionChangeEvent(new OperationChanged(this, operation));
   }

   @Override
   public void applicationEnded(String reason) {
      if (!this.quitHappened) {
         JOptionPane.showMessageDialog(super.getOwningFrame(), "Command " + reason + " prematurely terminated.\nYou can expect no more output.", "Command terminated", 2);
         this.setTitle(String.format("%s (LP Prematurely Terminated)", "DanderSpritz"));
      }

      this.opState = OperationState.Inactive;
      this.fireConnectionChangeEvent(new LpTerminatedEvent(this));
   }

   @Override
   public Class<? extends Peer> getPeer() {
      return PeerServer.class;
   }

   @Override
   public int getDispatcherPort() {
      return this.dispatcherPort;
   }

   @Override
   public boolean isDebugMode() {
      return this.debugItem != null ? this.debugItem.isSelected() : false;
   }

   @Override
   public void setDebugMode(boolean var1) {
      if (this.debugItem != null) {
         this.debugItem.setSelected(var1);
      }

   }

   @Override
   public OperationState getOperationState() {
      return this.opState;
   }

   @Override
   public void disconnected(String id) {
   }

   @Override
   public void setCommandEnvironmentVariable(String var1, String var2, HostInfo var3) {
      String var4 = "-default";
      if (var3 != null) {
         var4 = String.format("-destination %s", var3.getId());
      }

      try {
         this.startCommand(String.format("lpsetenv -name \"_GUI_%s\" -value \"%s\" %s", var1.toUpperCase(), var2, var4), (IdCallback)null, (Object)null, this.getHostById("localhost"));
      } catch (Exception var6) {
         this.logEvent(Level.SEVERE, "Unable to set environment variable", var6);
      }

   }

   private class DoFullQuit implements Runnable {
      private DoFullQuit() {
      }

      @Override
      public void run() {
         DSClient.this.getWorkbench().stopAllPlugins();
         DSClient.this.saveUserConfig();
         Handler[] var1 = DSClient.this.systemLogger.getHandlers();
         Handler[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Handler var5 = var2[var4];
            DSClient.this.systemLogger.removeHandler(var5);
            var5.flush();
         }

         EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               DSClient.this.getMainWidget().setVisible(false);
            }
         });
      }

      // $FF: synthetic method
      DoFullQuit(Object var2) {
         this();
      }
   }

   private class InitializeStep implements Runnable {
      List<PluginInitInfo> plugins;

      InitializeStep(List<PluginInitInfo> plugins) {
         this.plugins = plugins;
         if (plugins != null && plugins.size() > 0) {
            DSClient.this.splash.setDisplayedText(String.format("Starting %s", (plugins.get(0)).getInstanceName()));
            DSClient.this.splash.setMinorProgress(0, DSClient.this.SPLASH_MINOR_STAGES, DSClient.this.SPLASH_MINOR_CURRENT++);
            DSClient.this.splash.setStageIcon((plugins.get(0)).getIcon());
         } else {
            DSClient.this.splash.setDisplayedText("Final Setup");
            DSClient.this.splash.setMinorProgress(0, DSClient.this.SPLASH_MINOR_STAGES, 0);
         }

      }

      @Override
      public void run() {
         if (this.plugins != null && this.plugins.size() != 0) {
            DSClient.this.instantiatePlugin(this.plugins.get(0));
            EventQueue.invokeLater(DSClient.this.new InitializeStep(this.plugins.subList(1, this.plugins.size())));
         } else {
            DSClient.this.getWorkbench().enqueAction(WorkbenchAction.SETSELECTEDINDEX, new Object[]{0});
            DSClient.this.setupMaintenanceTasks();
            synchronized(DSClient.this) {
               DSClient.this.notifyAll();
            }
         }

      }
   }

   public class Title implements Runnable {
      @Override
      public void run() {
         JFrame var1 = DSClient.this.getOwningFrame();
         String var2 = DSClient.this.getTitle();
         DSClient.this.logEvent(Level.INFO, String.format("Setting title to '%s'", var2));
         if (var1 != null) {
            var1.setTitle(var2);
         } else {
            DSClient.this.logEvent(Level.SEVERE, String.format("Somehow owning frame is null?!  ('%s')", var2));
         }

         DSClient.this.getWorkbench().titleChanged();
      }
   }

   private final class ShowRunningPluginsListAction implements ActionListener {
      private ShowRunningPluginsListAction() {
      }

      @Override
      public void actionPerformed(ActionEvent var1) {
         DSClient.this.getWorkbench().showRunningPluginsList();
      }

      // $FF: synthetic method
      ShowRunningPluginsListAction(Object var2) {
         this();
      }
   }
}
