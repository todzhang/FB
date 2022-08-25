package ddb.dsz.plugin.transfermonitor;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import java.lang.reflect.Method;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/download_manager.png")
@DszName("Transfer Monitor")
@DszDescription("Lists all 'GET' requests")
public class TransferMonitor extends MultipleTargetPlugin {
   public TransferMonitor() {
      super.setName("Transfer Monitor");
      super.setShowButtons(false);
   }

   protected int init3() {
      super.setDisplay(super.tabWorkbench);
      this.tabWorkbench.setTabPlacement(2);
      return 0;
   }

   protected SingleTargetInterface newHost(HostInfo var1) {
      return new TransferMonitorHost(var1, this.core);
   }

   protected LocalHostState getLocalHostState() {
      return this.core.isDebugMode() ? LocalHostState.SHOW : LocalHostState.IGNORE;
   }

   public String newItemName() {
      return "New Transfers";
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
