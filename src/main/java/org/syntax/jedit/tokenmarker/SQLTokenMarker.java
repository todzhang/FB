package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;

public class SQLTokenMarker extends TokenMarker {
   private int offset;
   private int lastOffset;
   private int lastKeyword;
   private int length;
   protected boolean isTSQL;
   private KeywordMap keywords;
   private char literalChar;

   public SQLTokenMarker(KeywordMap var1) {
      this(var1, false);
   }

   public SQLTokenMarker(KeywordMap var1, boolean var2) {
      this.isTSQL = false;
      this.literalChar = 0;
      this.keywords = var1;
      this.isTSQL = var2;
   }

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      this.offset = this.lastOffset = this.lastKeyword = var2.offset;
      this.length = var2.count + this.offset;

      label98:
      for(int var4 = this.offset; var4 < this.length; ++var4) {
         switch(var2.array[var4]) {
         case '\t':
         case ' ':
            if (var1 == 0) {
               this.searchBack(var2, var4, false);
            }
            break;
         case '!':
            if (this.isTSQL && var1 == 0 && this.length - var4 >= 2 && (var2.array[var4 + 1] == '=' || var2.array[var4 + 1] == '<' || var2.array[var4 + 1] == '>')) {
               this.searchBack(var2, var4);
               this.addToken(1, (byte)9);
               this.lastOffset = var4 + 1;
            }
            break;
         case '"':
         case '\'':
            if (var1 == 0) {
               var1 = 3;
               this.literalChar = var2.array[var4];
               this.addToken(var4 - this.lastOffset, (byte)0);
               this.lastOffset = var4;
            } else if (var1 == 3 && this.literalChar == var2.array[var4]) {
               var1 = 0;
               this.literalChar = 0;
               this.addToken(var4 + 1 - this.lastOffset, (byte)3);
               this.lastOffset = var4 + 1;
            }
            break;
         case '%':
         case '&':
         case '+':
         case '<':
         case '=':
         case '>':
         case '^':
         case '|':
         case '~':
            if (var1 == 0) {
               this.searchBack(var2, var4);
               this.addToken(1, (byte)9);
               this.lastOffset = var4 + 1;
            }
            break;
         case '(':
         case ')':
         case ',':
         case '.':
            if (var1 == 0) {
               this.searchBack(var2, var4);
               this.addToken(1, (byte)0);
               this.lastOffset = var4 + 1;
            }
            break;
         case '*':
            if (var1 == 1 && this.length - var4 >= 1 && var2.array[var4 + 1] == '/') {
               var1 = 0;
               ++var4;
               this.addToken(var4 + 1 - this.lastOffset, (byte)1);
               this.lastOffset = var4 + 1;
            } else if (var1 == 0) {
               this.searchBack(var2, var4);
               this.addToken(1, (byte)9);
               this.lastOffset = var4 + 1;
            }
            break;
         case '-':
            if (var1 == 0) {
               if (this.length - var4 >= 2 && var2.array[var4 + 1] == '-') {
                  this.searchBack(var2, var4);
                  this.addToken(this.length - var4, (byte)1);
                  this.lastOffset = this.length;
                  break label98;
               }

               this.searchBack(var2, var4);
               this.addToken(1, (byte)9);
               this.lastOffset = var4 + 1;
            }
            break;
         case '/':
            if (var1 == 0) {
               if (this.length - var4 >= 2 && var2.array[var4 + 1] == '*') {
                  this.searchBack(var2, var4);
                  var1 = 1;
                  this.lastOffset = var4++;
               } else {
                  this.searchBack(var2, var4);
                  this.addToken(1, (byte)9);
                  this.lastOffset = var4 + 1;
               }
            }
            break;
         case ':':
            if (var1 == 0) {
               this.addToken(var4 + 1 - this.lastOffset, (byte)5);
               this.lastOffset = var4 + 1;
            }
            break;
         case '[':
            if (var1 == 0) {
               this.searchBack(var2, var4);
               var1 = 3;
               this.literalChar = '[';
               this.lastOffset = var4;
            }
            break;
         case ']':
            if (var1 == 3 && this.literalChar == '[') {
               var1 = 0;
               this.literalChar = 0;
               this.addToken(var4 + 1 - this.lastOffset, (byte)3);
               this.lastOffset = var4 + 1;
            }
         }
      }

      if (var1 == 0) {
         this.searchBack(var2, this.length, false);
      }

      if (this.lastOffset != this.length) {
         this.addToken(this.length - this.lastOffset, var1);
      }

      return var1;
   }

   private void searchBack(Segment var1, int var2) {
      this.searchBack(var1, var2, true);
   }

   private void searchBack(Segment var1, int var2, boolean var3) {
      int var4 = var2 - this.lastKeyword;
      byte var5 = this.keywords.lookup(var1, this.lastKeyword, var4);
      if (var5 != 0) {
         if (this.lastKeyword != this.lastOffset) {
            this.addToken(this.lastKeyword - this.lastOffset, (byte)0);
         }

         this.addToken(var4, var5);
         this.lastOffset = var2;
      }

      this.lastKeyword = var2 + 1;
      if (var3 && this.lastOffset < var2) {
         this.addToken(var2 - this.lastOffset, (byte)0);
      }

   }
}
