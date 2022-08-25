package ddb.dsz.plugin.multitarget;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.TabbablePopupMenu;
import ddb.detach.Workbench;
import ddb.dsz.core.controller.CoreController;

public class MultipleTargetPopupListener extends TabbablePopupListener {
   protected MultipleTargetPlugin manager;
   protected CoreController core;

   public MultipleTargetPopupListener(Workbench var1, MultipleTargetPlugin var2, CoreController var3) {
      super(var1);
      this.core = var3;
      this.manager = var2;
   }

   public MultipleTargetPopupListener(Workbench var1) {
      super(var1);
   }

   public void setCore(CoreController var1) {
      this.core = var1;
   }

   public void setManager(MultipleTargetPlugin var1) {
      this.manager = var1;
   }

   protected TabbablePopupMenu getPopupMenu(Tabbable var1) {
      return new MultipleTargetTabPopupMenu((MultipleTargetWorkbench)MultipleTargetWorkbench.class.cast(this.workbench), this.manager, (SingleTargetInterface)SingleTargetInterface.class.cast(var1), this.core);
   }
}
