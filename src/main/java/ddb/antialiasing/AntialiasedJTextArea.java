package ddb.antialiasing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextArea;
import javax.swing.text.Document;

public class AntialiasedJTextArea extends JTextArea {
   protected boolean autoScroll;

   public AntialiasedJTextArea() {
      this.init();
   }

   public AntialiasedJTextArea(boolean var1) {
      this.autoScroll = var1;
      this.init();
   }

   public AntialiasedJTextArea(int var1, int var2) {
      super(var1, var2);
      this.init();
   }

   public AntialiasedJTextArea(String var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTextArea(String var1, int var2, int var3) {
      super(var1, var2, var3);
      this.init();
   }

   public AntialiasedJTextArea(Document var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTextArea(Document var1, String var2, int var3, int var4) {
      super(var1, var2, var3, var4);
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

   @Override
   public void append(String var1) {
      super.append(var1);
      if (this.autoScroll) {
         this.setCaretPosition(this.getDocument().getLength());
      }

   }

   @Override
   public void setText(String var1) {
      super.setText(var1);
      if (this.autoScroll) {
         this.setCaretPosition(this.getDocument().getLength());
      }

   }

   public boolean isAutoScroll() {
      return this.autoScroll;
   }

   public void setAutoScroll(boolean var1) {
      this.autoScroll = var1;
   }
}
