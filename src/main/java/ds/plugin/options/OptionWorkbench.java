package ds.plugin.options;

import ddb.detach.Tabbable;
import ddb.detach.Workbench.RemoveTab;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ds.util.DszWorkbench;
import javax.swing.JPopupMenu;

public class OptionWorkbench extends DszWorkbench {
   public OptionWorkbench(CoreController var1, int var2, int var3) {
      super(var1.getSystemLogger(), var2, var3);
      this.init();
   }

   public OptionWorkbench(CoreController var1, int var2) {
      super(var1.getSystemLogger(), var2);
      this.init();
   }

   public OptionWorkbench(CoreController var1) {
      super(var1.getSystemLogger());
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, OptionWorkbench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return null;
   }

   protected class CloseTab extends RemoveTab {
      private CloseTab(Tabbable var2) {
         super( var2);
      }
   }
}
