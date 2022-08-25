package ddb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

public class GuiConstants {
   public static final GuiConstants.FontDetails FIXED_WIDTH_FONT = new GuiConstants.FontDetails("Andale Mono WTG", "/amwtg___.ttf");
   public static final GuiConstants.FontDetails VARIABLE_WIDTH_FONT = new GuiConstants.FontDetails("Arial Unicode MS", "/ARIALUNI.TTF");

   public static void locate(Frame var0, double var1, double var3) {
      Rectangle var5 = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
      int var6 = var5.width - var0.getPreferredSize().width;
      int var7 = var5.height - var0.getPreferredSize().height;
      Point var8 = new Point();
      var8.x = var5.x + (new Double((double)var6 * var1)).intValue();
      var8.y = var5.y + (new Double((double)var7 * var3)).intValue();
      var0.setLocation(var8);
   }

   public static enum DefaultColor {
      DEFAULT_BACKGROUND(Color.BLACK),
      DEFAULT_FOREGROUND(Color.LIGHT_GRAY),
      SECONDARY_FOREGROUND(Color.BLUE),
      SECONDARY_BACKGROUND(Color.LIGHT_GRAY),
      PROMPT_MODE_BACKGROUND(Color.RED),
      PROMPT_MODE_FOREGROUND(Color.YELLOW),
      ERROR(Color.RED),
      WARNING(Color.YELLOW),
      NOTICE(Color.GREEN);

      Color col;

      private DefaultColor(Color var3) {
         this.col = var3;
      }

      public Color getColor() {
         return this.col;
      }
   }

   public static class FontDetails {
      public final String name;
      public final String file;
      public final int size;
      public final Font Basic;
      public final Font Bold;

      public FontDetails(String var1, String var2) {
         this.name = var1;
         this.file = var2;
         this.size = 14;
         Font var3 = null;
         InputStream var4 = null;

         try {
            var4 = GuiConstants.class.getResourceAsStream(var2);
            var3 = Font.createFont(0, var4);
         } catch (Exception var7) {
            var3 = new Font(var1, 0, this.size);
            System.err.println("Failed to create font from file");
         }

         try {
            if (var4 != null) {
               var4.close();
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }

         this.Basic = var3.deriveFont(Float.parseFloat("14"));
         this.Bold = this.Basic.deriveFont(1);
      }
   }
}
