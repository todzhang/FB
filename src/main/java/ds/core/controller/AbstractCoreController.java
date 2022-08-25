package ds.core.controller;

import ddb.CheckThreadViolationRepaintManager;
import ddb.actions.tabnav.NavigationDirection;
import ddb.detach.Alignment;
import ddb.detach.TabNavigationListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.connection.events.DisconnectEvent;
import ddb.dsz.core.connection.events.LpTerminatedEvent;
import ddb.dsz.core.connection.events.NewHostEvent;
import ddb.dsz.core.connection.events.StatisticsEvent;
import ddb.dsz.core.connection.events.ThrottleEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.CoreController.OperationState;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.peer.PeerReceiver;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import ddb.util.BlockingInputStream;
import ddb.util.GeneralUtilities;
import ddb.util.Guid;
import ddb.util.KeybindingConfigParser;
import ddb.util.PluginInitInfo;
import ddb.util.SplitPrintStream;
import ddb.util.UtilityConstants;
import ddb.util.XMLException;
import ds.core.ConfigurationStore;
import ds.core.DSClient;
import ds.core.DSConstants;
import ds.core.PopupPromptWindow;
import ds.core.StatusBar;
import ds.core.UserManager;
import ds.core.commanddispatcher.MultipleCommandDispatcherClient;
import ds.core.impl.HostInfoImpl;
import ds.core.impl.OperationImpl;
import ds.core.impl.task.TaskDatabase;
import ds.core.impl.task.TaskImpl;
import ds.core.internalcommands.InternalCommandCallbackAdapter;
import ds.core.pluginevents.PluginEvent;
import ds.core.pluginevents.PluginEventListener;
import ds.gui.PluginWorkbench;
import ds.plugin.peer.Peer;
import ds.plugin.replay.ReplayTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.event.EventListenerList;

public abstract class AbstractCoreController implements CoreController, MutableCoreController {
   RepaintManager repaintManager;
   private PluginWorkbench workbench;
   private EventListenerList listeners = new EventListenerList();
   private String opDir = ".";
   private String logDir = ".";
   private String resourceDir = ".";
   private String buildType = "release";
   private String userConfigDir;
   private ConfigurationStore configurationStore;
   protected ScheduledThreadPoolExecutor executor;
   protected Logger systemLogger;
   protected MultipleCommandDispatcherClient dispatcherClient;
   private boolean debug = false;
   private StatusBar statusBar;
   private JComponent mainWidget;
   private JMenuBar menuBar;
   private JFrame ownerFrame;
   private final Map<Task, PopupPromptWindow> taskToPrompt = new HashMap();
   private final List<MutableTask> allTasks = new Vector();
   private final Map<Integer, MutableTask> taskByTempId = new HashMap();
   protected final List<PluginInitInfo> autoloadPlugins = new Vector();
   protected final UserManager userManager;
   String[] ResourceDirectories = null;
   private final Map<Guid, Operation> opById = new HashMap();
   protected Peer mPeer = null;
   private String localhostAddress = "127.0.0.1";
   private static final AbstractCoreController.FilloutPattern[] fillouts = new AbstractCoreController.FilloutPattern[]{new AbstractCoreController.FilloutPattern("[Zz]?([0-9]+)", "z0.0.0."), new AbstractCoreController.FilloutPattern("[Zz]?([0-9]+\\.[0-9]+)", "z0.0."), new AbstractCoreController.FilloutPattern("[Zz]?([0-9]+\\.[0-9]+\\.[0-9]+)", "z0."), new AbstractCoreController.FilloutPattern("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)", "z")};
   protected final List<HostInfo> hosts = Collections.synchronizedList(new Vector());
   protected final Pattern localHostPattern = Pattern.compile("\\s*127\\.\\d*.\\d*.\\d*|localhost|0*1|[Zz]?0\\.0\\.0\\.1");
   protected String title = null;
   protected String userTitle = null;
   protected boolean fullStop = false;
   private int count = 0;
   Map<KeyStroke, String> keybindings = null;

   public void setRepaintManager(RepaintManager repaintManager) {
      this.repaintManager = repaintManager;
      if (this.repaintManager instanceof CheckThreadViolationRepaintManager) {
         ((CheckThreadViolationRepaintManager)CheckThreadViolationRepaintManager.class.cast(this.repaintManager)).setEnableChecking(this.debug);
      }

   }

   public AbstractCoreController(String[] args) {
      this.parseArgs(args);

      try {
         TaskImpl.DATABASE = new TaskDatabase(this);
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }

      Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
         try {
            throwable.printStackTrace();
            LogRecord logRecord = new LogRecord(Level.SEVERE, throwable.getMessage());
            logRecord.setLoggerName("UncaughtExceptionHandler");
            logRecord.setSourceMethodName("...");
            logRecord.setSourceClassName("...");
            logRecord.setThrown(throwable);
            if (AbstractCoreController.this.systemLogger != null) {
               AbstractCoreController.this.systemLogger.log(logRecord);
            } else {
               Logger.getLogger("ds.core");
            }
         } catch (Throwable t) {
            t.printStackTrace();
         }

      });

      try {
//         Calendar var2 = Calendar.getInstance();
         File var3 = new File(this.getUserConfigDirectory(), String.format("Output-%s", GeneralUtilities.CalendarToStringFile(Calendar.getInstance())));
         var3.mkdirs();
         SplitPrintStream var4 = new SplitPrintStream(new PrintStream[]{System.out, new PrintStream(new File(var3, "out"))});
         SplitPrintStream var5 = new SplitPrintStream(new PrintStream[]{System.err, new PrintStream(new File(var3, "err"))});
         System.setOut(var4);
         System.setErr(var5);
      } catch (Throwable t) {
         this.logEvent(Level.WARNING, "Failed to redirect output", t);
      }

      DSConstants.addLibraryPath(String.format("%s/ExternalLibraries/%s", this.getResourceDirectory(), DSConstants.getOsString()));
      this.executor = new ScheduledThreadPoolExecutor(15, UtilityConstants.createThreadFactory("DSZ Core Thread"));
      this.executor.setMaximumPoolSize(50);
      this.dispatcherClient = new MultipleCommandDispatcherClient(this);
      ReplayTableModel.getReplayModel().setCoreController(this);
      this.userManager = new UserManager(this);
      this.schedule(() -> {
         String[] var1 = new String[]{System.getenv("TEMP"), System.getenv("TMP")};
         String[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (var5 != null) {
               File var6 = new File(var5);
               File[] var7 = var6.listFiles(new FilenameFilter() {
                  @Override
                  public boolean accept(File var1, String var2) {
                     return var2.matches("\\+~JF[0-9]+\\.tmp");
                  }
               });
               int var8 = var7.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  File var10 = var7[var9];

                  try {
                     var10.delete();
                  } catch (Exception var12) {
                  }
               }
            }
         }

      }, 2L, TimeUnit.MINUTES);
   }

   @Override
   public List<String> getUserAliases(HostInfo hostInfo) {
      return this.userManager.usersByHost(hostInfo);
   }

   public final void initialize() {
      this.workbench = new PluginWorkbench(this, (PluginWorkbench)null, 1);
      this.workbench.setSelected(true);
      this.workbench.setAllowWrap(true);
      this.statusBar = new StatusBar();
      JPanel jPanel = new JPanel(new BorderLayout());
      jPanel.add(this.workbench, "Center");
      jPanel.add(this.statusBar, "South");
      this.mainWidget = jPanel;
      NavigationDirection.fill(new TabNavigationListener(), jPanel.getActionMap());
      jPanel.getInputMap(1).put(KeyStroke.getKeyStroke(37, 128), NavigationDirection.PREVIOUS.getName());
      jPanel.getInputMap(1).put(KeyStroke.getKeyStroke(39, 128), NavigationDirection.NEXT.getName());
      this.menuBar = new JMenuBar();
      this.initialize2();
   }

   protected void initialize2() {
   }

   public void createDirectoryMonitor() {
      this.dispatcherClient.addDirectoryMonitor(new File(this.getLogDir()));
   }

   protected final void parseArgs(String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         int var3 = var1[var2].lastIndexOf(61);
         if (var3 != -1) {
            String var4 = var1[var2].substring(0, var3);
            String var5 = var1[var2].substring(var3 + 1);
            this.parseArg(var4, var5);
         }
      }

   }

   protected void parseArg(String var1, String var2) {
      if (var1.equals("-opDir")) {
         this.setOpDir(var2);
      } else if (var1.equals("-logDir")) {
         this.setLogDir(var2);
      } else if (var1.equals("-comms")) {
         this.localhostAddress = var2;
      } else if (var1.equals("-resourceDir")) {
         this.setResourceDir(var2);
      } else if (var1.equals("-config")) {
         this.setUserConfigDirectory(var2);
      } else if (var1.equals("-debug")) {
         this.debug = Boolean.parseBoolean(var2);
      } else if (var1.equals("-threadDump") && var2.equals("true")) {
         this.newThread("Thread Dump", new Runnable() {
            @Override
            public void run() {
               (new File("Thread.dump")).delete();
               int var1 = 0;

               while(true) {
                  try {
                     FileOutputStream var2 = new FileOutputStream("Thread.dump", true);
                     PrintStream var3 = new PrintStream(var2);
                     Map var4 = Thread.getAllStackTraces();
                     var3.println("-------------------------------------------------------------");
                     var3.println(String.format("Thread Dump #%d", var1++));
                     var3.println("-------------------------------------------------------------");
                     int var5 = 0;
                     Iterator var6 = var4.keySet().iterator();

                     while(var6.hasNext()) {
                        Thread var7 = (Thread)var6.next();
                        var3.println(String.format("Thread %d: %s (%s)", var5++, var7.getName(), var7.getState().toString()));
                        StackTraceElement[] var8 = (StackTraceElement[])var4.get(var7);
                        int var9 = 0;
                        StackTraceElement[] var10 = var8;
                        int var11 = var8.length;

                        for(int var12 = 0; var12 < var11; ++var12) {
                           StackTraceElement var13 = var10[var12];
                           var3.println(String.format("\t%02d: %s:%s (%s: %d)", var9++, var13.getClassName(), var13.getMethodName(), var13.getFileName(), var13.getLineNumber()));
                        }
                     }

                     var3.close();
                     var2.close();
                  } catch (Exception var15) {
                     var15.printStackTrace();
                  }

                  try {
                     TimeUnit.SECONDS.sleep(300L);
                  } catch (Exception var14) {
                  }
               }
            }
         }).start();
      }

   }

   @Override
   public void execute(Runnable var1) {
      this.executor.execute(var1);
   }

   public boolean isTerminated() {
      return this.executor.isTerminated();
   }

   @Override
   public void addPluginEventListener(PluginEventListener pluginEventListener) {
      this.logEvent(Level.FINEST, "Registering PluginEventListener: " + pluginEventListener.toString());
      this.listeners.add(PluginEventListener.class, pluginEventListener);
   }

   @Override
   public void removePluginEventListener(PluginEventListener pluginEventListener) {
      this.logEvent(Level.FINEST, "Registering PluginEventListener: " + pluginEventListener.toString());
      this.listeners.remove(PluginEventListener.class, pluginEventListener);
   }

   protected void firePluginEvent(PluginEvent var1) {
      PluginEventListener[] var2 = (PluginEventListener[])this.listeners.getListeners(PluginEventListener.class);
      PluginEventListener[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         PluginEventListener var6 = var3[var5];

         try {
            var6.pluginEvent(var1);
         } catch (Throwable var8) {
            this.logEvent(Level.WARNING, "Unable to properly report plugin events", var8);
         }
      }

   }

   @Override
   public void addConnectionChangeListener(ConnectionChangeListener connectionChangeListener) {
      this.logEvent(Level.FINEST, "Registering ConnectionChangeListener: " + connectionChangeListener.toString());
      this.listeners.add(ConnectionChangeListener.class, connectionChangeListener);
   }

   @Override
   public void removeConnectionChangeListener(ConnectionChangeListener connectionChangeListener) {
      this.logEvent(Level.FINEST, "Unregistering ConnectionChangeListener: " + connectionChangeListener.toString());
      this.listeners.remove(ConnectionChangeListener.class, connectionChangeListener);
   }

   protected void fireConnectionChangeEvent(ConnectionChangeEvent var1) {
      ConnectionChangeListener[] var2 = (ConnectionChangeListener[])this.listeners.getListeners(ConnectionChangeListener.class);
      ConnectionChangeListener[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectionChangeListener var6 = var3[var5];

         try {
            var6.connectionChanged(var1);
         } catch (Throwable var8) {
            this.logEvent(Level.WARNING, "Unable to properly report connection change events", var8);
         }
      }

   }

   protected void fireStatisticsChanged(StatisticsEvent var1) {
      ConnectionChangeListener[] var2 = (ConnectionChangeListener[])this.listeners.getListeners(ConnectionChangeListener.class);
      ConnectionChangeListener[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectionChangeListener var6 = var3[var5];

         try {
            var6.connectionChanged(var1);
         } catch (Throwable var8) {
            this.logEvent(Level.WARNING, "Unable to properly report statistics events", var8);
         }
      }

   }

   protected void fireConnectionNewHost(HostInfo var1) {
      synchronized(this.allTasks) {
         Iterator var3 = this.allTasks.iterator();

         while(true) {
            if (!var3.hasNext()) {
               break;
            }

            MutableTask var4 = (MutableTask)var3.next();

            try {
               if (var4.getHost() != null && var4.getHost().getId().equals(var1.getId())) {
                  var4.setHost(var1);
               }
            } catch (NullPointerException var7) {
               var7.printStackTrace();
            }
         }
      }

      this.fireConnectionChangeEvent(new NewHostEvent(this, var1));
   }

   protected void fireDisconnectedHost(HostInfo var1) {
      this.fireConnectionChangeEvent(new DisconnectEvent(this, var1));
   }

   @Override
   public void fireThrottleEvent(ThrottleEvent throttleEvent) {
      this.fireConnectionChangeEvent(throttleEvent);
   }

   @Override
   public MultipleCommandDispatcherClient getDispatcherClient() {
      return this.dispatcherClient;
   }

   @Override
   public void setDispatcherClient(MultipleCommandDispatcherClient multipleCommandDispatcherClient) {
      this.dispatcherClient = multipleCommandDispatcherClient;
   }

   @Override
   public void addCommandEventListener(final CommandEventListener commandEventListener) {
      this.logEvent(Level.FINEST, "Registering CommandEventListener: " + commandEventListener.toString());
      this.dispatcherClient.addCommandEventListener(commandEventListener);
      this.submit(new Runnable() {
         @Override
         public void run() {
            AbstractCoreController.this.dispatcherClient.repeatTasks(commandEventListener);
         }
      });
   }

   @Override
   public void removeCommandEventListener(CommandEventListener commandEventListener) {
      this.logEvent(Level.FINEST, "Unregistering CommandEventListener: " + commandEventListener.toString());
      this.dispatcherClient.removeCommandEventListener(commandEventListener);
   }

   @Override
   public void lpConnectionTerminated() {
      this.fireConnectionChangeEvent(new LpTerminatedEvent(this));
   }

   @Override
   public void addInternalCommandHandler(InternalCommandHandler internalCommandHandler) {
      this.logEvent(Level.FINEST, "Registering InternalCommandHandler: " + internalCommandHandler.toString());
      this.listeners.add(InternalCommandHandler.class, internalCommandHandler);
   }

   @Override
   public void removeInternalCommandHandler(InternalCommandHandler internalCommandHandler) {
      this.logEvent(Level.FINEST, "Unregistering InternalCommandHandler: " + internalCommandHandler.toString());
      this.listeners.remove(InternalCommandHandler.class, internalCommandHandler);
   }

   @Override
   public boolean internalCommand(InternalCommandCallback internalCommandCallback, List<String> var2) {
      return this.internalCommand(internalCommandCallback, (TaskId)null, var2);
   }

   @Override
   public void sendGuiCommandResponse(int var1, boolean var2) {
      this.dispatcherClient.sendGuiCommandResponse(var1, var2);
   }

   public boolean internalCommand(InternalCommandCallback var1, TaskId var2, List<String> var3) {
      Level var4 = Level.INFO;
      if (var2 != null) {
         this.logEvent(var4, String.format("Command %d has issued a command:  '%s'", var2.getId(), var3.size() > 0 ? (String)var3.get(0) : "unknown"));
      } else {
         this.logEvent(var4, String.format("Command Issued:  '%s'", var3.size() > 0 ? (String)var3.get(0) : "unknown"));
      }

      if (var1 == null) {
         var1 = InternalCommandCallbackAdapter.INSTANCE;
      }

      InternalCommandHandler[] var5 = (InternalCommandHandler[])this.listeners.getListeners(InternalCommandHandler.class);

      for(int var6 = var5.length - 1; var6 >= 0; --var6) {
         if (var2 == null) {
            if (var5[var6].runInternalCommand(var3, (InternalCommandCallback)var1)) {
               this.logEvent(var4, String.format("Command '%s' succeeded on '%s'", var3, var5[var6].toString()));
               return true;
            }
         } else if (var5[var6].runInternalCommand(var3, var2, (InternalCommandCallback)var1)) {
            this.logEvent(var4, String.format("Command '%s' succeeded on '%s'", var3.size() > 0 ? (String)var3.get(0) : "unknown", var5[var6].toString()));
            return true;
         }
      }

      this.logEvent(var4, String.format("Command '%s' not handled", var3.size() > 0 ? (String)var3.get(0) : "unknown"));
      return false;
   }

   @Override
   public boolean internalCommand(InternalCommandCallback internalCommandCallback, String... var2) {
      return this.internalCommand(internalCommandCallback, Arrays.asList(var2));
   }

   @Override
   public void pluginStarted(Plugin plugin) {
      this.firePluginEvent(new PluginEvent(plugin, PluginEvent.PluginEventState.START));
   }

   @Override
   public void pluginStopped(Plugin plugin) {
      this.firePluginEvent(new PluginEvent(plugin, PluginEvent.PluginEventState.STOP));
   }

   public void setLogDir(String var1) {
      this.logDir = (new File(var1)).getAbsolutePath();
   }

   public String getLogDir() {
      return this.logDir;
   }

   public void setResourceDir(String var1) {
      this.resourceDir = (new File(var1)).getAbsolutePath();
   }

   @Override
   public String getLogDirectory() {
      return this.logDir;
   }

   @Override
   public String getResourceDirectory() {
      return this.resourceDir;
   }

   @Override
   public String getUserConfigDirectory() {
      return this.userConfigDir;
   }

   public void setBuildType(String var1) {
      this.buildType = var1;
   }

   @Override
   public String getBuildType() {
      return this.buildType;
   }

   public void setUserConfigDirectory(String var1) {
      this.userConfigDir = (new File(var1)).getAbsolutePath();
   }

   @Override
   public String getOpDir() {
      return this.opDir;
   }

   public void setOpDir(String var1) {
      this.opDir = var1;
   }

   public ConfigurationStore getConfigurationStore() {
      if (this.configurationStore == null) {
         this.configurationStore = new ConfigurationStore(this.userConfigDir, this);
      }

      return this.configurationStore;
   }

   public void saveUserConfig() {
      this.getConfigurationStore().commitSettings();
   }

   @Override
   public void setOption(Class<? extends Plugin> plugin, String var2, Object var3) {
      this.getConfigurationStore().setOption(plugin, var2, var3);
   }

   @Override
   public void setOption(Plugin plugin, String var2, Object var3) {
      this.getConfigurationStore().setOption(plugin, var2, var3);
   }

   @Override
   public Object getOption(Class<? extends Plugin> plugin, String var2) {
      return this.getOption((Class) plugin, var2, (Object)null);
   }

   @Override
   public Object getOption(Plugin plugin, String var2) {
      return this.getOption((Plugin) plugin, var2, (Object)null);
   }

   @Override
   public Object getOption(Class<? extends Plugin> plugin, String var2, Object var3) {
      Object var4 = this.getConfigurationStore().getOption(plugin, var2);
      return var4 == null ? var3 : var4;
   }

   @Override
   public Object getOption(Plugin plugin, String var2, Object var3) {
      Object var4 = this.getConfigurationStore().getOption(plugin, var2);
      return var4 == null ? var3 : var4;
   }

   @Override
   public void setObject(Class<? extends Plugin> plugin, String var2, Serializable var3) {
      try {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         ObjectOutputStream var5 = new ObjectOutputStream(var4);
         var5.writeObject(var3);
         this.setOption((Class) plugin, var2, var4.toByteArray());
      } catch (Throwable var6) {
         this.logEvent(Level.WARNING, "Unable to save theme", var6);
      }

   }

   @Override
   public void setObject(Plugin plugin, String var2, Serializable var3) {
      this.setObject(plugin.getClass(), var2, var3);
   }

   @Override
   public <E extends Serializable> E getObject(Class<? extends Plugin> plugin, String var2, Class<? extends E> var3) {
      Object var4 = this.getOption(plugin, var2);
      if (var4 instanceof byte[]) {
         try {
            ByteArrayInputStream var5 = new ByteArrayInputStream((byte[])byte[].class.cast(var4));
            ObjectInputStream var6 = new ObjectInputStream(var5);
            Object var7 = var6.readObject();
            if (var3.isInstance(var7)) {
               return (E) var3.cast(var7);
            }
         } catch (IOException var8) {
            var8.printStackTrace();
         } catch (ClassNotFoundException var9) {
            var9.printStackTrace();
         }
      }

      return null;
   }

   @Override
   public <E extends Serializable> E getObject(Plugin plugin, String var2, Class<? extends E> var3) {
      return this.getObject(plugin.getClass(), var2, var3);
   }

   @Override
   public void commitSettings() {
      this.getConfigurationStore().commitSettings();
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long var2, TimeUnit timeUnit) {
      return this.executor.schedule(callable, var2, timeUnit);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable runnable, long var2, TimeUnit timeUnit) {
      return this.executor.schedule(runnable, var2, timeUnit);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long var2, long var4, TimeUnit timeUnit) {
      return this.executor.scheduleAtFixedRate(runnable, var2, var4, timeUnit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long var2, long var4, TimeUnit timeUnit) {
      return this.executor.scheduleWithFixedDelay(runnable, var2, var4, timeUnit);
   }

   @Override
   public <T> Future<T> submit(Callable<T> callable) {
      return this.executor.submit(callable);
   }

   @Override
   public <T> Future<T> submit(Runnable runnable, T var2) {
      return this.executor.submit(runnable, var2);
   }

   @Override
   public Future<?> submit(Runnable runnable) {
      return this.executor.submit(runnable);
   }

   @Override
   public Operation getOperation() {
      return Operation.NULL;
   }

   @Override
   public void offerOperation(Operation operation) {
   }

   @Override
   public synchronized Thread newThread(Runnable var1) {
      return this.newThread(String.format("Thread %d", this.count++), var1);
   }

   @Override
   public synchronized Thread newThread(String var1, Runnable var2) {
      Thread var3 = new Thread(var2);
      var3.setDaemon(true);
      var3.setPriority(1);
      var3.setName(var1);
      return var3;
   }

   @Override
   public final void logEvent(Level level, String var2) {
      this.logEvent(level, (String)null, var2, (Throwable)null);
   }

   @Override
   public final void logEvent(Level level, String var2, String var3) {
      this.logEvent(level, var2, var3, (Throwable)null);
   }

   @Override
   public final void logEvent(Level level, String var2, Throwable throwable) {
      this.logEvent(level, (String)null, var2, throwable);
   }

   @Override
   public final void logEvent(Level level, String var2, String var3, Throwable throwable) {
      if (level.intValue() >= Level.WARNING.intValue()) {
         System.out.printf("%s:  %s\n", level, var3);
         if (throwable != null) {
            throwable.printStackTrace();
         }
      }

      LogRecord var5 = new LogRecord(level, var3);
      if (this.isDebugMode() || level.intValue() >= Level.WARNING.intValue()) {
         StackTraceElement[] var6 = Thread.currentThread().getStackTrace();

         for(int var7 = 2; var7 < var6.length; ++var7) {
            StackTraceElement var8 = var6[var7];
            boolean var9 = false;
            String[] var10 = new String[]{"logEvent", "invoke0", "invoke"};
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               String var13 = var10[var12];
               if (var8.getMethodName().equals(var13)) {
                  var9 = true;
                  break;
               }
            }

            if (!var9) {
               var5.setSourceClassName(var8.getClassName());
               var5.setSourceMethodName(var8.getMethodName());
               var5.setResourceBundleName(var8.getFileName());
               if (var2 == null) {
                  try {
                     var2 = Class.forName(var8.getFileName()).getSimpleName();
                  } catch (Exception var15) {
                  }
               }

               if (var2 == null) {
                  try {
                     Class var16 = Class.forName(var8.getClassName());
                     if (var16.isAnonymousClass()) {
                        var2 = var8.getClassName();
                     } else {
                        var2 = var16.getSimpleName();
                     }
                  } catch (Exception var14) {
                  }
               }

               if (var2 == null) {
                  if (var8.getFileName() == null) {
                     var2 = var8.getClassName();
                  } else if (var8.getFileName().lastIndexOf(46) > -1) {
                     var2 = var8.getFileName().substring(0, var8.getFileName().lastIndexOf(46));
                  }
               }
               break;
            }
         }
      }

      var5.setLoggerName(var2);
      var5.setThreadID(Long.valueOf(Thread.currentThread().getId()).intValue());
      var5.setThrown(throwable);
      if (this.systemLogger != null) {
         this.systemLogger.log(var5);
      } else if (throwable != null) {
         throwable.printStackTrace();
      }

   }

   protected final void instantiatePlugin(PluginInitInfo pluginInitInfo) {
      try {
         Class clazz = Class.forName(pluginInitInfo.getClassName(), false, DSConstants.getClassLoader());
         Plugin plugin = this.workbench.startPlugin(clazz, pluginInitInfo.getInstanceName(), pluginInitInfo.getInitArgs(), Alignment.getAlignment(pluginInitInfo.getAlign()), pluginInitInfo.getIcon(), true);
         if (plugin == null) {
            this.logEvent(Level.WARNING, "Failed to add plugin of class" + clazz);
         } else {
            plugin.setIdentifier(pluginInitInfo.getIdentifier());
         }

         if (pluginInitInfo.isDetached()) {
            this.workbench.enqueAction(PluginWorkbench.PluginWorkbenchAction.DETACHPLUGIN, new Object[]{plugin, pluginInitInfo.getFrameSize(), pluginInitInfo.getFramePosition()});
         } else {
            this.workbench.enqueAction(PluginWorkbench.PluginWorkbenchAction.PREDETACHPLUGIN, new Object[]{plugin, pluginInitInfo.getFrameSize(), pluginInitInfo.getFramePosition()});
         }

         if (!pluginInitInfo.isVisible()) {
            this.workbench.enqueAction(WorkbenchAction.HIDETAB, new Object[]{plugin});
         }
      } catch (Throwable throwable) {
         System.err.println("Unable to instantiate " + pluginInitInfo.getClassName());
         throwable.printStackTrace();
      }

   }

   @Override
   public PluginWorkbench getWorkbench() {
      return this.workbench;
   }

   @Override
   public void unhidePlugin(Plugin plugin) {
      this.workbench.enqueAction(WorkbenchAction.UNHIDETAB, new Object[]{plugin});
   }

   @Override
   public void hidePlugin(Plugin plugin) {
      this.workbench.enqueAction(WorkbenchAction.HIDETAB, new Object[]{plugin});
   }

   @Override
   public void detachPlugin(Plugin plugin, Dimension dimension, Point point) {
      this.workbench.enqueAction(WorkbenchAction.DETACHTAB, new Object[]{plugin, dimension, point});
   }

   @Override
   public boolean allowNewInstance(Class<?> var1) {
      return var1 == null ? false : this.workbench.allowNewInstance(var1);
   }

   @Override
   public boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5) {
      return this.startNewPlugin(var1, var2, var3, var4, var5, Alignment.DEFAULT);
   }

   @Override
   public boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5, Alignment alignment) {
      Plugin var7 = null;

      try {
         var7 = this.workbench.startPlugin(var1, var2, var3, alignment, (String)null, var5);
      } catch (Exception var9) {
         this.systemLogger.logp(Level.WARNING, DSClient.class.getName(), "startNewPlugin", var9.getMessage());
         return false;
      }

      if (var7 == null) {
         this.logEvent(Level.WARNING, "Failed to add plugin of class" + var1);
         return false;
      } else {
         if (var4) {
            this.workbench.enqueAction(WorkbenchAction.DETACHTAB, new Object[]{var7, null, null});
         }

         return true;
      }
   }

   @Override
   public void closePlugin(Plugin plugin) {
      if (plugin.isClosable()) {
         this.workbench.enqueAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{plugin});
      }

   }

   public void setFocusOnDefaultElement() {
      this.workbench.setFocusOnDefaultElement();
   }

   public StatusBar getStatusBar() {
      return this.statusBar;
   }

   public JComponent getMainWidget() {
      return this.mainWidget;
   }

   public JMenuBar getMenuBar() {
      return this.menuBar;
   }

   public void setOwningFrame(JFrame var1) {
      this.ownerFrame = var1;
   }

   public JFrame getOwningFrame() {
      return this.ownerFrame;
   }

   @Override
   public final Task getTaskById(TaskId taskId) {
      if (taskId != TaskId.NULL && taskId != TaskId.UNINITIALIZED_ID) {
         if (taskId.getTask() != null) {
            return taskId.getTask();
         } else {
            return taskId.getId() == 0 ? null : null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Operation getOperationById(Guid guid) {
      Object var2;
      synchronized(this.opById) {
         var2 = (Operation)this.opById.get(guid);
      }

      if (var2 == null) {
         var2 = OperationImpl.GenerateOperation(guid);
      }

      return (Operation)var2;
   }

   @Override
   public Operation getOperationById(BigInteger id) {
      return this.getOperationById(Guid.GenerateGuid(id));
   }

   @Override
   public void addOperation(Operation operation) {
      if (operation != null) {
         synchronized(this.opById) {
            this.opById.put(operation.getGuid(), operation);
         }
      }
   }

   @Override
   public final Collection<Operation> getOperationList() {
      Vector var1 = new Vector();
      synchronized(this.opById) {
         var1.addAll(this.opById.values());
         return var1;
      }
   }

   @Override
   public final Collection<Task> getTaskList() {
      Vector var1 = new Vector();
      synchronized(this.allTasks) {
         var1.addAll(this.allTasks);
         return var1;
      }
   }

   @Override
   public final Task getRunningTaskById(TaskId taskId) {
      Task var2 = taskId.getTask();
      if (var2 == null) {
         var2 = this.getTaskById(taskId);
         if (var2 == null) {
            return null;
         }
      }

      return var2.isAlive() ? var2 : null;
   }

   @Override
   public final Task getTaskByTaskId(Guid guid) {
      if (guid == null) {
         return null;
      } else {
         synchronized(this.allTasks) {
            Iterator var3 = this.allTasks.iterator();

            MutableTask var4;
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               var4 = (MutableTask)var3.next();
            } while(var4.getTaskId() == null || !var4.getTaskId().equals(guid));

            return var4;
         }
      }
   }

   @Override
   public final Task getTaskByTempId(int id) {
      synchronized(this.taskByTempId) {
         return (Task)this.taskByTempId.get(id);
      }
   }

   @Override
   public final void addNewTask(MutableTask mutableTask) {
      if (mutableTask.getId().equals(TaskId.UNINITIALIZED_ID)) {
         synchronized(this.taskByTempId) {
            this.taskByTempId.put(mutableTask.getTempId(), mutableTask);
         }
      } else {
         synchronized(this.allTasks) {
            this.allTasks.add(mutableTask);
         }
      }

   }

   @Override
   public void registerTaskId(MutableTask mutableTask) {
   }

   @Override
   public Task registerId(int id, TaskId taskId) {
      MutableTask mutableTask;
      synchronized(this.taskByTempId) {
         mutableTask = (MutableTask)this.taskByTempId.get(id);
         this.taskByTempId.remove(id);
      }

      if (mutableTask == null) {
         return null;
      } else {
         mutableTask.setId(taskId);
         synchronized(this.allTasks) {
            this.allTasks.add(mutableTask);
            return mutableTask;
         }
      }
   }

   public final Collection<TaskId> getTaskIdList() {
      Vector var1 = new Vector();
      synchronized(this.allTasks) {
         Iterator var3 = this.allTasks.iterator();

         while(var3.hasNext()) {
            MutableTask var4 = (MutableTask)var3.next();
            var1.add(var4.getId());
         }

         return var1;
      }
   }

   protected final void setLocalMode(boolean var1) {
   }

   @Override
   public final boolean isLocalMode() {
      return false;
   }

   public void setDebugMode(boolean var1) {
      this.debug = var1;
      if (this.repaintManager instanceof CheckThreadViolationRepaintManager) {
         ((CheckThreadViolationRepaintManager)CheckThreadViolationRepaintManager.class.cast(this.repaintManager)).setEnableChecking(this.debug);
      }

   }

   @Override
   public boolean isDebugMode() {
      return this.debug;
   }

   @Override
   public Map<KeyStroke, String> getKeyBindings() {
      synchronized(this) {
         if (this.keybindings == null) {
            try {
               this.keybindings = KeybindingConfigParser.parse(DSConstants.getClassLoader().getResourceAsStream("keybindings.xml"));
            } catch (XMLException var4) {
               this.logEvent(Level.SEVERE, var4.getMessage(), (Throwable)var4);
               return null;
            }
         }
      }

      return this.keybindings;
   }

   @Override
   public void setupKeyBindings(JComponent jComponent) {
      Map var2 = this.getKeyBindings();
      InputMap var3 = jComponent.getInputMap();
      Set var4 = var2.keySet();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         KeyStroke var6 = (KeyStroke)var5.next();
         String var7 = (String)var2.get(var6);
         var3.put(var6, var7);
      }

   }

   @Override
   public String claimPrompt(Task task) {
      synchronized(this.taskToPrompt) {
         PopupPromptWindow var3 = (PopupPromptWindow)this.taskToPrompt.get(task);
         if (var3 != null && var3.isVisible()) {
            this.taskToPrompt.remove(task);
            var3.setVisible(false);
            var3.dispose();
            return var3.getPromptText();
         } else {
            return null;
         }
      }
   }

   @Override
   public void stopPrompt(Task task) {
      synchronized(this.taskToPrompt) {
         PopupPromptWindow var3 = (PopupPromptWindow)this.taskToPrompt.get(task);
         if (var3 != null && var3.isVisible()) {
            var3.setVisible(false);
            var3.dispose();
            this.taskToPrompt.remove(task);
         }

      }
   }

   @Override
   public void showPrompt(Task task, int var2, String var3) {
      synchronized(this.taskToPrompt) {
         PopupPromptWindow var5 = (PopupPromptWindow)this.taskToPrompt.get(task);
         if (var5 != null && var5.isVisible()) {
            var5.setVisible(false);
            var5.dispose();
         }

         var5 = new PopupPromptWindow(this, task, var2, var3);
         this.taskToPrompt.put(task, var5);
         var5.setVisible(true);
      }
   }

   @Override
   public void taskEnded(Task task) {
      if (task != null) {
         synchronized(this.taskToPrompt) {
            PopupPromptWindow var3 = (PopupPromptWindow)this.taskToPrompt.remove(task);
            if (var3 != null) {
               var3.setVisible(false);
               var3.dispose();
               this.taskToPrompt.remove(task);
            }

         }
      }
   }

   @Override
   public String[] getResourcePackages() {
      synchronized(this) {
         if (this.ResourceDirectories == null) {
            Vector var2 = new Vector();
            var2.add("Ops");
            var2.add("Dsz");
            var2.add(".");
            File var3 = new File(this.getResourceDirectory());
            if (var3.exists()) {
               File[] var4 = var3.listFiles();
               if (var4 != null) {
                  File[] var5 = var4;
                  int var6 = var4.length;

                  for(int var7 = 0; var7 < var6; ++var7) {
                     File var8 = var5[var7];
                     if (var8.isDirectory() && !var2.contains(var8.getName())) {
                        var2.add(var8.getName());
                     }
                  }
               }
            }

            this.ResourceDirectories = (String[])var2.toArray(new String[var2.size()]);
         }
      }

      return this.ResourceDirectories;
   }

   @Override
   public int requestStatistics() {
      try {
         return this.dispatcherClient.requestStatistics();
      } catch (Exception var2) {
         this.logEvent(Level.FINE, var2.getMessage(), (Throwable)var2);
         return -1;
      }
   }

   @Override
   public boolean remove(Runnable runnable) {
      return this.executor.remove(runnable);
   }

   @Override
   public String getDefaultPackage() {
      return "Dsz";
   }

   @Override
   public void StealFocus(Plugin plugin) {
      this.workbench.enqueAction(WorkbenchAction.STEALFOCUS, new Object[]{plugin});
   }

   @Override
   public void applicationEnded(String reason) {
   }

   public abstract Class<? extends Peer> getPeer();

   @Override
   public void addPeerReceiver(PeerReceiver peerReceiver) {
      this.listeners.add(PeerReceiver.class, peerReceiver);
   }

   @Override
   public void removePeerReceiver(PeerReceiver peerReceiver) {
      this.listeners.remove(PeerReceiver.class, peerReceiver);
   }

   @Override
   public PeerTransferStatus sendMessageToPeer(String var1) {
      return this.sendMessageToPeer(var1, (PeerTag)null);
   }

   @Override
   public PeerTransferStatus sendMessageToPeer(String var1, PeerTag peerTag) {
      return this.mPeer != null ? this.mPeer.sendMessage(var1, peerTag) : PeerTransferStatus.DESTINATION_NOT_FOUND;
   }

   @Override
   public void firePeerConnected(PeerTag peerTag) {
      PeerReceiver[] var2 = (PeerReceiver[])this.listeners.getListeners(PeerReceiver.class);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PeerReceiver var5 = var2[var4];

         try {
            var5.newConnection(peerTag);
         } catch (Exception e) {
            this.logEvent(Level.FINER, e.getMessage(), e);
         }
      }

   }

   @Override
   public void fireReceivedMessage(String message, PeerTag peerTag) {
      PeerReceiver[] listeners = this.listeners.getListeners(PeerReceiver.class);
      int var4 = listeners.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         PeerReceiver var6 = listeners[var5];

         try {
            var6.receivedMessage(message, peerTag);
         } catch (Exception e) {
            this.logEvent(Level.FINER, e.getMessage(), (Throwable)e);
         }
      }

   }

   @Override
   public void registerPeer(Peer peer) {
      this.mPeer = peer;
   }

   @Override
   public void firePeerDisconnected(PeerTag peerTag) {
      PeerReceiver[] var2 = (PeerReceiver[])this.listeners.getListeners(PeerReceiver.class);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PeerReceiver var5 = var2[var4];

         try {
            var5.closedConnection(peerTag);
         } catch (Exception var7) {
            this.logEvent(Level.FINER, var7.getMessage(), (Throwable)var7);
         }
      }

   }

   @Override
   public List<HostInfo> getHosts() {
      Vector var1 = new Vector();
      var1.addAll(this.hosts);
      return var1;
   }

   @Override
   public HostInfo getHostById(String var1) {
      if (var1 == null) {
         return this.getHostById("localhost");
      } else {
         AbstractCoreController.FilloutPattern[] var2 = fillouts;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            AbstractCoreController.FilloutPattern var5 = var2[var4];
            var1 = var5.fillout(var1);
         }

         Matcher var6 = this.localHostPattern.matcher(var1);
         boolean var7 = false;
         if (var6.matches()) {
            var7 = true;
         }

         Iterator var8 = this.getHosts().iterator();

         HostInfo var9;
         do {
            if (!var8.hasNext()) {
               return new HostInfoImpl(var1, false);
            }

            var9 = (HostInfo)var8.next();
         } while((!var7 || !var9.isLocal()) && !var1.equalsIgnoreCase(var9.getId()));

         return var9;
      }
   }

   @Override
   public boolean hasConnected() {
      Iterator var1 = this.getHosts().iterator();

      HostInfo var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (HostInfo)var1.next();
      } while(var2.isLocal());

      return true;
   }

   public boolean isConnected() {
      Iterator var1 = this.getHosts().iterator();

      HostInfo var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (HostInfo)var1.next();
      } while(var2.isLocal() || !var2.isConnected());

      return true;
   }

   @Override
   public int getDispatcherPort() {
      return -1;
   }

   @Override
   public String getLocalhostAddress() {
      return this.localhostAddress;
   }

   @Override
   public HostInfo addHostInfo(HostInfo hostInfo) {
      if (hostInfo == null) {
         return new HostInfoImpl("", true);
      } else {
         synchronized(this.hosts) {
            Iterator var3 = this.hosts.iterator();

            while(true) {
               if (!var3.hasNext()) {
                  this.hosts.add(hostInfo);
                  break;
               }

               HostInfo var4 = (HostInfo)var3.next();
               if (var4.getId().equals(hostInfo.getId())) {
                  return var4;
               }
            }
         }

         this.fireConnectionNewHost(hostInfo);
         return hostInfo;
      }
   }

   @Override
   public OperationState getOperationState() {
      return OperationState.Inactive;
   }

   @Override
   public String getTitle() {
      String var1 = this.title;
      if (this.userTitle != null) {
         var1 = var1 + " - " + this.userTitle;
      }

      return var1;
   }

   @Override
   public void setTitle(String var1) {
      this.title = var1;
   }

   @Override
   public boolean isFullStop() {
      return this.fullStop;
   }

   @Override
   public String translate(String var1) {
      return var1.toUpperCase();
   }

   public void stop() {
      BlockingInputStream.setEnded();
   }

   @Override
   public List<TaskId> getTaskChildren(TaskId taskId) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.getTaskList().iterator();

      while(var3.hasNext()) {
         Task var4 = (Task)var3.next();
         if (var4.getParentId().equals(taskId)) {
            var2.add(var4.getId());
         }
      }

      return var2;
   }

   @Override
   public Dimension getLabelImageSize() {
      return DSConstants.getLabelImageSize();
   }

   @Override
   public Dimension getTabImageSize() {
      return DSConstants.getTabImageSize();
   }

   @Override
   public void setCommandEnvironmentVariable(String var1, String var2) {
      this.setCommandEnvironmentVariable(var1, var2, (HostInfo)null);
   }

   @Override
   public void setCommandEnvironmentVariable(String var1, String var2, HostInfo var3) {
   }

   private static class FilloutPattern {
      Pattern pattern;
      String start;

      public FilloutPattern(String var1, String var2) {
         this.pattern = Pattern.compile(var1);
         this.start = var2;
      }

      public String fillout(String var1) {
         Matcher var2 = this.pattern.matcher(var1);
         return !var2.matches() ? var1 : String.format("%s%s", this.start, var2.group(1));
      }
   }
}
