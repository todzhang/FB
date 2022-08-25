package ddb.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DoubleClickListener extends MouseAdapter {
   private ActionListener action;

   public DoubleClickListener(ActionListener var1) {
      this.action = var1;
   }

   public void mousePressed(MouseEvent var1) {
      this.maybePerformAction(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      this.maybePerformAction(var1);
   }

   private void maybePerformAction(MouseEvent var1) {
      if (var1.getClickCount() >= 2) {
         this.action.actionPerformed(new ActionEvent(var1.getSource(), var1.getID(), var1.paramString()));
      }

   }
}
