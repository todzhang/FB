package ds.plugin.peer;

import ddb.detach.Tabbable;
import ddb.detach.Workbench.RemoveTab;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ds.gui.PluginWorkbench;
import javax.swing.JPopupMenu;

public class PeerWorkbench extends PluginWorkbench {
   private final Peer _plugin;

   public PeerWorkbench(Peer var1, CoreController var2, int var3, int var4) {
      super(var2, var1.getParentWorkbench(), var3, var4);
      this._plugin = var1;
      this.init();
   }

   public PeerWorkbench(Peer var1, CoreController var2, int var3) {
      super(var2, var1.getParentWorkbench(), var3);
      this._plugin = var1;
      this.init();
   }

   public PeerWorkbench(Peer var1, CoreController var2) {
      super(var2, var1.getParentWorkbench());
      this._plugin = var1;
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, PeerWorkbench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return null;
   }

   protected class CloseTab extends RemoveTab {
      private CloseTab(Tabbable var2) {
         super(var2);
      }
   }
}
