package ddb.dsz.plugin.monitor;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEventDemultiplexor;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import java.lang.reflect.Method;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/monitor.png")
@DszName("Monitor")
@DszDescription("Monitored Command Output")
@DszUserStartable(false)
public class MonitorWindow2 extends MultipleTargetPlugin {
   public static final String MONITOR_MAXIMUM = "-MonitorMaximumLines";

   public MonitorWindow2() {
      super.setName("Monitor");
      super.setShowButtons(false);
   }

   protected int init3() {
      super.setDisplay(super.tabWorkbench);
      super.tabWorkbench.setTabPlacement(2);
      return 0;
   }

   protected MonitorHost newHost(HostInfo hostInfo) {
      return new MonitorHost(hostInfo, this.core, this);
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.HIDE;
   }

   public String newItemName() {
      return "New Monitor";
   }

   protected boolean parseArgument3(String var1, String var2) {
      if (var1.equals("-MonitorMaximumLines") && var2 != null) {
         this.core.setOption(MonitorWindow2.class, "-MonitorMaximumLines", var2);
         return true;
      } else {
         return false;
      }
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new MonitorWorkBench(this);
   }

   public CommandEventDemultiplexor getDemultiplexor() {
      return this.demulti;
   }

   public CoreController getCore() {
      return this.core;
   }

   public static void main(String[] args) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Method var2 = var1.getMethod("main", String[].class);
      var2.invoke((Object)null, args);
   }
}
