package ddb.misc;

import java.awt.Color;

public class ColorPalette {
   private final Color[] foreground;
   private final Color[] background;
   private final boolean opaque;

   public ColorPalette(Color[] foreground, boolean opaque, Color[] background) {
      this.foreground = foreground;
      this.background = background;
      this.opaque = opaque;
   }

   public ColorPalette(Color foreground, boolean opaque, Color... background) {
      this.foreground = new Color[]{foreground};
      this.background = background;
      this.opaque = opaque;
   }

   public final Color getForeground() {
      return this.getForeground(0);
   }

   public final Color getForeground(int i) {
      return this.getColor(this.foreground, i);
   }

   public final Color getBackground() {
      return this.getBackground(0);
   }

   public final Color getBackground(int i) {
      return this.getColor(this.background, i);
   }

   private final Color getColor(Color[] col, int i) {
      if (col == null) {
         return null;
      } else if (col.length == 0) {
         return null;
      } else {
         i %= col.length;
         return col[i];
      }
   }

   public final boolean isOpaque() {
      return this.opaque;
   }
}
