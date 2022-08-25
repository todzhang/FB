package ddb.antialiasing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class AntialiasedTextPane extends JTextPane {
   public AntialiasedTextPane(StyledDocument var1) {
      super(var1);
      this.init();
   }

   public AntialiasedTextPane() {
      this.init();
   }

   private void init() {
      this.putClientProperty("substancelaf.colorizationFactor", new Double(1.0D));
   }

   @Override
   public void paintComponent(Graphics var1) {
      Graphics2D var2 = (Graphics2D)var1;
      var2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(var2);
   }
}
