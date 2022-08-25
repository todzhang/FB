package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.SyntaxUtilities;

public class ShellScriptTokenMarker extends TokenMarker {
   public static final byte LVARIABLE = 100;

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      byte var5 = 0;
      int var6 = var2.offset;
      int var7 = var6;
      int var8 = var2.count + var6;
      if (var1 == 3 && var3 != 0 && this.lineInfo[var3 - 1].obj != null) {
         String var13 = (String)this.lineInfo[var3 - 1].obj;
         if (var13 != null && var13.length() == var2.count && SyntaxUtilities.regionMatches(false, var2, var6, var13)) {
            this.addToken(var2.count, (byte)3);
            return 0;
         } else {
            this.addToken(var2.count, (byte)3);
            this.lineInfo[var3].obj = var13;
            return 3;
         }
      } else {
         boolean var9 = false;
         int var10 = var6;

         while(true) {
            label139: {
               if (var10 < var8) {
                  int var11 = var10 + 1;
                  char var12 = var4[var10];
                  if (var12 == '\\') {
                     var9 = !var9;
                     break label139;
                  }

                  label127:
                  switch(var1) {
                  case 0:
                     switch(var12) {
                     case '\t':
                     case ' ':
                     case '(':
                     case ')':
                        var9 = false;
                        if (var5 == 1) {
                           this.addToken(var10 - var7, (byte)6);
                           var7 = var10;
                           var5 = 2;
                        }
                        break label139;
                     case '"':
                        if (var9) {
                           var9 = false;
                        } else {
                           this.addToken(var10 - var7, var1);
                           var1 = 3;
                           this.lineInfo[var3].obj = null;
                           var5 = 2;
                           var7 = var10;
                        }
                        break label139;
                     case '#':
                        if (var9) {
                           var9 = false;
                           break label139;
                        }

                        this.addToken(var10 - var7, var1);
                        this.addToken(var8 - var10, (byte)1);
                        var7 = var8;
                        break label127;
                     case '$':
                        if (var9) {
                           var9 = false;
                        } else {
                           this.addToken(var10 - var7, var1);
                           var5 = 2;
                           var7 = var10;
                           if (var8 - var10 >= 2) {
                              switch(var4[var11]) {
                              case '(':
                                 break label139;
                              case '{':
                                 var1 = 100;
                                 break label139;
                              default:
                                 var1 = 7;
                              }
                           } else {
                              var1 = 7;
                           }
                        }
                        break label139;
                     case '&':
                     case ';':
                     case '|':
                        if (var9) {
                           var9 = false;
                        } else {
                           var5 = 0;
                        }
                        break label139;
                     case '\'':
                        if (var9) {
                           var9 = false;
                        } else {
                           this.addToken(var10 - var7, var1);
                           var1 = 4;
                           var5 = 2;
                           var7 = var10;
                        }
                        break label139;
                     case '<':
                        if (var9) {
                           var9 = false;
                        } else if (var8 - var10 > 1 && var4[var11] == '<') {
                           this.addToken(var10 - var7, var1);
                           var1 = 3;
                           var7 = var10;
                           this.lineInfo[var3].obj = new String(var4, var10 + 2, var8 - (var10 + 2));
                        }
                        break label139;
                     case '=':
                        var9 = false;
                        if (var5 == 1) {
                           this.addToken(var10 - var7, var1);
                           var7 = var10;
                           var5 = 2;
                        }
                        break label139;
                     default:
                        var9 = false;
                        if (Character.isLetter(var12) && var5 == 0) {
                           this.addToken(var10 - var7, var1);
                           var7 = var10;
                           ++var5;
                        }
                        break label139;
                     }
                  case 3:
                     if (var9) {
                        var9 = false;
                     } else if (var12 == '"') {
                        this.addToken(var11 - var7, var1);
                        var5 = 2;
                        var7 = var11;
                        var1 = 0;
                     } else {
                        var9 = false;
                     }
                     break label139;
                  case 4:
                     if (var9) {
                        var9 = false;
                     } else if (var12 == '\'') {
                        this.addToken(var11 - var7, (byte)3);
                        var5 = 2;
                        var7 = var11;
                        var1 = 0;
                     } else {
                        var9 = false;
                     }
                     break label139;
                  case 7:
                     var9 = false;
                     if (!Character.isLetterOrDigit(var12) && var12 != '_') {
                        if (var10 != var6 && var4[var10 - 1] == '$') {
                           this.addToken(var11 - var7, var1);
                           var7 = var11;
                           var1 = 0;
                        } else {
                           this.addToken(var10 - var7, var1);
                           var7 = var10;
                           var1 = 0;
                        }
                     }
                     break label139;
                  case 100:
                     var9 = false;
                     if (var12 == '}') {
                        this.addToken(var11 - var7, (byte)7);
                        var7 = var11;
                        var1 = 0;
                     }
                     break label139;
                  default:
                     throw new InternalError("Invalid state: " + var1);
                  }
               }

               switch(var1) {
               case 0:
                  if (var5 == 1) {
                     this.addToken(var8 - var7, (byte)6);
                  } else {
                     this.addToken(var8 - var7, var1);
                  }
                  break;
               case 4:
                  this.addToken(var8 - var7, (byte)3);
                  break;
               case 7:
                  this.addToken(var8 - var7, var1);
                  var1 = 0;
                  break;
               case 100:
                  this.addToken(var8 - var7, (byte)10);
                  var1 = 0;
                  break;
               default:
                  this.addToken(var8 - var7, var1);
               }

               return var1;
            }

            ++var10;
         }
      }
   }
}
