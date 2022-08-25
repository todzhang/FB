package ddb.detach;

import java.awt.Color;
import java.util.Observer;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;

public interface TabbableStatus {
   TabbableStatus.State getDetails();

   TabbableStatus.State getHost();

   boolean isIndeterminate();

   BoundedRangeModel getProgressModel();

   Icon getStatusIcon();

   void addObserver(Observer var1);

   void deleteObserver(Observer var1);

   void deleteObservers();

   boolean hasChanged();

   void notifyObservers();

   void notifyObservers(Object var1);

   void fini();

   public static class State {
      private String text = "";
      private Color foreground;
      private Color background;

      public State() {
         this.foreground = Color.BLACK;
         this.background = null;
      }

      public Color getBackground() {
         return this.background;
      }

      public void setBackground(Color var1) {
         this.background = var1;
      }

      public Color getForeground() {
         return this.foreground;
      }

      public void setForeground(Color var1) {
         this.foreground = var1;
      }

      public String getText() {
         return this.text;
      }

      public void setText(String var1) {
         this.text = var1;
      }
   }
}
