package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;
import org.syntax.jedit.SyntaxUtilities;

public class PythonTokenMarker extends TokenMarker {
   private static final byte TRIPLEQUOTE1 = 100;
   private static final byte TRIPLEQUOTE2 = 126;
   private static KeywordMap pyKeywords;
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

      label84:
      for(int var8 = var5; var8 < var6; ++var8) {
         int var9 = var8 + 1;
         char var10 = var4[var8];
         if (var10 == '\\') {
            var7 = !var7;
         } else {
            switch(var1) {
            case 0:
               switch(var10) {
               case '"':
                  this.doKeyword(var2, var8, var10);
                  if (var7) {
                     var7 = false;
                  } else {
                     this.addToken(var8 - this.lastOffset, var1);
                     if (SyntaxUtilities.regionMatches(false, var2, var9, "\"\"")) {
                        var1 = 100;
                     } else {
                        var1 = 3;
                     }

                     this.lastOffset = this.lastKeyword = var8;
                  }
                  continue;
               case '#':
                  if (!var7) {
                     this.doKeyword(var2, var8, var10);
                     this.addToken(var8 - this.lastOffset, var1);
                     this.addToken(var6 - var8, (byte)1);
                     this.lastOffset = this.lastKeyword = var6;
                     break label84;
                  }

                  var7 = false;
                  continue;
               case '\'':
                  this.doKeyword(var2, var8, var10);
                  if (var7) {
                     var7 = false;
                  } else {
                     this.addToken(var8 - this.lastOffset, var1);
                     if (SyntaxUtilities.regionMatches(false, var2, var9, "''")) {
                        var1 = 126;
                     } else {
                        var1 = 4;
                     }

                     this.lastOffset = this.lastKeyword = var8;
                  }
                  continue;
               default:
                  var7 = false;
                  if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                     this.doKeyword(var2, var8, var10);
                  }
                  continue;
               }
            case 3:
               if (var7) {
                  var7 = false;
               } else if (var10 == '"') {
                  this.addToken(var9 - this.lastOffset, var1);
                  var1 = 0;
                  this.lastOffset = this.lastKeyword = var9;
               }
               break;
            case 4:
               if (var7) {
                  var7 = false;
               } else if (var10 == '\'') {
                  this.addToken(var9 - this.lastOffset, (byte)3);
                  var1 = 0;
                  this.lastOffset = this.lastKeyword = var9;
               }
               break;
            case 100:
               if (var7) {
                  var7 = false;
               } else if (SyntaxUtilities.regionMatches(false, var2, var8, "\"\"\"")) {
                  var8 += 4;
                  this.addToken(var8 - this.lastOffset, (byte)3);
                  var1 = 0;
                  this.lastOffset = this.lastKeyword = var8;
               }
               break;
            case 126:
               if (var7) {
                  var7 = false;
               } else if (SyntaxUtilities.regionMatches(false, var2, var8, "'''")) {
                  var8 += 4;
                  this.addToken(var8 - this.lastOffset, (byte)3);
                  var1 = 0;
                  this.lastOffset = this.lastKeyword = var8;
               }
               break;
            default:
               throw new InternalError("Invalid state: " + var1);
            }
         }
      }

      switch(var1) {
      case 0:
         this.doKeyword(var2, var6, '\u0000');
      default:
         this.addToken(var6 - this.lastOffset, var1);
         break;
      case 100:
      case 126:
         this.addToken(var6 - this.lastOffset, (byte)3);
      }

      return var1;
   }

   public static KeywordMap getKeywords() {
      if (pyKeywords == null) {
         pyKeywords = new KeywordMap(false);
         pyKeywords.add("and", (byte)8);
         pyKeywords.add("not", (byte)8);
         pyKeywords.add("or", (byte)8);
         pyKeywords.add("if", (byte)6);
         pyKeywords.add("for", (byte)6);
         pyKeywords.add("assert", (byte)6);
         pyKeywords.add("break", (byte)6);
         pyKeywords.add("continue", (byte)6);
         pyKeywords.add("elif", (byte)6);
         pyKeywords.add("else", (byte)6);
         pyKeywords.add("except", (byte)6);
         pyKeywords.add("exec", (byte)6);
         pyKeywords.add("finally", (byte)6);
         pyKeywords.add("raise", (byte)6);
         pyKeywords.add("return", (byte)6);
         pyKeywords.add("try", (byte)6);
         pyKeywords.add("while", (byte)6);
         pyKeywords.add("def", (byte)7);
         pyKeywords.add("class", (byte)7);
         pyKeywords.add("del", (byte)7);
         pyKeywords.add("from", (byte)7);
         pyKeywords.add("global", (byte)7);
         pyKeywords.add("import", (byte)7);
         pyKeywords.add("in", (byte)7);
         pyKeywords.add("is", (byte)7);
         pyKeywords.add("lambda", (byte)7);
         pyKeywords.add("pass", (byte)7);
         pyKeywords.add("print", (byte)7);
      }

      return pyKeywords;
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
