package ddb.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class RightClickListener extends MouseAdapter {
   private JPopupMenu popup;

   public RightClickListener(JPopupMenu var1) {
      this.popup = var1;
   }

   public void mousePressed(MouseEvent var1) {
      this.maybeShowPopup(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      this.maybeShowPopup(var1);
   }

   private void maybeShowPopup(MouseEvent var1) {
      if (var1.isPopupTrigger()) {
         this.popup.show(var1.getComponent(), var1.getX(), var1.getY());
      }

   }
}
