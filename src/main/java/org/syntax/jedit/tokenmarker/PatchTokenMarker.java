package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;

public class PatchTokenMarker extends TokenMarker {
   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      if (var2.count == 0) {
         return 0;
      } else {
         switch(var2.array[var2.offset]) {
         case '*':
         case '@':
            this.addToken(var2.count, (byte)8);
            break;
         case '+':
         case '>':
            this.addToken(var2.count, (byte)6);
            break;
         case '-':
         case '<':
            this.addToken(var2.count, (byte)7);
            break;
         default:
            this.addToken(var2.count, (byte)0);
         }

         return 0;
      }
   }

   public boolean supportsMultilineTokens() {
      return false;
   }
}
