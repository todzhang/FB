package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.KeywordMap;

public class CCTokenMarker extends CTokenMarker {
   private static KeywordMap ccKeywords;

   public CCTokenMarker() {
      super(true, getKeywords());
   }

   public static KeywordMap getKeywords() {
      if (ccKeywords == null) {
         ccKeywords = new KeywordMap(false);
         ccKeywords.add("and", (byte)8);
         ccKeywords.add("and_eq", (byte)8);
         ccKeywords.add("asm", (byte)7);
         ccKeywords.add("auto", (byte)6);
         ccKeywords.add("bitand", (byte)8);
         ccKeywords.add("bitor", (byte)8);
         ccKeywords.add("bool", (byte)8);
         ccKeywords.add("break", (byte)6);
         ccKeywords.add("case", (byte)6);
         ccKeywords.add("catch", (byte)6);
         ccKeywords.add("char", (byte)8);
         ccKeywords.add("class", (byte)8);
         ccKeywords.add("compl", (byte)8);
         ccKeywords.add("const", (byte)6);
         ccKeywords.add("const_cast", (byte)8);
         ccKeywords.add("continue", (byte)6);
         ccKeywords.add("default", (byte)6);
         ccKeywords.add("delete", (byte)6);
         ccKeywords.add("do", (byte)6);
         ccKeywords.add("double", (byte)8);
         ccKeywords.add("dynamic_cast", (byte)8);
         ccKeywords.add("else", (byte)6);
         ccKeywords.add("enum", (byte)8);
         ccKeywords.add("explicit", (byte)6);
         ccKeywords.add("export", (byte)7);
         ccKeywords.add("extern", (byte)7);
         ccKeywords.add("false", (byte)4);
         ccKeywords.add("float", (byte)8);
         ccKeywords.add("for", (byte)6);
         ccKeywords.add("friend", (byte)6);
         ccKeywords.add("goto", (byte)6);
         ccKeywords.add("if", (byte)6);
         ccKeywords.add("inline", (byte)6);
         ccKeywords.add("int", (byte)8);
         ccKeywords.add("long", (byte)8);
         ccKeywords.add("mutable", (byte)8);
         ccKeywords.add("namespace", (byte)7);
         ccKeywords.add("new", (byte)6);
         ccKeywords.add("not", (byte)8);
         ccKeywords.add("not_eq", (byte)8);
         ccKeywords.add("operator", (byte)8);
         ccKeywords.add("or", (byte)8);
         ccKeywords.add("or_eq", (byte)8);
         ccKeywords.add("private", (byte)6);
         ccKeywords.add("protected", (byte)6);
         ccKeywords.add("public", (byte)6);
         ccKeywords.add("register", (byte)6);
         ccKeywords.add("reinterpret_cast", (byte)8);
         ccKeywords.add("return", (byte)6);
         ccKeywords.add("short", (byte)8);
         ccKeywords.add("signed", (byte)8);
         ccKeywords.add("sizeof", (byte)6);
         ccKeywords.add("static", (byte)6);
         ccKeywords.add("static_cast", (byte)8);
         ccKeywords.add("struct", (byte)8);
         ccKeywords.add("switch", (byte)6);
         ccKeywords.add("template", (byte)8);
         ccKeywords.add("this", (byte)4);
         ccKeywords.add("throw", (byte)6);
         ccKeywords.add("true", (byte)4);
         ccKeywords.add("try", (byte)6);
         ccKeywords.add("typedef", (byte)8);
         ccKeywords.add("typeid", (byte)8);
         ccKeywords.add("typename", (byte)8);
         ccKeywords.add("union", (byte)8);
         ccKeywords.add("unsigned", (byte)8);
         ccKeywords.add("using", (byte)7);
         ccKeywords.add("virtual", (byte)6);
         ccKeywords.add("void", (byte)6);
         ccKeywords.add("volatile", (byte)6);
         ccKeywords.add("wchar_t", (byte)8);
         ccKeywords.add("while", (byte)6);
         ccKeywords.add("xor", (byte)8);
         ccKeywords.add("xor_eq", (byte)8);
         ccKeywords.add("NULL", (byte)4);
      }

      return ccKeywords;
   }
}
