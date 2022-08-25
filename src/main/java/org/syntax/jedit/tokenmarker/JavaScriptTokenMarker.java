package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.KeywordMap;

public class JavaScriptTokenMarker extends CTokenMarker {
   private static KeywordMap javaScriptKeywords;

   public JavaScriptTokenMarker() {
      super(false, getKeywords());
   }

   public static KeywordMap getKeywords() {
      if (javaScriptKeywords == null) {
         javaScriptKeywords = new KeywordMap(false);
         javaScriptKeywords.add("function", (byte)8);
         javaScriptKeywords.add("var", (byte)8);
         javaScriptKeywords.add("else", (byte)6);
         javaScriptKeywords.add("for", (byte)6);
         javaScriptKeywords.add("if", (byte)6);
         javaScriptKeywords.add("in", (byte)6);
         javaScriptKeywords.add("new", (byte)6);
         javaScriptKeywords.add("return", (byte)6);
         javaScriptKeywords.add("while", (byte)6);
         javaScriptKeywords.add("with", (byte)6);
         javaScriptKeywords.add("break", (byte)6);
         javaScriptKeywords.add("case", (byte)6);
         javaScriptKeywords.add("continue", (byte)6);
         javaScriptKeywords.add("default", (byte)6);
         javaScriptKeywords.add("false", (byte)5);
         javaScriptKeywords.add("this", (byte)5);
         javaScriptKeywords.add("true", (byte)5);
      }

      return javaScriptKeywords;
   }
}
