package ddb.dsz.plugin.transfermonitor;

import ddb.detach.Tabbable;
import ddb.detach.Workbench;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.transfermonitor.tabs.TransferDetails;

public class TransferWorkbench extends Workbench {
   public TransferWorkbench() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, TransferWorkbench.CloseTab.class);
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super( var2);
      }

      @Override
      public void runTask() {
         if (this.tab instanceof TransferDetails) {
            TransferWorkbench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
            this.tab.hideFrame();
         }

      }
   }
}
