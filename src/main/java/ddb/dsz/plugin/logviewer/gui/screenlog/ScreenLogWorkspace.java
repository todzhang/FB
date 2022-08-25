package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;

public class ScreenLogWorkspace extends Workbench {
   ScreenLogPane owner;

   public ScreenLogWorkspace(Logger core, ScreenLogPane owner, int tabPlacement) {
      super(core, tabPlacement);
      this.owner = owner;
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, ScreenLogWorkspace.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return new ScreenLogPopupMenu(this);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new ScreenLogPopupListener(this);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable plugin) {
         super( plugin);
      }

      public void runTask() {
         ScreenLogWorkspace.this.owner.destroy((LogViewerDetachable)LogViewerDetachable.class.cast(this.tab));
         ScreenLogWorkspace.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         ScreenLogWorkspace.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }
}
