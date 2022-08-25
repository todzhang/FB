package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;

public class MakefileTokenMarker extends TokenMarker {
   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      int var6 = var5;
      int var7 = var2.count + var5;
      boolean var8 = false;

      label87:
      for(int var9 = var5; var9 < var7; ++var9) {
         int var10 = var9 + 1;
         char var11 = var4[var9];
         if (var11 == '\\') {
            var8 = !var8;
         } else {
            switch(var1) {
            case 0:
               switch(var11) {
               case '\t':
               case ' ':
               case ':':
               case '=':
                  var8 = false;
                  if (var6 == var5) {
                     this.addToken(var10 - var6, (byte)6);
                     var6 = var10;
                  }
                  break;
               case '"':
                  if (var8) {
                     var8 = false;
                  } else {
                     this.addToken(var9 - var6, var1);
                     var1 = 3;
                     var6 = var9;
                  }
                  break;
               case '#':
                  if (!var8) {
                     this.addToken(var9 - var6, var1);
                     this.addToken(var7 - var9, (byte)1);
                     var6 = var7;
                     break label87;
                  }

                  var8 = false;
                  break;
               case '$':
                  if (var8) {
                     var8 = false;
                  } else if (var6 != var5) {
                     this.addToken(var9 - var6, var1);
                     var6 = var9;
                     if (var7 - var9 > 1) {
                        char var12 = var4[var10];
                        if (var12 != '(' && var12 != '{') {
                           this.addToken(2, (byte)7);
                           var6 = var9 + 2;
                           ++var9;
                        } else {
                           var1 = 7;
                        }
                     }
                  }
                  break;
               case '\'':
                  if (var8) {
                     var8 = false;
                  } else {
                     this.addToken(var9 - var6, var1);
                     var1 = 4;
                     var6 = var9;
                  }
                  break;
               default:
                  var8 = false;
               }
            case 7:
               var8 = false;
               if (var11 == ')' || var11 == '}') {
                  this.addToken(var10 - var6, var1);
                  var1 = 0;
                  var6 = var10;
               }
            case 1:
            case 2:
            case 5:
            case 6:
            default:
               break;
            case 3:
               if (var8) {
                  var8 = false;
               } else if (var11 == '"') {
                  this.addToken(var10 - var6, var1);
                  var1 = 0;
                  var6 = var10;
               } else {
                  var8 = false;
               }
               break;
            case 4:
               if (var8) {
                  var8 = false;
               } else if (var11 == '\'') {
                  this.addToken(var10 - var6, (byte)3);
                  var1 = 0;
                  var6 = var10;
               } else {
                  var8 = false;
               }
            }
         }
      }

      switch(var1) {
      case 4:
         this.addToken(var7 - var6, (byte)3);
         break;
      case 7:
         this.addToken(var7 - var6, (byte)10);
         var1 = 0;
         break;
      default:
         this.addToken(var7 - var6, var1);
      }

      return var1;
   }
}
