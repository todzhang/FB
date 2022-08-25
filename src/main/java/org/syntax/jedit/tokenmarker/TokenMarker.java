package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;

public abstract class TokenMarker {
   protected Token firstToken;
   protected Token lastToken;
   protected TokenMarker.LineInfo[] lineInfo;
   protected int length;
   protected int lastLine = -1;
   protected boolean nextLineRequested;

   public Token markTokens(Segment var1, int var2) {
      if (var2 >= this.length) {
         throw new IllegalArgumentException("Tokenizing invalid line: " + var2);
      } else {
         this.lastToken = null;
         TokenMarker.LineInfo var3 = this.lineInfo[var2];
         TokenMarker.LineInfo var4;
         if (var2 == 0) {
            var4 = null;
         } else {
            var4 = this.lineInfo[var2 - 1];
         }

         byte var5 = var3.token;
         byte var6 = this.markTokensImpl(var4 == null ? 0 : var4.token, var1, var2);
         var3.token = var6;
         if (this.lastLine != var2 || !this.nextLineRequested) {
            this.nextLineRequested = var5 != var6;
         }

         this.lastLine = var2;
         this.addToken(0, (byte)127);
         return this.firstToken;
      }
   }

   protected abstract byte markTokensImpl(byte var1, Segment var2, int var3);

   public boolean supportsMultilineTokens() {
      return true;
   }

   public void insertLines(int var1, int var2) {
      if (var2 > 0) {
         this.length += var2;
         this.ensureCapacity(this.length);
         int var3 = var1 + var2;
         System.arraycopy(this.lineInfo, var1, this.lineInfo, var3, this.lineInfo.length - var3);

         for(int var4 = var1 + var2 - 1; var4 >= var1; --var4) {
            this.lineInfo[var4] = new TokenMarker.LineInfo();
         }

      }
   }

   public void deleteLines(int var1, int var2) {
      if (var2 > 0) {
         int var3 = var1 + var2;
         this.length -= var2;
         System.arraycopy(this.lineInfo, var3, this.lineInfo, var1, this.lineInfo.length - var3);
      }
   }

   public int getLineCount() {
      return this.length;
   }

   public boolean isNextLineRequested() {
      return this.nextLineRequested;
   }

   protected TokenMarker() {
   }

   protected void ensureCapacity(int var1) {
      if (this.lineInfo == null) {
         this.lineInfo = new TokenMarker.LineInfo[var1 + 1];
      } else if (this.lineInfo.length <= var1) {
         TokenMarker.LineInfo[] var2 = new TokenMarker.LineInfo[(var1 + 1) * 2];
         System.arraycopy(this.lineInfo, 0, var2, 0, this.lineInfo.length);
         this.lineInfo = var2;
      }

   }

   protected void addToken(int var1, byte var2) {
      if (var2 >= 100 && var2 <= 126) {
         throw new InternalError("Invalid id: " + var2);
      } else if (var1 != 0 || var2 == 127) {
         if (this.firstToken == null) {
            this.firstToken = new Token(var1, var2);
            this.lastToken = this.firstToken;
         } else if (this.lastToken == null) {
            this.lastToken = this.firstToken;
            this.firstToken.length = var1;
            this.firstToken.id = var2;
         } else if (this.lastToken.next == null) {
            this.lastToken.next = new Token(var1, var2);
            this.lastToken = this.lastToken.next;
         } else {
            this.lastToken = this.lastToken.next;
            this.lastToken.length = var1;
            this.lastToken.id = var2;
         }

      }
   }

   public class LineInfo {
      public byte token;
      public Object obj;

      public LineInfo() {
      }

      public LineInfo(byte var2, Object var3) {
         this.token = var2;
         this.obj = var3;
      }
   }
}
