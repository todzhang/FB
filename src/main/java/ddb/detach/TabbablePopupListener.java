package ddb.detach;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class TabbablePopupListener extends MouseAdapter {
   protected Workbench workbench;

   public TabbablePopupListener(Workbench var1) {
      this.workbench = var1;
   }

   public void mousePressed(MouseEvent var1) {
      this.maybeShowPopup(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      this.maybeShowPopup(var1);
   }

   private void maybeShowPopup(MouseEvent var1) {
      if (var1.isPopupTrigger()) {
         int var2 = this.workbench.getUI().tabForCoordinate(this.workbench, var1.getX(), var1.getY());
         if (var2 == -1) {
            JPopupMenu var6 = this.workbench.getMenu();
            if (var6 != null) {
               SwingUtilities.updateComponentTreeUI(var6);
               var6.show(var1.getComponent(), var1.getX(), var1.getY());
            }

            return;
         }

         Component var3 = this.workbench.getComponentAt(var2);
         Tabbable var4 = this.workbench.getTabbableForDisplay(var3);
         TabbablePopupMenu var5 = this.getPopupMenu(var4);
         SwingUtilities.updateComponentTreeUI(var5);
         var5.show(var1.getComponent(), var1.getX(), var1.getY());
      }

   }

   protected TabbablePopupMenu getPopupMenu(Tabbable var1) {
      return new TabbablePopupMenu(var1, this.workbench);
   }
}
