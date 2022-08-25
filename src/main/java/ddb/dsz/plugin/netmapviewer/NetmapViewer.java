package ddb.dsz.plugin.netmapviewer;

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
@DszLogo("images/agt_web.png")
@DszName("Network")
@DszDescription("Shows information on the local network")
public class NetmapViewer extends MultipleTargetPlugin {
   public NetmapViewer() {
      super.setName("Network");
      super.setShowButtons(false);
   }

   protected int init3() {
      super.setShowButtons(false);
      super.setDisplay(super.tabWorkbench);
      super.tabWorkbench.setTabPlacement(2);
      return 0;
   }

   protected SingleTargetInterface newHost(HostInfo var1) {
      return new NetmapViewerHost(var1, this.core);
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.IGNORE;
   }

   public String newItemName() {
      return "New Network Display";
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return true;
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
