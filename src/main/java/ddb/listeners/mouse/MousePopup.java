package ddb.listeners.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MousePopup extends MouseAdapter {
   protected abstract void maybePopup(MouseEvent var1);

   public void mouseClicked(MouseEvent var1) {
      super.mouseClicked(var1);
      this.maybePopup(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      super.mouseReleased(var1);
      this.maybePopup(var1);
   }

   public void mousePressed(MouseEvent var1) {
      super.mousePressed(var1);
      this.maybePopup(var1);
   }
}
