package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;

public class EiffelTokenMarker extends TokenMarker {
   private static KeywordMap eiffelKeywords;
   private boolean cpp;
   private KeywordMap keywords = getKeywords();
   private int lastOffset;
   private int lastKeyword;

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      this.lastOffset = var5;
      this.lastKeyword = var5;
      int var6 = var2.count + var5;
      boolean var7 = false;
      int var8 = var5;

      while(true) {
         label90: {
            if (var8 < var6) {
               int var9 = var8 + 1;
               char var10 = var4[var8];
               if (var10 == '%') {
                  var7 = !var7;
                  break label90;
               }

               label78:
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
                     break label90;
                  case '\'':
                     this.doKeyword(var2, var8, var10);
                     if (var7) {
                        var7 = false;
                     } else {
                        this.addToken(var8 - this.lastOffset, var1);
                        var1 = 4;
                        this.lastOffset = this.lastKeyword = var8;
                     }
                     break label90;
                  case '-':
                     var7 = false;
                     this.doKeyword(var2, var8, var10);
                     if (var6 - var8 <= 1) {
                        break label90;
                     }

                     switch(var4[var9]) {
                     case '-':
                        this.addToken(var8 - this.lastOffset, var1);
                        this.addToken(var6 - var8, (byte)1);
                        this.lastOffset = this.lastKeyword = var6;
                        break label78;
                     default:
                        break label90;
                     }
                  case ':':
                     if (this.lastKeyword == var5) {
                        if (!this.doKeyword(var2, var8, var10)) {
                           var7 = false;
                           this.addToken(var9 - this.lastOffset, (byte)5);
                           this.lastOffset = this.lastKeyword = var9;
                        }
                     } else if (this.doKeyword(var2, var8, var10)) {
                     }
                     break label90;
                  default:
                     var7 = false;
                     if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                        this.doKeyword(var2, var8, var10);
                     }
                     break label90;
                  }
               case 1:
               case 2:
                  throw new RuntimeException("Wrong eiffel parser state");
               case 3:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '"') {
                     this.addToken(var9 - this.lastOffset, var1);
                     var1 = 0;
                     this.lastOffset = this.lastKeyword = var9;
                  }
                  break label90;
               case 4:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '\'') {
                     this.addToken(var9 - this.lastOffset, (byte)3);
                     var1 = 0;
                     this.lastOffset = this.lastKeyword = var9;
                  }
                  break label90;
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
            case 7:
               this.addToken(var6 - this.lastOffset, var1);
               if (!var7) {
                  var1 = 0;
               }
            case 5:
            case 6:
            default:
               this.addToken(var6 - this.lastOffset, var1);
            }

            return var1;
         }

         ++var8;
      }
   }

   public static KeywordMap getKeywords() {
      if (eiffelKeywords == null) {
         eiffelKeywords = new KeywordMap(true);
         eiffelKeywords.add("alias", (byte)6);
         eiffelKeywords.add("all", (byte)6);
         eiffelKeywords.add("and", (byte)6);
         eiffelKeywords.add("as", (byte)6);
         eiffelKeywords.add("check", (byte)6);
         eiffelKeywords.add("class", (byte)6);
         eiffelKeywords.add("creation", (byte)6);
         eiffelKeywords.add("debug", (byte)6);
         eiffelKeywords.add("deferred", (byte)6);
         eiffelKeywords.add("do", (byte)6);
         eiffelKeywords.add("else", (byte)6);
         eiffelKeywords.add("elseif", (byte)6);
         eiffelKeywords.add("end", (byte)6);
         eiffelKeywords.add("ensure", (byte)6);
         eiffelKeywords.add("expanded", (byte)6);
         eiffelKeywords.add("export", (byte)6);
         eiffelKeywords.add("external", (byte)6);
         eiffelKeywords.add("feature", (byte)6);
         eiffelKeywords.add("from", (byte)6);
         eiffelKeywords.add("frozen", (byte)6);
         eiffelKeywords.add("if", (byte)6);
         eiffelKeywords.add("implies", (byte)6);
         eiffelKeywords.add("indexing", (byte)6);
         eiffelKeywords.add("infix", (byte)6);
         eiffelKeywords.add("inherit", (byte)6);
         eiffelKeywords.add("inspect", (byte)6);
         eiffelKeywords.add("invariant", (byte)6);
         eiffelKeywords.add("is", (byte)6);
         eiffelKeywords.add("like", (byte)6);
         eiffelKeywords.add("local", (byte)6);
         eiffelKeywords.add("loop", (byte)6);
         eiffelKeywords.add("not", (byte)6);
         eiffelKeywords.add("obsolete", (byte)6);
         eiffelKeywords.add("old", (byte)6);
         eiffelKeywords.add("once", (byte)6);
         eiffelKeywords.add("or", (byte)6);
         eiffelKeywords.add("prefix", (byte)6);
         eiffelKeywords.add("redefine", (byte)6);
         eiffelKeywords.add("rename", (byte)6);
         eiffelKeywords.add("require", (byte)6);
         eiffelKeywords.add("rescue", (byte)6);
         eiffelKeywords.add("retry", (byte)6);
         eiffelKeywords.add("select", (byte)6);
         eiffelKeywords.add("separate", (byte)6);
         eiffelKeywords.add("then", (byte)6);
         eiffelKeywords.add("undefine", (byte)6);
         eiffelKeywords.add("until", (byte)6);
         eiffelKeywords.add("variant", (byte)6);
         eiffelKeywords.add("when", (byte)6);
         eiffelKeywords.add("xor", (byte)6);
         eiffelKeywords.add("current", (byte)4);
         eiffelKeywords.add("false", (byte)4);
         eiffelKeywords.add("precursor", (byte)4);
         eiffelKeywords.add("result", (byte)4);
         eiffelKeywords.add("strip", (byte)4);
         eiffelKeywords.add("true", (byte)4);
         eiffelKeywords.add("unique", (byte)4);
         eiffelKeywords.add("void", (byte)4);
      }

      return eiffelKeywords;
   }

   private boolean doKeyword(Segment var1, int var2, char var3) {
      int var4 = var2 + 1;
      boolean var5 = false;
      int var6 = var2 - this.lastKeyword;
      byte var7 = this.keywords.lookup(var1, this.lastKeyword, var6);
      if (var7 == 0) {
         var5 = true;

         for(int var8 = this.lastKeyword; var8 < this.lastKeyword + var6; ++var8) {
            char var9 = var1.array[var8];
            if (var9 != '_' && !Character.isUpperCase(var9)) {
               var5 = false;
               break;
            }
         }

         if (var5) {
            var7 = 8;
         }
      }

      if (var7 != 0) {
         if (this.lastKeyword != this.lastOffset) {
            this.addToken(this.lastKeyword - this.lastOffset, (byte)0);
         }

         this.addToken(var6, var7);
         this.lastOffset = var2;
      }

      this.lastKeyword = var4;
      return false;
   }
}
