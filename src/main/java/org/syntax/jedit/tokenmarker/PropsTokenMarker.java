package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;

public class PropsTokenMarker extends TokenMarker {
   public static final byte VALUE = 100;

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      int var6 = var5;
      int var7 = var2.count + var5;

      label38:
      for(int var8 = var5; var8 < var7; ++var8) {
         int var9 = var8 + 1;
         switch(var1) {
         case 0:
            switch(var4[var8]) {
            case '#':
            case ';':
               if (var8 == var5) {
                  this.addToken(var2.count, (byte)1);
                  var6 = var7;
                  break label38;
               }
               continue;
            case '=':
               this.addToken(var8 - var6, (byte)6);
               var1 = 100;
               var6 = var8;
               continue;
            case '[':
               if (var8 == var5) {
                  this.addToken(var8 - var6, var1);
                  var1 = 7;
                  var6 = var8;
               }
            default:
               continue;
            }
         case 7:
            if (var4[var8] == ']') {
               this.addToken(var9 - var6, var1);
               var1 = 0;
               var6 = var9;
            }
         case 100:
            break;
         default:
            throw new InternalError("Invalid state: " + var1);
         }
      }

      if (var6 != var7) {
         this.addToken(var7 - var6, (byte)0);
      }

      return 0;
   }

   public boolean supportsMultilineTokens() {
      return false;
   }
}
