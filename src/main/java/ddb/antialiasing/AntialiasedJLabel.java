package ddb.antialiasing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JLabel;

public class AntialiasedJLabel extends JLabel {
   public AntialiasedJLabel() {
   }

   public AntialiasedJLabel(String var1) {
      super(var1);
   }

   public AntialiasedJLabel(String var1, int var2) {
      super(var1, var2);
   }

   public AntialiasedJLabel(String var1, Icon var2, int var3) {
      super(var1, var2, var3);
   }

   public AntialiasedJLabel(Icon var1) {
      super(var1);
   }

   public AntialiasedJLabel(Icon var1, int var2) {
      super(var1, var2);
   }

   @Override
   public void paintComponent(Graphics var1) {
      Graphics2D var2 = (Graphics2D)var1;
      var2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(var2);
   }
}
