package ddb.dsz.plugin.screenshot;

import ddb.detach.Tabbable;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.util.List;
import java.util.Vector;

public class ScreenShotWorkBench extends MultipleTargetWorkbench {
   List<ScreenShotHost> lists = new Vector();

   public ScreenShotWorkBench(ScreenShot var1) {
      super(var1);
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, ScreenShotWorkBench.CloseTab.class);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         ScreenShotWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         ScreenShotWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }
}
