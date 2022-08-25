package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;
import org.syntax.jedit.SyntaxUtilities;

public class HTMLTokenMarker extends TokenMarker {
   public static final byte JAVASCRIPT = 100;
   private KeywordMap keywords;
   private boolean js;
   private int lastOffset;
   private int lastKeyword;

   public HTMLTokenMarker() {
      this(true);
   }

   public HTMLTokenMarker(boolean var1) {
      this.js = var1;
      this.keywords = JavaScriptTokenMarker.getKeywords();
   }

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      this.lastOffset = var5;
      this.lastKeyword = var5;
      int var6 = var2.count + var5;
      boolean var7 = false;

      label103:
      for(int var8 = var5; var8 < var6; ++var8) {
         int var9 = var8 + 1;
         char var10 = var4[var8];
         if (var10 == '\\') {
            var7 = !var7;
         } else {
            switch(var1) {
            case 0:
               var7 = false;
               switch(var10) {
               case '&':
                  this.addToken(var8 - this.lastOffset, var1);
                  this.lastOffset = this.lastKeyword = var8;
                  var1 = 7;
                  continue;
               case '<':
                  this.addToken(var8 - this.lastOffset, var1);
                  this.lastOffset = this.lastKeyword = var8;
                  if (SyntaxUtilities.regionMatches(false, var2, var9, "!--")) {
                     var8 += 3;
                     var1 = 1;
                  } else if (this.js && SyntaxUtilities.regionMatches(true, var2, var9, "script>")) {
                     this.addToken(8, (byte)6);
                     var8 += 8;
                     this.lastOffset = this.lastKeyword = var8;
                     var1 = 100;
                  } else {
                     var1 = 6;
                  }
               default:
                  continue;
               }
            case 1:
               var7 = false;
               if (SyntaxUtilities.regionMatches(false, var2, var8, "-->")) {
                  this.addToken(var8 + 3 - this.lastOffset, var1);
                  this.lastOffset = this.lastKeyword = var8 + 3;
                  var1 = 0;
               }
               break;
            case 2:
               var7 = false;
               if (var10 == '*' && var6 - var8 > 1 && var4[var9] == '/') {
                  var8 += 2;
                  this.addToken(var8 - this.lastOffset, (byte)2);
                  this.lastOffset = this.lastKeyword = var8;
                  var1 = 100;
               }
               break;
            case 3:
               if (var7) {
                  var7 = false;
               } else if (var10 == '"') {
                  this.addToken(var9 - this.lastOffset, (byte)3);
                  this.lastOffset = this.lastKeyword = var9;
                  var1 = 100;
               }
               break;
            case 4:
               if (var7) {
                  var7 = false;
               } else if (var10 == '\'') {
                  this.addToken(var9 - this.lastOffset, (byte)3);
                  this.lastOffset = this.lastKeyword = var9;
                  var1 = 100;
               }
               break;
            case 6:
               var7 = false;
               if (var10 == '>') {
                  this.addToken(var9 - this.lastOffset, var1);
                  this.lastOffset = this.lastKeyword = var9;
                  var1 = 0;
               }
               break;
            case 7:
               var7 = false;
               if (var10 == ';') {
                  this.addToken(var9 - this.lastOffset, var1);
                  this.lastOffset = this.lastKeyword = var9;
                  var1 = 0;
               }
               break;
            case 100:
               switch(var10) {
               case '"':
                  if (var7) {
                     var7 = false;
                  } else {
                     this.doKeyword(var2, var8, var10);
                     this.addToken(var8 - this.lastOffset, (byte)0);
                     this.lastOffset = this.lastKeyword = var8;
                     var1 = 3;
                  }
                  continue;
               case '\'':
                  if (var7) {
                     var7 = false;
                  } else {
                     this.doKeyword(var2, var8, var10);
                     this.addToken(var8 - this.lastOffset, (byte)0);
                     this.lastOffset = this.lastKeyword = var8;
                     var1 = 4;
                  }
                  continue;
               case '/':
                  var7 = false;
                  this.doKeyword(var2, var8, var10);
                  if (var6 - var8 > 1) {
                     this.addToken(var8 - this.lastOffset, (byte)0);
                     this.lastOffset = this.lastKeyword = var8;
                     if (var4[var9] == '/') {
                        this.addToken(var6 - var8, (byte)2);
                        this.lastOffset = this.lastKeyword = var6;
                        break label103;
                     }

                     if (var4[var9] == '*') {
                        var1 = 2;
                     }
                  }
                  continue;
               case '<':
                  var7 = false;
                  this.doKeyword(var2, var8, var10);
                  if (SyntaxUtilities.regionMatches(true, var2, var9, "/script>")) {
                     this.addToken(var8 - this.lastOffset, (byte)0);
                     this.addToken(9, (byte)6);
                     var8 += 9;
                     this.lastOffset = this.lastKeyword = var8;
                     var1 = 0;
                  }
                  continue;
               default:
                  var7 = false;
                  if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                     this.doKeyword(var2, var8, var10);
                  }
                  continue;
               }
            default:
               throw new InternalError("Invalid state: " + var1);
            }
         }
      }

      switch(var1) {
      case 3:
      case 4:
         this.addToken(var6 - this.lastOffset, (byte)10);
         var1 = 100;
         break;
      case 7:
         this.addToken(var6 - this.lastOffset, (byte)10);
         var1 = 0;
         break;
      case 100:
         this.doKeyword(var2, var6, '\u0000');
         this.addToken(var6 - this.lastOffset, (byte)0);
         break;
      default:
         this.addToken(var6 - this.lastOffset, var1);
      }

      return var1;
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
