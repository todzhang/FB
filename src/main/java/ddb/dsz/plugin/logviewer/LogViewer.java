package ddb.dsz.plugin.logviewer;

import ddb.detach.TabbableOption;
import ddb.detach.TabbableStatus;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventAdapter;
import ddb.dsz.core.command.CommandEventDemultiplexor;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.events.OperationChanged;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.logviewer.gui.LogViewerWorkBench;
import ddb.dsz.plugin.logviewer.gui.SummaryPane;
import ddb.dsz.plugin.logviewer.gui.screenlog.ScreenLogPane;
import ddb.dsz.plugin.logviewer.gui.target.TargetLogspace;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/zoom.png")
@DszName("LogViewer")
@DszDescription("Enables the user to review executed commands")
public class LogViewer extends MultipleTargetPlugin {
   public static final String SUMMARY_ICON = "images/gkrellm2.png";
   public static final String RELOAD_ICON = "images/reload.png";
   public static final String LIST_ICON = "images/folder_man.png";
   public static final String DETAIL_ICON = "images/document2.png";
   public static final String SHOWN = "Shown";
   public static final String HIDDEN = "Hidden";
   public static final String INCREASE_FONT = "images/blue-plus.png";
   public static final String DECREASE_FONT = "images/blue-minus.png";
   private static LogViewerOptions optionPane = new LogViewerOptions();
   public static final String STOP_ICON = "images/player_stop.png";
   public static final String RUN_ICON = "images/player_play.png";
   public static final String PAUSE_ICON = "images/player_pause.png";
   public static final String SUCCESS_ICON = "images/button_ok.png";
   public static final String FAILURE_ICON = "images/error.png";
   public static final String TASKED_ICON = "images/player_end.png";
   public static final String KILLED_ICON = "images/yellowled.png";
   public static final String ZOOM_ICON = "images/zoom.png";
   public static final String PRINT_ICON = "images/print_printer.png";
   private SummaryPane summaryPane = new SummaryPane();
   private ScreenLogPane screenLogs;
   private boolean developerMode = false;
   public static final int DEFAULT_MAX_CHAR = 1000000;
   int maxCharacters = 1000000;

   public boolean isDeveloperMode() {
      return this.developerMode;
   }

   public LogViewer() {
      super.setName("LogViewer");
      super.setCareAboutLocalEvents(true);
      super.setShowButtons(false);
   }

   protected int init3() {
      optionPane.init(this.core);
      this.screenLogs = new ScreenLogPane(this.core);
      this.core.logEvent(Level.FINE, "Initializing LogViewer");
      super.tabWorkbench.setTabPlacement(2);
      super.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{this.summaryPane});
      super.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{this.screenLogs});
      this.demulti.addCommandEventListenerAll(new CommandEventAdapter() {
         @Override
         public void commandEventReceived(CommandEvent commandEvent) {
            LogViewer.this.summaryPane.commandEventReceived(commandEvent);
            Task t = LogViewer.this.core.getTaskById(commandEvent.getId());
            if (t != null) {
               LogViewer.optionPane.addCommand(t.getCommandName());
            }
         }
      });
      TargetLogspace invalid = this.createLogspace((HostInfo)null, false, true);
      invalid.setName("Invalid");
      invalid.setIgnoreHost(true);
      this.demulti.addCommandEventListenerAll(invalid);
      super.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{invalid});
      super.setDisplay(super.tabWorkbench);
      return 0;
   }

   protected SingleTargetInterface newHost(HostInfo host) {
      return this.createLogspace(host, true, false);
   }

   public TargetLogspace createLogspace(HostInfo host, boolean focus, boolean invalid) {
      TargetLogspace targetLogs = new TargetLogspace(host, this.core, this, invalid);
      Object obj1 = this.core.getOption(this, "Shown");
      Object obj2 = this.core.getOption(this, "Hidden");
      Collection hidden;
      Iterator i$;
      Object o;
      if (obj1 != null && obj1 instanceof Collection) {
         hidden = (Collection)Collection.class.cast(obj1);
         i$ = hidden.iterator();

         while(i$.hasNext()) {
            o = i$.next();
            targetLogs.addCommandName(o.toString(), Boolean.TRUE);
         }
      }

      if (obj2 != null && obj2 instanceof Collection) {
         hidden = (Collection)Collection.class.cast(obj2);
         i$ = hidden.iterator();

         while(i$.hasNext()) {
            o = i$.next();
            targetLogs.addCommandName(o.toString(), Boolean.FALSE);
         }
      }

      return targetLogs;
   }

   public int getMaxCharacters() {
      return this.maxCharacters;
   }

   protected boolean parseArgument3(String argument, String value) {
      if (argument.equalsIgnoreCase("-developer")) {
         this.developerMode = true;
         return true;
      } else {
         if (argument.equalsIgnoreCase("-maxChar") && value != null) {
            try {
               int temp = Integer.parseInt(value);
               if (temp > 0) {
                  this.maxCharacters = temp;
                  return true;
               }

               this.core.logEvent(Level.WARNING, "-maxChar requires a positive integer");
            } catch (NumberFormatException var4) {
               this.core.logEvent(Level.WARNING, String.format("-maxChar requires a positive integer [value=%s]", value), var4);
            }
         }

         return false;
      }
   }

   @Override
   public TabbableOption getStaticOptions() {
      return optionPane;
   }

   protected void fini3() {
      optionPane.fini();
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      if (connectionChangeEvent instanceof OperationChanged) {
         OperationChanged gc = (OperationChanged)OperationChanged.class.cast(connectionChangeEvent);
         this.summaryPane.setSessionId(gc.getOperation().getGuid().toString());
      }

   }

   @Override
   public TabbableStatus getStatus() {
      return super.tabWorkbench.getStatus();
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return true;
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new LogViewerWorkBench(this);
   }

   @Override
   public MultipleTargetWorkbench getWorkbench() {
      return this.tabWorkbench;
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.SHOW;
   }

   public String newItemName() {
      return "New LogViewer";
   }

   public static void main(String[] args) throws Throwable {
      Class<?> live = Class.forName("ds.plugin.live.DSClientApp");
      Class<?> replay = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method m = live.getMethod("main", args.getClass());
      m.invoke((Object)null, args);
   }

   public CommandEventDemultiplexor getDemultiplexor() {
      return this.demulti;
   }

   public CoreController getCore() {
      return this.core;
   }

   public static enum FONT_SIZE {
      INCREASE,
      DECREASE,
      RESET;
   }
}
