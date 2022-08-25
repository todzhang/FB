package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;

public class TeXTokenMarker extends TokenMarker {
   public static final byte BDFORMULA = 100;
   public static final byte EDFORMULA = 101;

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      int var6 = var5;
      int var7 = var2.count + var5;
      boolean var8 = false;

      label96:
      for(int var9 = var5; var9 < var7; ++var9) {
         int var10 = var9 + 1;
         char var11 = var4[var9];
         if (Character.isLetter(var11)) {
            var8 = false;
         } else {
            if (var8) {
               var8 = false;
               if (var1 == 7 || var1 == 101) {
                  var1 = 7;
               }

               this.addToken(var10 - var6, var1);
               var6 = var10;
               if (var1 == 6) {
                  var1 = 0;
               }
               continue;
            }

            if (var1 == 100 || var1 == 101) {
               var1 = 7;
            }

            this.addToken(var9 - var6, var1);
            if (var1 == 6) {
               var1 = 0;
            }

            var6 = var9;
         }

         switch(var11) {
         case '$':
            var8 = false;
            if (var1 == 0) {
               var1 = 7;
               this.addToken(var9 - var6, (byte)0);
               var6 = var9;
            } else if (var1 == 6) {
               var1 = 7;
               this.addToken(var9 - var6, (byte)6);
               var6 = var9;
            } else if (var1 == 7) {
               if (var9 - var6 == 1 && var4[var9 - 1] == '$') {
                  var1 = 100;
               } else {
                  var1 = 0;
                  this.addToken(var10 - var6, (byte)7);
                  var6 = var10;
               }
            } else if (var1 == 100) {
               var1 = 101;
            } else if (var1 == 101) {
               var1 = 0;
               this.addToken(var10 - var6, (byte)7);
               var6 = var10;
            }
            break;
         case '%':
            if (!var8) {
               this.addToken(var9 - var6, var1);
               this.addToken(var7 - var9, (byte)1);
               var6 = var7;
               break label96;
            }

            var8 = false;
            break;
         case '\\':
            var8 = true;
            if (var1 == 0) {
               var1 = 6;
               this.addToken(var9 - var6, (byte)0);
               var6 = var9;
            }
         }
      }

      if (var6 != var7) {
         this.addToken(var7 - var6, var1 != 100 && var1 != 101 ? var1 : 7);
      }

      return var1 != 6 ? var1 : 0;
   }
}
