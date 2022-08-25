package ddb.detach;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class CloseTabbableAction extends AbstractAction {
   private Tabbable plugin;
   private Workbench workbench;

   public CloseTabbableAction(Tabbable var1, Workbench var2) {
      this.plugin = var1;
      this.workbench = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      if (this.plugin.isClosable()) {
         if (this.plugin.isVerifyClose()) {
            if (JOptionPane.showConfirmDialog(this.workbench, String.format("Are you sure you want to close %s?", this.plugin.getName()), "Verify Close", 0) == 0) {
               this.plugin.close();
               this.workbench.enqueAction(Workbench.WorkbenchAction.CLOSETAB, this.plugin);
            } else {
               this.workbench.enqueAction(Workbench.WorkbenchAction.TABBIFYTAB, this.plugin);
            }
         } else {
            this.workbench.enqueAction(Workbench.WorkbenchAction.CLOSETAB, this.plugin);
         }
      } else {
         this.workbench.enqueAction(Workbench.WorkbenchAction.TABBIFYTAB, this.plugin);
      }

   }
}
