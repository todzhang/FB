package org.syntax.jedit;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;

public class SyntaxStyle {
   private Color color;
   private boolean italic;
   private boolean bold;
   private Font lastFont;
   private Font lastStyledFont;
   private FontMetrics fontMetrics;

   public SyntaxStyle(Color var1, boolean var2, boolean var3) {
      this.color = var1;
      this.italic = var2;
      this.bold = var3;
   }

   public Color getColor() {
      return this.color;
   }

   public boolean isPlain() {
      return !this.bold && !this.italic;
   }

   public boolean isItalic() {
      return this.italic;
   }

   public boolean isBold() {
      return this.bold;
   }

   public Font getStyledFont(Font var1) {
      if (var1 == null) {
         throw new NullPointerException("font param must not be null");
      } else if (var1.equals(this.lastFont)) {
         return this.lastStyledFont;
      } else {
         this.lastFont = var1;
         this.lastStyledFont = new Font(var1.getFamily(), (this.bold ? 1 : 0) | (this.italic ? 2 : 0), var1.getSize());
         return this.lastStyledFont;
      }
   }

   public FontMetrics getFontMetrics(Font var1) {
      if (var1 == null) {
         throw new NullPointerException("font param must not be null");
      } else if (var1.equals(this.lastFont) && this.fontMetrics != null) {
         return this.fontMetrics;
      } else {
         this.lastFont = var1;
         this.lastStyledFont = new Font(var1.getFamily(), (this.bold ? 1 : 0) | (this.italic ? 2 : 0), var1.getSize());
         this.fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(this.lastStyledFont);
         return this.fontMetrics;
      }
   }

   public void setGraphicsFlags(Graphics var1, Font var2) {
      Font var3 = this.getStyledFont(var2);
      var1.setFont(var3);
      var1.setColor(this.color);
   }

   public String toString() {
      return this.getClass().getName() + "[color=" + this.color + (this.italic ? ",italic" : "") + (this.bold ? ",bold" : "") + "]";
   }
}
