package ds.gui;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.TabbablePopupMenu;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;

public class PluginPopupListener extends TabbablePopupListener {
   CoreController core = null;

   public PluginPopupListener(PluginWorkbench var1) {
      super(var1);
   }

   public void setCoreController(CoreController var1) {
      this.core = var1;
   }

   protected TabbablePopupMenu getPopupMenu(Tabbable var1) {
      return var1 instanceof Plugin && this.workbench instanceof PluginWorkbench ? new PluginPopupMenu((Plugin)var1, (PluginWorkbench)this.workbench, this.core) : null;
   }
}
