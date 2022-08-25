package ddb.dsz.plugin.taskmanager;

import ddb.detach.Tabbable;
import ddb.detach.TabbableOption;
import ddb.detach.TabbableStatus;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/taskmanager.png")
@DszName("Processes")
@DszDescription("Displays the current state of the target's processes")
public class TaskManager2 extends MultipleTargetPlugin {
   public static final int HIGHLIGHT_TIME = 60;
   static List<String> interestingCommands = new Vector();
   static TaskManagerOptions optionPane;

   public TaskManager2() {
      super.setName("Processes");
      super.setShowButtons(false);
   }

   public int init3() {
      optionPane.init(this.core);
      this.core.logEvent(Level.FINE, "Initializing TaskManager2");
      this.tabWorkbench.setTabPlacement(2);
      super.setDisplay(this.tabWorkbench);
      return 0;
   }

   protected void fini3() {
      Iterator i$ = this.tabWorkbench.getTabs().iterator();

      while(i$.hasNext()) {
         Tabbable tab = (Tabbable)i$.next();
         if (tab instanceof TargetProcessSpace) {
            TargetProcessSpace tps = (TargetProcessSpace)tab;
            tps.fini();
         }
      }

   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new TaskManagerWorkBench(this);
   }

   protected SingleTargetInterface newHost(HostInfo host) {
      return new TargetProcessSpace(host, this.core, this);
   }

   @Override
   public TabbableStatus getStatus() {
      return this.tabWorkbench.getStatus();
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.IGNORE;
   }

   public String newItemName() {
      return "New Processes";
   }

   @Override
   public TabbableOption getStaticOptions() {
      return optionPane;
   }

   public static void main(String[] args) throws Throwable {
      Class<?> live = Class.forName("ds.plugin.live.DSClientApp");
      Class<?> replay = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method m = live.getMethod("main", args.getClass());
      m.invoke((Object)null, args);
   }

   static {
      interestingCommands.add("processes");
      interestingCommands.add("processinfo");
      optionPane = new TaskManagerOptions();
   }
}
