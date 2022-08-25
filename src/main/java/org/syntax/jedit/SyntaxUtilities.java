package org.syntax.jedit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import org.syntax.jedit.tokenmarker.Token;

public class SyntaxUtilities {
   public static boolean regionMatches(boolean var0, Segment var1, int var2, String var3) {
      int var4 = var2 + var3.length();
      char[] var5 = var1.array;
      if (var4 > var1.offset + var1.count) {
         return false;
      } else {
         int var6 = var2;

         for(int var7 = 0; var6 < var4; ++var7) {
            char var8 = var5[var6];
            char var9 = var3.charAt(var7);
            if (var0) {
               var8 = Character.toUpperCase(var8);
               var9 = Character.toUpperCase(var9);
            }

            if (var8 != var9) {
               return false;
            }

            ++var6;
         }

         return true;
      }
   }

   public static boolean regionMatches(boolean var0, Segment var1, int var2, char[] var3) {
      int var4 = var2 + var3.length;
      char[] var5 = var1.array;
      if (var4 > var1.offset + var1.count) {
         return false;
      } else {
         int var6 = var2;

         for(int var7 = 0; var6 < var4; ++var7) {
            char var8 = var5[var6];
            char var9 = var3[var7];
            if (var0) {
               var8 = Character.toUpperCase(var8);
               var9 = Character.toUpperCase(var9);
            }

            if (var8 != var9) {
               return false;
            }

            ++var6;
         }

         return true;
      }
   }

   public static SyntaxStyle[] getDefaultSyntaxStyles() {
      SyntaxStyle[] var0 = new SyntaxStyle[]{null, new SyntaxStyle(Color.black, true, false), new SyntaxStyle(new Color(10027059), true, false), new SyntaxStyle(new Color(6619289), false, false), new SyntaxStyle(new Color(6619289), false, true), new SyntaxStyle(new Color(10027059), false, true), new SyntaxStyle(Color.black, false, true), new SyntaxStyle(Color.magenta, false, false), new SyntaxStyle(new Color(38400), false, false), new SyntaxStyle(Color.black, false, true), new SyntaxStyle(Color.red, false, true)};
      return var0;
   }

   public static int paintSyntaxLine(Segment var0, Token var1, SyntaxStyle[] var2, TabExpander var3, Graphics var4, int var5, int var6) {
      Font var7 = var4.getFont();
      Color var8 = var4.getColor();
      int var9 = 0;

      while(true) {
         byte var10 = var1.id;
         if (var10 == 127) {
            return var5;
         }

         int var11 = var1.length;
         if (var10 == 0) {
            if (!var8.equals(var4.getColor())) {
               var4.setColor(var8);
            }

            if (!var7.equals(var4.getFont())) {
               var4.setFont(var7);
            }
         } else {
            var2[var10].setGraphicsFlags(var4, var7);
         }

         var0.count = var11;
         var5 = Utilities.drawTabbedText(var0, var5, var6, var4, var3, 0);
         var0.offset += var11;
         var9 += var11;
         var1 = var1.next;
      }
   }

   private SyntaxUtilities() {
   }
}
