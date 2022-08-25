package ds.plugin.replay;

import ddb.CheckThreadViolationRepaintManager;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.connection.events.StatisticsEvent;
import ddb.dsz.core.controller.CommandSet;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.host.MutableHostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.Plugin;
import ddb.util.GeneralUtilities;
import ddb.util.PluginInitInfo;
import ddb.util.StartupConfigParser;
import ddb.util.XMLException;
import ds.core.DSConstants;
import ds.core.controller.AbstractCoreController;
import ds.jaxb.ipc.Message;
import ds.plugin.peer.Peer;
import ds.plugin.peer.PeerClient;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

public class OpReplayDriver extends AbstractCoreController implements CoreController {
   CheckThreadViolationRepaintManager repaintManager = new CheckThreadViolationRepaintManager();
   public static String LOCAL = "0x00000004";
   public static String MONITOR = "0x00000010";
   public static String LOG = "0x00000020";
   public static String NOTIFY = "0x00000040";
   public static String BACKGROUND = "0x00000080";
   public static String TASK = "0x00000100";
   private final CommandSet commands = new CommandSet();
   private List<Plugin> plugins;
   boolean localMode;
   boolean loadPrevious;

   public OpReplayDriver(String[] var1) {
      super(var1);
      RepaintManager.setCurrentManager(this.repaintManager);
      this.plugins = new Vector();
      this.systemLogger = Logger.getLogger("ds.core");
      this.systemLogger.setLevel(Level.ALL);
      this.systemLogger.setUseParentHandlers(false);
      ReplayTableModel.getReplayModel().setAutoLoad(this.loadPrevious);
      File var3 = new File(this.getUserConfigDirectory(), "GuiSystemLog");
      var3.mkdirs();

      FileHandler var2;
      Calendar var4;
      try {
         var4 = Calendar.getInstance();
         var2 = new FileHandler(String.format("%s/%s-%%u.xml", var3.getAbsolutePath(), GeneralUtilities.CalendarToStringFile(var4)));
         var2.setLevel(Level.WARNING);
         this.systemLogger.addHandler(var2);
      } catch (Exception var6) {
         this.logEvent(Level.WARNING, var6.getMessage(), var6);
      }

      if (this.isDebugMode()) {
         try {
            var4 = Calendar.getInstance();
            var2 = new FileHandler(String.format("%s/%s-%%u.all.xml", var3.getAbsolutePath(), GeneralUtilities.CalendarToStringFile(var4)));
            var2.setLevel(Level.ALL);
            this.systemLogger.addHandler(var2);
         } catch (Exception var5) {
            this.logEvent(Level.WARNING, var5.getMessage(), var5);
         }
      }

   }

   @Override
   protected void parseArg(String var1, String var2) {
      super.parseArg(var1, var2);
      if (var1.equals("-local")) {
         try {
            this.localMode = Boolean.parseBoolean(var2);
         } catch (Exception var5) {
         }
      } else if (var1.equalsIgnoreCase("-loadPrevious") && var2 != null) {
         try {
            this.loadPrevious = Boolean.parseBoolean(var2);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   void fireCommandEvent(CommandEvent var1) {
      HostInfo var2 = this.getHostById(var1.getTargetAddress());
      Iterator var3 = this.plugins.iterator();

      while(true) {
         Plugin var4;
         do {
            if (!var3.hasNext()) {
               return;
            }

            var4 = (Plugin)var3.next();
         } while(!var4.caresAboutLocalEvents() && var2.isLocal());

         var4.commandEventReceived(var1);
      }
   }

   @Override
   public CommandSet getCommandSet() {
      return this.commands;
   }

   public void addPlugin(Plugin var1) {
      this.plugins.add(var1);
   }

   public static void main(final String[] var0) {
      DSConstants.InstallDefaults();
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            final OpReplayDriver var1 = new OpReplayDriver(var0);
            var1.initialize();
            JFrame var2 = new JFrame();
            var2.setDefaultCloseOperation(3);
            var2.setSize(new Dimension(800, 600));
            var2.setLayout(new BorderLayout());
            var1.setOwningFrame(var2);
            var2.setJMenuBar(var1.getMenuBar());
            var2.setTitle("DanderSpritz Replay");
            JComponent var3 = var1.getMainWidget();
            var2.add(var3);
            var2.addWindowListener(new WindowAdapter() {
               public void windowOpened(WindowEvent var1x) {
                  var1.setFocusOnDefaultElement();
               }
            });
            Vector var4 = new Vector();
            List var5 = DSConstants.getStartupConfigurationFiles(new File(var1.getResourceDirectory()), "replay");
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               File var7 = (File)var6.next();

               try {
                  var4.addAll(StartupConfigParser.parse(var7.toURI().toURL().openStream(), var1.getLogDir(), String.format("%s/%s", var1.getResourceDirectory(), var1.getDefaultPackage()), ""));
               } catch (XMLException var9) {
               } catch (IOException var10) {
               }
            }

            var1.instantiatePlugins(var4);
            var1.createDirectoryMonitor();
            SwingUtilities.updateComponentTreeUI(var2);
            var2.setVisible(true);
         }
      });
   }

   public void instantiatePlugins(List<PluginInitInfo> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         PluginInitInfo var3 = (PluginInitInfo)var2.next();
         this.instantiatePlugin(var3);
      }

      if (this.getWorkbench().getTabCount() > 0) {
         this.getWorkbench().setSelectedIndex(0);
      }

   }

   public void shutdown() {
      super.saveUserConfig();
      System.exit(0);
   }

   public Task generateTaskForCommand(String var1) {
      return null;
   }

   @Override
   public void startCommand(String var1, IdCallback idCallback, Object var3, HostInfo hostInfo) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public void addPrefixesToTask(TaskId var1, List<String> var2) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public void killCommand(Task task) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public void interruptCommand(Task task) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public void stopCommandOutput(Task task) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public void sendPromptReply(int var1, TaskId taskId, String var3) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   public TableModel getLogTableModel() {
      return null;
   }

   @Override
   public void requestHelpStatement(String var1, HostInfo hostInfo) throws DispatcherException {
      throw new DispatcherException("Not connected.  Replaying past operation.");
   }

   @Override
   public File createLogFile(String var1) {
      return null;
   }

   @Override
   public Logger getSystemLogger() {
      return this.systemLogger;
   }

   @Override
   public boolean isLiveOperation() {
      return false;
   }

   @Override
   public void restartCommandOutput(Task task) throws DispatcherException {
   }

   @Override
   public <E extends Serializable> E getObject(Class<? extends Plugin> plugin, String var2, Class<? extends E> var3) {
      return null;
   }

   @Override
   public void exceptionOccurredInPlugin(Plugin plugin) {
   }

   @Override
   public void shutdown(boolean id) {
   }

   @Override
   public void updateConnectionInfo(Message message) {
   }

   @Override
   public void setTitle(String var1) {
   }

   @Override
   public void updateStatistics(StatisticsEvent statisticsEvent) {
   }

   @Override
   public Class<? extends Peer> getPeer() {
      return PeerClient.class;
   }

   @Override
   public boolean hasConnected() {
      return false;
   }

   @Override
   public void disconnected(String id) {
      HostInfo var2 = this.getHostById(id);
      if (var2 instanceof MutableHostInfo) {
         MutableHostInfo var3 = (MutableHostInfo)var2;
         var3.setConnected(false);
         this.fireDisconnectedHost(var3);
      }

   }

   @Override
   public void requestShutdown() {
   }
}
