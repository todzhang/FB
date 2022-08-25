package ddb.events;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AutoScroll implements ChangeListener {
   BoundedRangeModel rangeModel;
   int max;
   boolean scroll;

   public AutoScroll(BoundedRangeModel var1) {
      this.max = 0;
      this.rangeModel = var1;
      this.scroll = true;
      this.rangeModel.addChangeListener(this);
   }

   public AutoScroll(JScrollPane var1) {
      this(var1.getVerticalScrollBar().getModel());
   }

   public void stateChanged(ChangeEvent var1) {
      if (this.scroll) {
         boolean var2 = false;
         int var6;
         synchronized(this) {
            if (this.rangeModel.getMaximum() == this.max) {
               return;
            }

            var6 = this.max = this.rangeModel.getMaximum();
         }

         this.rangeModel.setValue(var6);
      }
   }

   public void setScroll(boolean var1) {
      this.scroll = var1;
   }

   public boolean getScroll() {
      return this.scroll;
   }
}
