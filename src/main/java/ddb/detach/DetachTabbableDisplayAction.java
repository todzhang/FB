package ddb.detach;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class DetachTabbableDisplayAction extends AbstractAction {
   private Tabbable plugin;
   private Workbench workbench;

   public DetachTabbableDisplayAction(Tabbable var1, Workbench var2) {
      this.plugin = var1;
      this.workbench = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      if (this.plugin.isDetached()) {
         this.workbench.enqueAction(Workbench.WorkbenchAction.TABBIFYTAB, this.plugin);
      } else if (this.plugin.isDetachable()) {
         this.workbench.enqueAction(Workbench.WorkbenchAction.DETACHTAB, this.plugin, null, null);
      }

   }
}
