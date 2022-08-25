package org.syntax.jedit;

import javax.swing.text.Segment;

public class KeywordMap {
   protected int mapLength;
   private KeywordMap.Keyword[] map;
   private boolean ignoreCase;

   public KeywordMap(boolean var1) {
      this(var1, 52);
      this.ignoreCase = var1;
   }

   public KeywordMap(boolean var1, int var2) {
      this.mapLength = var2;
      this.ignoreCase = var1;
      this.map = new KeywordMap.Keyword[var2];
   }

   public byte lookup(Segment var1, int var2, int var3) {
      if (var3 == 0) {
         return 0;
      } else {
         KeywordMap.Keyword var4 = this.map[this.getSegmentMapKey(var1, var2, var3)];

         while(var4 != null) {
            if (var3 != var4.keyword.length) {
               var4 = var4.next;
            } else {
               if (SyntaxUtilities.regionMatches(this.ignoreCase, var1, var2, var4.keyword)) {
                  return var4.id;
               }

               var4 = var4.next;
            }
         }

         return 0;
      }
   }

   public void add(String var1, byte var2) {
      int var3 = this.getStringMapKey(var1);
      this.map[var3] = new KeywordMap.Keyword(var1.toCharArray(), var2, this.map[var3]);
   }

   public boolean getIgnoreCase() {
      return this.ignoreCase;
   }

   public void setIgnoreCase(boolean var1) {
      this.ignoreCase = var1;
   }

   protected int getStringMapKey(String var1) {
      return (Character.toUpperCase(var1.charAt(0)) + Character.toUpperCase(var1.charAt(var1.length() - 1))) % this.mapLength;
   }

   protected int getSegmentMapKey(Segment var1, int var2, int var3) {
      return (Character.toUpperCase(var1.array[var2]) + Character.toUpperCase(var1.array[var2 + var3 - 1])) % this.mapLength;
   }

   class Keyword {
      public char[] keyword;
      public byte id;
      public KeywordMap.Keyword next;

      public Keyword(char[] var2, byte var3, KeywordMap.Keyword var4) {
         this.keyword = var2;
         this.id = var3;
         this.next = var4;
      }
   }
}
