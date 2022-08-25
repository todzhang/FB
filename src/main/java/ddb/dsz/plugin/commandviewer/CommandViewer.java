package ddb.dsz.plugin.commandviewer;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
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
@DszLogo("images/gkrellm2.png")
@DszName("Commands")
@DszDescription("Shows running commands")
public class CommandViewer extends MultipleTargetPlugin {
   public CommandViewer() {
      super.setName("Commands");
      super.setShowButtons(false);
   }

   protected int init3() {
      super.setShowButtons(false);
      super.setDisplay(super.tabWorkbench);
      super.tabWorkbench.setTabPlacement(2);
      return 0;
   }

   protected CommandViewerHost newHost(HostInfo var1) {
      return new CommandViewerHost(var1, this.core);
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.SHOW;
   }

   public String newItemName() {
      return "New Command List";
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new CommandViewerWorkBench(this);
   }

   public CommandEventDemultiplexor getDemultiplexor() {
      return this.demulti;
   }

   public CoreController getCore() {
      return this.core;
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var4 = var1.getMethod("main", var0.getClass());
      var4.invoke((Object)null, var0);
   }
}
