package ddb.dsz.plugin.targetdetails;

import ddb.detach.TabbablePopupListener;
import ddb.detach.WorkbenchPopupMenu;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import javax.swing.JPopupMenu;

public class TargetDetailsWorkbench extends MultipleTargetWorkbench {
   public TargetDetailsWorkbench(MultipleTargetPlugin var1) {
      super(var1);
   }

   @Override
   public JPopupMenu getMenu() {
      return new WorkbenchPopupMenu(this);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new TabbablePopupListener(this);
   }
}
