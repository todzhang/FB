package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;
import org.syntax.jedit.SyntaxUtilities;

public class PHPTokenMarker extends TokenMarker {
   public static final byte SCRIPT = 100;
   private static KeywordMap keywords = new KeywordMap(false);
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
         label127: {
            if (var8 < var6) {
               int var9 = var8 + 1;
               char var10 = var4[var8];
               if (var10 == '\\') {
                  var7 = !var7;
                  break label127;
               }

               label115:
               switch(var1) {
               case 0:
                  var7 = false;
                  switch(var10) {
                  case '&':
                     this.addToken(var8 - this.lastOffset, var1);
                     this.lastOffset = this.lastKeyword = var8;
                     var1 = 7;
                     break label127;
                  case '<':
                     this.addToken(var8 - this.lastOffset, var1);
                     this.lastOffset = this.lastKeyword = var8;
                     if (SyntaxUtilities.regionMatches(false, var2, var9, "!--")) {
                        var8 += 3;
                        var1 = 1;
                     } else if (SyntaxUtilities.regionMatches(true, var2, var9, "?php")) {
                        this.addToken(5, (byte)5);
                        var8 += 4;
                        this.lastOffset = this.lastKeyword = var8 + 1;
                        var1 = 100;
                     } else if (SyntaxUtilities.regionMatches(true, var2, var9, "?")) {
                        this.addToken(2, (byte)5);
                        ++var8;
                        this.lastOffset = this.lastKeyword = var8 + 1;
                        var1 = 100;
                     } else if (SyntaxUtilities.regionMatches(true, var2, var9, "script>")) {
                        this.addToken(8, (byte)5);
                        var8 += 7;
                        this.lastOffset = this.lastKeyword = var8 + 1;
                        var1 = 100;
                     } else {
                        var1 = 6;
                     }
                  default:
                     break label127;
                  }
               case 1:
                  var7 = false;
                  if (SyntaxUtilities.regionMatches(false, var2, var8, "-->")) {
                     this.addToken(var8 + 3 - this.lastOffset, var1);
                     var8 += 2;
                     this.lastOffset = this.lastKeyword = var8 + 1;
                     var1 = 0;
                  }
                  break label127;
               case 2:
                  var7 = false;
                  if (var10 == '*' && var6 - var8 > 1 && var4[var9] == '/') {
                     this.addToken(var8 + 2 - this.lastOffset, (byte)2);
                     ++var8;
                     this.lastOffset = this.lastKeyword = var8 + 1;
                     var1 = 100;
                  }
                  break label127;
               case 3:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '"') {
                     this.addToken(var9 - this.lastOffset, (byte)3);
                     this.lastOffset = this.lastKeyword = var9;
                     var1 = 100;
                  }
                  break label127;
               case 4:
                  if (var7) {
                     var7 = false;
                  } else if (var10 == '\'') {
                     this.addToken(var9 - this.lastOffset, (byte)3);
                     this.lastOffset = this.lastKeyword = var9;
                     var1 = 100;
                  }
                  break label127;
               case 6:
                  var7 = false;
                  if (var10 == '>') {
                     this.addToken(var9 - this.lastOffset, var1);
                     this.lastOffset = this.lastKeyword = var9;
                     var1 = 0;
                  }
                  break label127;
               case 7:
                  var7 = false;
                  if (var10 == ';') {
                     this.addToken(var9 - this.lastOffset, var1);
                     this.lastOffset = this.lastKeyword = var9;
                     var1 = 0;
                  }
                  break label127;
               case 100:
                  switch(var10) {
                  case '"':
                     if (var7) {
                        var7 = false;
                     } else {
                        this.doKeyword(var2, var8, var10);
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.lastOffset = this.lastKeyword = var8;
                        var1 = 3;
                     }
                     break label127;
                  case '#':
                     this.doKeyword(var2, var8, var10);
                     this.addToken(var8 - this.lastOffset, (byte)8);
                     this.addToken(var6 - var8, (byte)2);
                     this.lastOffset = this.lastKeyword = var6;
                     break label115;
                  case '\'':
                     if (var7) {
                        var7 = false;
                     } else {
                        this.doKeyword(var2, var8, var10);
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.lastOffset = this.lastKeyword = var8;
                        var1 = 4;
                     }
                     break label127;
                  case '/':
                     var7 = false;
                     this.doKeyword(var2, var8, var10);
                     if (var6 - var8 <= 1) {
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.addToken(1, (byte)9);
                        this.lastOffset = this.lastKeyword = var9;
                        break label127;
                     }

                     this.addToken(var8 - this.lastOffset, (byte)8);
                     this.lastOffset = this.lastKeyword = var8;
                     if (var4[var9] != '/') {
                        if (var4[var9] == '*') {
                           var1 = 2;
                        } else {
                           this.addToken(var8 - this.lastOffset, (byte)8);
                           this.addToken(1, (byte)9);
                           this.lastOffset = this.lastKeyword = var9;
                        }
                        break label127;
                     }

                     this.addToken(var6 - var8, (byte)2);
                     this.lastOffset = this.lastKeyword = var6;
                     break label115;
                  case '<':
                     var7 = false;
                     this.doKeyword(var2, var8, var10);
                     if (SyntaxUtilities.regionMatches(true, var2, var9, "/script>")) {
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.addToken(9, (byte)5);
                        var8 += 8;
                        this.lastOffset = this.lastKeyword = var8 + 1;
                        var1 = 0;
                     } else {
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.addToken(1, (byte)9);
                        this.lastOffset = this.lastKeyword = var9;
                     }
                     break label127;
                  case '?':
                     var7 = false;
                     this.doKeyword(var2, var8, var10);
                     if (var4[var9] == '>') {
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.addToken(2, (byte)5);
                        ++var8;
                        this.lastOffset = this.lastKeyword = var8 + 1;
                        var1 = 0;
                     } else {
                        this.addToken(var8 - this.lastOffset, (byte)8);
                        this.addToken(1, (byte)9);
                        this.lastOffset = this.lastKeyword = var9;
                     }
                     break label127;
                  default:
                     var7 = false;
                     if (!Character.isLetterOrDigit(var10) && var10 != '_' && var10 != '$') {
                        this.doKeyword(var2, var8, var10);
                        if (var10 != ' ') {
                           this.addToken(var8 - this.lastOffset, (byte)8);
                           this.addToken(1, (byte)9);
                           this.lastOffset = this.lastKeyword = var9;
                        }
                     }
                     break label127;
                  }
               default:
                  throw new InternalError("Invalid state: " + var1);
               }
            }

            switch(var1) {
            case 3:
               this.addToken(var6 - this.lastOffset, (byte)3);
               break;
            case 4:
               this.addToken(var6 - this.lastOffset, (byte)4);
               break;
            case 7:
               this.addToken(var6 - this.lastOffset, (byte)10);
               var1 = 0;
               break;
            case 100:
               this.doKeyword(var2, var6, '\u0000');
               this.addToken(var6 - this.lastOffset, (byte)8);
               break;
            default:
               this.addToken(var6 - this.lastOffset, var1);
            }

            return var1;
         }

         ++var8;
      }
   }

   private boolean doKeyword(Segment var1, int var2, char var3) {
      int var4 = var2 + 1;
      int var5 = var2 - this.lastKeyword;
      byte var6 = keywords.lookup(var1, this.lastKeyword, var5);
      if (var6 != 0) {
         if (this.lastKeyword != this.lastOffset) {
            this.addToken(this.lastKeyword - this.lastOffset, (byte)8);
         }

         this.addToken(var5, var6);
         this.lastOffset = var2;
      }

      this.lastKeyword = var4;
      return false;
   }

   static {
      keywords.add("function", (byte)7);
      keywords.add("class", (byte)7);
      keywords.add("var", (byte)7);
      keywords.add("require", (byte)7);
      keywords.add("include", (byte)7);
      keywords.add("else", (byte)6);
      keywords.add("elseif", (byte)6);
      keywords.add("do", (byte)6);
      keywords.add("for", (byte)6);
      keywords.add("if", (byte)6);
      keywords.add("endif", (byte)6);
      keywords.add("in", (byte)6);
      keywords.add("new", (byte)6);
      keywords.add("return", (byte)6);
      keywords.add("while", (byte)6);
      keywords.add("endwhile", (byte)6);
      keywords.add("with", (byte)6);
      keywords.add("break", (byte)6);
      keywords.add("switch", (byte)6);
      keywords.add("case", (byte)6);
      keywords.add("continue", (byte)6);
      keywords.add("default", (byte)6);
      keywords.add("echo", (byte)6);
      keywords.add("false", (byte)6);
      keywords.add("this", (byte)6);
      keywords.add("true", (byte)6);
      keywords.add("array", (byte)6);
      keywords.add("extends", (byte)6);
   }
}
