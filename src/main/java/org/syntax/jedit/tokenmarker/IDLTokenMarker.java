package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.KeywordMap;

public class IDLTokenMarker extends CTokenMarker {
   private static KeywordMap idlKeywords;

   public IDLTokenMarker() {
      super(true, getKeywords());
   }

   public static KeywordMap getKeywords() {
      if (idlKeywords == null) {
         idlKeywords = new KeywordMap(false);
         idlKeywords.add("any", (byte)8);
         idlKeywords.add("attribute", (byte)6);
         idlKeywords.add("boolean", (byte)8);
         idlKeywords.add("case", (byte)6);
         idlKeywords.add("char", (byte)8);
         idlKeywords.add("const", (byte)6);
         idlKeywords.add("context", (byte)6);
         idlKeywords.add("default", (byte)6);
         idlKeywords.add("double", (byte)8);
         idlKeywords.add("enum", (byte)8);
         idlKeywords.add("exception", (byte)6);
         idlKeywords.add("FALSE", (byte)4);
         idlKeywords.add("fixed", (byte)6);
         idlKeywords.add("float", (byte)8);
         idlKeywords.add("in", (byte)6);
         idlKeywords.add("inout", (byte)6);
         idlKeywords.add("interface", (byte)6);
         idlKeywords.add("long", (byte)8);
         idlKeywords.add("module", (byte)6);
         idlKeywords.add("Object", (byte)8);
         idlKeywords.add("octet", (byte)8);
         idlKeywords.add("oneway", (byte)6);
         idlKeywords.add("out", (byte)6);
         idlKeywords.add("raises", (byte)6);
         idlKeywords.add("readonly", (byte)6);
         idlKeywords.add("sequence", (byte)8);
         idlKeywords.add("short", (byte)8);
         idlKeywords.add("string", (byte)8);
         idlKeywords.add("struct", (byte)8);
         idlKeywords.add("switch", (byte)6);
         idlKeywords.add("TRUE", (byte)4);
         idlKeywords.add("typedef", (byte)8);
         idlKeywords.add("unsigned", (byte)8);
         idlKeywords.add("union", (byte)8);
         idlKeywords.add("void", (byte)8);
         idlKeywords.add("wchar", (byte)8);
         idlKeywords.add("wstring", (byte)8);
      }

      return idlKeywords;
   }
}
