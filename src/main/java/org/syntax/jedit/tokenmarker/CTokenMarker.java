package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;

public class CTokenMarker extends TokenMarker {
   private static KeywordMap cKeywords;
   private boolean cpp;
   private KeywordMap keywords;
   private int lastOffset;
   private int lastKeyword;

   public CTokenMarker() {
      this(true, getKeywords());
   }

   public CTokenMarker(boolean var1, KeywordMap var2) {
      this.cpp = var1;
      this.keywords = var2;
   }

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      this.lastOffset = var5;
      this.lastKeyword = var5;
      int var6 = var2.count + var5;
      boolean var7 = false;

      label107:
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
                     var1 = 3;
                     this.lastOffset = this.lastKeyword = var8;
                  }
                  continue;
               case '#':
                  if (var7) {
                     var7 = false;
                  } else if (this.cpp && !this.doKeyword(var2, var8, var10)) {
                     this.addToken(var8 - this.lastOffset, var1);
                     this.addToken(var6 - var8, (byte)7);
                     this.lastOffset = this.lastKeyword = var6;
                     break label107;
                  }
                  continue;
               case '\'':
                  this.doKeyword(var2, var8, var10);
                  if (var7) {
                     var7 = false;
                  } else {
                     this.addToken(var8 - this.lastOffset, var1);
                     var1 = 4;
                     this.lastOffset = this.lastKeyword = var8;
                  }
                  continue;
               case '/':
                  var7 = false;
                  this.doKeyword(var2, var8, var10);
                  if (var6 - var8 > 1) {
                     switch(var4[var9]) {
                     case '*':
                        this.addToken(var8 - this.lastOffset, var1);
                        this.lastOffset = this.lastKeyword = var8;
                        if (var6 - var8 > 2 && var4[var8 + 2] == '*') {
                           var1 = 2;
                        } else {
                           var1 = 1;
                        }
                        continue;
                     case '/':
                        this.addToken(var8 - this.lastOffset, var1);
                        this.addToken(var6 - var8, (byte)1);
                        this.lastOffset = this.lastKeyword = var6;
                        break label107;
                     }
                  }
                  continue;
               case ':':
                  if (this.lastKeyword == var5) {
                     if (!this.doKeyword(var2, var8, var10)) {
                        var7 = false;
                        this.addToken(var9 - this.lastOffset, (byte)5);
                        this.lastOffset = this.lastKeyword = var9;
                     }
                  } else if (this.doKeyword(var2, var8, var10)) {
                  }
                  continue;
               default:
                  var7 = false;
                  if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                     this.doKeyword(var2, var8, var10);
                  }
                  continue;
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
               break;
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
            default:
               throw new InternalError("Invalid state: " + var1);
            }
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

   public static KeywordMap getKeywords() {
      if (cKeywords == null) {
         cKeywords = new KeywordMap(false);
         cKeywords.add("char", (byte)8);
         cKeywords.add("double", (byte)8);
         cKeywords.add("enum", (byte)8);
         cKeywords.add("float", (byte)8);
         cKeywords.add("int", (byte)8);
         cKeywords.add("long", (byte)8);
         cKeywords.add("short", (byte)8);
         cKeywords.add("signed", (byte)8);
         cKeywords.add("struct", (byte)8);
         cKeywords.add("typedef", (byte)8);
         cKeywords.add("union", (byte)8);
         cKeywords.add("unsigned", (byte)8);
         cKeywords.add("void", (byte)8);
         cKeywords.add("auto", (byte)6);
         cKeywords.add("const", (byte)6);
         cKeywords.add("extern", (byte)6);
         cKeywords.add("register", (byte)6);
         cKeywords.add("static", (byte)6);
         cKeywords.add("volatile", (byte)6);
         cKeywords.add("break", (byte)6);
         cKeywords.add("case", (byte)6);
         cKeywords.add("continue", (byte)6);
         cKeywords.add("default", (byte)6);
         cKeywords.add("do", (byte)6);
         cKeywords.add("else", (byte)6);
         cKeywords.add("for", (byte)6);
         cKeywords.add("goto", (byte)6);
         cKeywords.add("if", (byte)6);
         cKeywords.add("return", (byte)6);
         cKeywords.add("sizeof", (byte)6);
         cKeywords.add("switch", (byte)6);
         cKeywords.add("while", (byte)6);
         cKeywords.add("asm", (byte)7);
         cKeywords.add("asmlinkage", (byte)7);
         cKeywords.add("far", (byte)7);
         cKeywords.add("huge", (byte)7);
         cKeywords.add("inline", (byte)7);
         cKeywords.add("near", (byte)7);
         cKeywords.add("pascal", (byte)7);
         cKeywords.add("true", (byte)4);
         cKeywords.add("false", (byte)4);
         cKeywords.add("NULL", (byte)4);
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
