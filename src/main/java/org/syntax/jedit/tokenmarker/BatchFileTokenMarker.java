package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.SyntaxUtilities;

public class BatchFileTokenMarker extends TokenMarker {
   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      int var6 = var5;
      int var7 = var2.count + var5;
      if (SyntaxUtilities.regionMatches(true, var2, var5, "rem")) {
         this.addToken(var2.count, (byte)1);
         return 0;
      } else {
         label57:
         for(int var8 = var5; var8 < var7; ++var8) {
            int var9 = var8 + 1;
            switch(var1) {
            case 0:
               switch(var4[var8]) {
               case ' ':
                  if (var6 == var5) {
                     this.addToken(var8 - var6, (byte)6);
                     var6 = var8;
                  }
                  continue;
               case '"':
                  this.addToken(var8 - var6, var1);
                  var1 = 3;
                  var6 = var8;
                  continue;
               case '%':
                  this.addToken(var8 - var6, var1);
                  var6 = var8;
                  if (var7 - var8 > 3 && var4[var8 + 2] != ' ') {
                     var1 = 7;
                  } else {
                     this.addToken(2, (byte)7);
                     var8 += 2;
                     var6 = var8;
                  }
                  continue;
               case ':':
                  if (var8 == var5) {
                     this.addToken(var2.count, (byte)5);
                     var6 = var7;
                     break label57;
                  }
               default:
                  continue;
               }
            case 3:
               if (var4[var8] == '"') {
                  this.addToken(var9 - var6, var1);
                  var1 = 0;
                  var6 = var9;
               }
               break;
            case 7:
               if (var4[var8] == '%') {
                  this.addToken(var9 - var6, var1);
                  var1 = 0;
                  var6 = var9;
               }
               break;
            default:
               throw new InternalError("Invalid state: " + var1);
            }
         }

         if (var6 != var7) {
            if (var1 != 0) {
               var1 = 10;
            } else if (var6 == var5) {
               var1 = 6;
            }

            this.addToken(var7 - var6, var1);
         }

         return 0;
      }
   }

   public boolean supportsMultilineTokens() {
      return false;
   }
}
