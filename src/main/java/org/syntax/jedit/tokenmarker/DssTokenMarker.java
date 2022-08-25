package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;

public class DssTokenMarker extends TokenMarker {
   private static KeywordMap cKeywords;
   private KeywordMap keywords;
   private int lastOffset;
   private int lastKeyword;

   public DssTokenMarker() {
      this(getKeywords());
   }

   public DssTokenMarker(KeywordMap var1) {
      this.keywords = var1;
   }

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      this.lastOffset = var5;
      this.lastKeyword = var5;
      int var6 = var2.count + var5;
      boolean var7 = false;
      int var8 = var5;

      while(true) {
         label118: {
            if (var8 < var6) {
               int var9 = var8 + 1;
               char var10 = var4[var8];
               if (var10 == '\\') {
                  var7 = !var7;
                  break label118;
               }

               label106:
               switch(var1) {
               case 0:
                  switch(var10) {
                  case '"':
                     this.doKeyword(var2, var8, var10);
                     if (var7) {
                        var7 = false;
                     } else {
                        this.addToken(var8 - this.lastOffset, var1);
                        var1 = 3;
                        this.lastOffset = this.lastKeyword = var8;
                     }
                     break label118;
                  case '#':
                     if (var7) {
                        var7 = false;
                     }

                     this.addToken(var8 - this.lastOffset, var1);
                     this.addToken(var6 - var8, (byte)1);
                     this.lastOffset = this.lastKeyword = var6;
                     break label106;
                  case '\'':
                     this.doKeyword(var2, var8, var10);
                     if (var7) {
                        var7 = false;
                     } else {
                        this.addToken(var8 - this.lastOffset, var1);
                        var1 = 3;
                        this.lastOffset = this.lastKeyword = var8;
                     }
                     break label118;
                  case '/':
                     var7 = false;
                     this.doKeyword(var2, var8, var10);
                     if (var6 - var8 <= 1) {
                        break label118;
                     }

                     switch(var4[var9]) {
                     case '*':
                        this.addToken(var8 - this.lastOffset, var1);
                        this.lastOffset = this.lastKeyword = var8;
                        if (var6 - var8 > 2 && var4[var8 + 2] == '*') {
                           var1 = 2;
                        } else {
                           var1 = 1;
                        }
                        break label118;
                     case '/':
                        this.addToken(var8 - this.lastOffset, var1);
                        this.addToken(var6 - var8, (byte)1);
                        this.lastOffset = this.lastKeyword = var6;
                        break label106;
                     default:
                        break label118;
                     }
                  case '@':
                     if (var7) {
                        var7 = false;
                     } else {
                        this.doKeyword(var2, var8, var10);
                        this.lastKeyword = var8;
                     }
                     break label118;
                  case '`':
                     this.doKeyword(var2, var8, var10);
                     if (var7) {
                        var7 = false;
                     } else {
                        this.addToken(var8 - this.lastOffset, var1);
                        var1 = 4;
                        this.lastOffset = this.lastKeyword = var8;
                     }
                     break label118;
                  default:
                     var7 = false;
                     if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                        this.doKeyword(var2, var8, var10);
                     }
                     break label118;
                  }
               case 1:
               case 2:
                  var7 = false;
                  if (var10 == '*' && var6 - var8 > 1 && var4[var9] == '/') {
                     ++var8;
                     this.addToken(var8 + 1 - this.lastOffset, var1);
                     var1 = 0;
                     this.lastOffset = this.lastKeyword = var8 + 1;
                  }
                  break label118;
               case 3:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '"') {
                     if (var4[this.lastOffset] == '"') {
                        this.addToken(var9 - this.lastOffset, var1);
                        var1 = 0;
                        this.lastOffset = this.lastKeyword = var9;
                     }
                  } else if (var10 == '\'' && var4[this.lastOffset] == '\'') {
                     this.addToken(var9 - this.lastOffset, var1);
                     var1 = 0;
                     this.lastOffset = this.lastKeyword = var9;
                  }
                  break label118;
               case 4:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '`') {
                     this.addToken(var9 - this.lastOffset, (byte)4);
                     var1 = 0;
                     this.lastOffset = this.lastKeyword = var9;
                  }
                  break label118;
               default:
                  throw new InternalError("Invalid state: " + var1);
               }
            }

            if (var1 == 0) {
               this.doKeyword(var2, var6, '\u0000');
            }

            switch(var1) {
            case 3:
            case 4:
               this.addToken(var6 - this.lastOffset, (byte)10);
               var1 = 0;
               break;
            case 5:
            case 6:
            case 8:
            default:
               this.addToken(var6 - this.lastOffset, var1);
               break;
            case 7:
               this.addToken(var6 - this.lastOffset, var1);
               if (!var7) {
                  var1 = 0;
               }
               break;
            case 9:
               this.addToken(var6 - this.lastOffset, var1);
               if (!var7) {
                  var1 = 0;
               }
            }

            return var1;
         }

         ++var8;
      }
   }

   public static KeywordMap getKeywords() {
      if (cKeywords == null) {
         cKeywords = new KeywordMap(true, 101);
      }

      return cKeywords;
   }

   private boolean doKeyword(Segment var1, int var2, char var3) {
      int var4 = var2 + 1;
      int var5 = var2 - this.lastKeyword;
      byte var6 = this.keywords.lookup(var1, this.lastKeyword, var5);
      if (var6 != 0) {
         if (this.lastKeyword != this.lastOffset) {
            this.addToken(this.lastKeyword - this.lastOffset, (byte)0);
         }

         this.addToken(var5, var6);
         this.lastOffset = var2;
      }

      this.lastKeyword = var4;
      return false;
   }
}
