package org.syntax.jedit;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class TextUtilities {
   public static int findMatchingBracket(Document var0, int var1) throws BadLocationException {
      if (var0.getLength() == 0) {
         return -1;
      } else {
         char var2 = var0.getText(var1, 1).charAt(0);
         byte var3;
         boolean var4;
         switch(var2) {
         case '(':
            var3 = 41;
            var4 = false;
            break;
         case ')':
            var3 = 40;
            var4 = true;
            break;
         case '[':
            var3 = 93;
            var4 = false;
            break;
         case ']':
            var3 = 91;
            var4 = true;
            break;
         case '{':
            var3 = 125;
            var4 = false;
            break;
         case '}':
            var3 = 123;
            var4 = true;
            break;
         default:
            return -1;
         }

         int var5;
         if (var4) {
            var5 = 1;
            String var6 = var0.getText(0, var1);

            for(int var7 = var1 - 1; var7 >= 0; --var7) {
               char var8 = var6.charAt(var7);
               if (var8 == var2) {
                  ++var5;
               } else if (var8 == var3) {
                  --var5;
                  if (var5 == 0) {
                     return var7;
                  }
               }
            }
         } else {
            var5 = 1;
            ++var1;
            int var10 = var0.getLength() - var1;
            String var11 = var0.getText(var1, var10);

            for(int var12 = 0; var12 < var10; ++var12) {
               char var9 = var11.charAt(var12);
               if (var9 == var2) {
                  ++var5;
               } else if (var9 == var3) {
                  --var5;
                  if (var5 == 0) {
                     return var12 + var1;
                  }
               }
            }
         }

         return -1;
      }
   }

   public static int findWordStart(String var0, int var1, String var2) {
      char var3 = var0.charAt(var1 - 1);
      if (var2 == null) {
         var2 = "";
      }

      boolean var4 = !Character.isLetterOrDigit(var3) && var2.indexOf(var3) == -1;
      int var5 = 0;

      for(int var6 = var1 - 1; var6 >= 0; --var6) {
         var3 = var0.charAt(var6);
         if (var4 ^ (!Character.isLetterOrDigit(var3) && var2.indexOf(var3) == -1)) {
            var5 = var6 + 1;
            break;
         }
      }

      return var5;
   }

   public static int findWordEnd(String var0, int var1, String var2) {
      char var3 = var0.charAt(var1);
      if (var2 == null) {
         var2 = "";
      }

      boolean var4 = !Character.isLetterOrDigit(var3) && var2.indexOf(var3) == -1;
      int var5 = var0.length();

      for(int var6 = var1; var6 < var0.length(); ++var6) {
         var3 = var0.charAt(var6);
         if (var4 ^ (!Character.isLetterOrDigit(var3) && var2.indexOf(var3) == -1)) {
            var5 = var6;
            break;
         }
      }

      return var5;
   }
}
