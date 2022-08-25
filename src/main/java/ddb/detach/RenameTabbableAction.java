package ddb.detach;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

public class RenameTabbableAction extends AbstractAction implements Runnable {
   private Tabbable plugin;
   private Workbench workbench;

   public RenameTabbableAction(Tabbable var1, Workbench var2) {
      this.workbench = var2;
      this.plugin = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      EventQueue.invokeLater(this);
   }

   public void run() {
      String var1 = (String)JOptionPane.showInputDialog(this.workbench, "Enter new plugin name", "Rename", 3, (Icon)null, (Object[])null, this.plugin.getName());
      if (var1 != null) {
         this.workbench.enqueAction(Workbench.WorkbenchAction.RENAMETAB, this.plugin, var1);
      }

   }
}
