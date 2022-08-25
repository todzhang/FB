package ddb.web.util;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HTMLUtils {
   public static final String classVersion = "5.5";
   public static final String BREAK = "<br>";
   public static final String SEPARATOR = System.getProperty("line.separator");
   public static final String WRAP_DELIMITERS = "\t\n\r ";
   public static final String EOLN_DELIMITERS = "\n\r";
   protected static final String[] names = new String[256];
   protected static final Map<Object, Object> rgbColorStrings;

   private HTMLUtils() {
   }

   public static String decodeURL(String s) {
      try {
         return URLDecoder.decode(s, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   /** @deprecated */
   public static String encodeURL(String s) {
      try {
         return URLEncoder.encode(s, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   public static String escapeText(String text) {
      char[] buffer = text.toCharArray();
      StringBuffer result = new StringBuffer();
      boolean space = false;

      for(int i = 0; i < buffer.length; ++i) {
         if (buffer[i] == ' ') {
            if (space) {
               result.append("&nbsp;");
            } else {
               space = true;
               result.append(buffer[i]);
            }
         } else {
            space = false;
            String name = names[buffer[i]];
            if (name == null) {
               result.append(buffer[i]);
            } else {
               result.append(name);
            }
         }
      }

      return result.toString();
   }

   public static String toHTML(Color color) {
      return HTMLColor.toHTML(color);
   }

   public static StringBuffer toPreHTMLText(String text, int maxLineLength) {
      char[] buffer = text.toCharArray();
      StringBuffer result = new StringBuffer();
      StringBuffer line = new StringBuffer();
      StringBuffer link = null;
      int offset = 0;
      int lastSpace = -1;
      int count = 0;

      for(int i = 0; i < buffer.length; ++i) {
         if (buffer[i] == ' ') {
            lastSpace = count;
         } else if (buffer[i] == 'h' && i + 6 < buffer.length && buffer[i + 1] == 't' && buffer[i + 2] == 't' && buffer[i + 3] == 'p' && buffer[i + 4] == ':' && buffer[i + 5] == '/' && buffer[i + 6] == '/') {
            link = new StringBuffer();
            line.append("<A HREF=\"");
         }

         if (link != null && (buffer[i] == ' ' || buffer[i] == '\r' || buffer[i] == '\n')) {
            line.append("\">");
            line.append(link);
            line.append("</A>");
            link = null;
         }

         line.append(buffer[i]);
         ++count;
         if (link != null) {
            link.append(buffer[i]);
         }

         if (buffer[i] != '\r' && buffer[i] != '\n') {
            if (count + offset == maxLineLength) {
               if (lastSpace >= 0) {
                  line.insert(lastSpace + 1, SEPARATOR);
                  offset = maxLineLength - (lastSpace + offset);
                  lastSpace = -1;
                  count = 0;
                  result.append(line);
                  line = new StringBuffer();
               } else if (link == null) {
                  line.append(SEPARATOR);
                  offset = 0;
                  lastSpace = -1;
                  count = 0;
                  result.append(line);
                  line = new StringBuffer();
               }
            }
         } else {
            lastSpace = -1;
            count = 0;
            result.append(line);
            line = new StringBuffer();
            offset = 0;
         }
      }

      if (line != null) {
         result.append(line);
         if (link != null) {
            result.append("\">");
            result.append(link);
            result.append("</A>");
         }
      }

      return result;
   }

   public static String unescapeText(String text) {
      char[] buffer = text.toCharArray();
      StringBuffer result = new StringBuffer();

      for(int i = 0; i < buffer.length; ++i) {
         char ch = buffer[i];
         if (buffer[i] == '&') {
            try {
               int end = text.indexOf(59, i);
               if (end != -1) {
                  String name = text.substring(i, end + 1);
                  if (name.charAt(1) == '#') {
                     name = text.substring(i + 2, end);
                     ch = (char)Integer.parseInt(name);
                     i = end;
                  } else {
                     for(int j = 0; j < names.length; ++j) {
                        if (name.equals(names[j])) {
                           ch = (char)j;
                           i = end;
                           break;
                        }
                     }
                  }
               }
            } catch (ArrayIndexOutOfBoundsException var8) {
            } catch (NumberFormatException var9) {
            }
         }

         result.append(ch);
      }

      return result.toString();
   }

   public static String wrapText(String text, int column) {
      int minLineWidth = column / 2;
      String token = null;
      boolean firstLine = true;
      boolean continuation = false;
      if (text != null && text.length() != 0) {
         StringBuffer sb = new StringBuffer();
         StringTokenizer st = new StringTokenizer(text, "\n\r");

         while(true) {
            while(true) {
               while(st.hasMoreTokens() || continuation) {
                  if (!continuation) {
                     token = st.nextToken();
                  }

                  int numChars = token.length();
                  if (numChars > 0) {
                     if (firstLine) {
                        firstLine = false;
                     } else {
                        sb.append("<br>");
                     }

                     if (numChars > column) {
                        for(numChars = column; "\t\n\r ".indexOf(token.charAt(numChars)) == -1 && numChars > minLineWidth; --numChars) {
                        }

                        if (numChars == minLineWidth && "\t\n\r ".indexOf(token.charAt(numChars)) == -1) {
                           numChars = column;
                        }

                        String partialLine = token.substring(numChars);
                        token = token.substring(0, numChars).trim();
                        sb.append(token);
                        token = partialLine.trim();
                        continuation = token.length() > 0;
                     } else {
                        sb.append(token);
                        continuation = false;
                     }
                  } else {
                     continuation = false;
                  }
               }

               return sb.toString();
            }
         }
      } else {
         return text;
      }
   }

   static {
      names[34] = "&quot;";
      names[38] = "&amp;";
      names[60] = "&lt;";
      names[62] = "&gt;";
      names[160] = "&nbsp;";
      names[161] = "&iexcl;";
      names[162] = "&cent;";
      names[163] = "&pound;";
      names[164] = "&curren;";
      names[165] = "&yen;";
      names[166] = "&brvbar;";
      names[167] = "&sect;";
      names[168] = "&uml;";
      names[169] = "&copy;";
      names[170] = "&ordf;";
      names[171] = "&laquo;";
      names[172] = "&not;";
      names[173] = "&shy;";
      names[174] = "&reg;";
      names[175] = "&macr;";
      names[176] = "&deg;";
      names[177] = "&plusmn;";
      names[178] = "&sup2;";
      names[179] = "&sup3;";
      names[180] = "&acute;";
      names[181] = "&micro;";
      names[182] = "&para;";
      names[183] = "&middot;";
      names[184] = "&cedil;";
      names[185] = "&sup1;";
      names[186] = "&ordm;";
      names[187] = "&raquo;";
      names[188] = "&frac14;";
      names[189] = "&frac12;";
      names[190] = "&frac34;";
      names[191] = "&iquest;";
      names[192] = "&Agrave;";
      names[193] = "&Aacute;";
      names[194] = "&Acirc;";
      names[195] = "&Atilde;";
      names[196] = "&Auml;";
      names[197] = "&Aring;";
      names[198] = "&AElig;";
      names[199] = "&Ccedil;";
      names[200] = "&Egrave;";
      names[201] = "&Eacute;";
      names[202] = "&Ecirc;";
      names[203] = "&Euml;";
      names[204] = "&Igrave;";
      names[205] = "&Iacute;";
      names[206] = "&Icirc;";
      names[207] = "&Iuml;";
      names[208] = "&ETH;";
      names[209] = "&Ntilde;";
      names[210] = "&Ograve;";
      names[211] = "&Oacute;";
      names[212] = "&Ocirc;";
      names[213] = "&Otilde;";
      names[214] = "&Ouml;";
      names[215] = "&times;";
      names[216] = "&Oslash;";
      names[217] = "&Ugrave;";
      names[218] = "&Uacute;";
      names[219] = "&Ucirc;";
      names[220] = "&Uuml;";
      names[221] = "&Yacute;";
      names[222] = "&THORN;";
      names[223] = "&szlig;";
      names[224] = "&agrave;";
      names[225] = "&aacute;";
      names[226] = "&acirc;";
      names[227] = "&atilde;";
      names[228] = "&auml;";
      names[229] = "&aring;";
      names[230] = "&aelig;";
      names[231] = "&ccedil;";
      names[232] = "&egrave;";
      names[233] = "&eacute;";
      names[234] = "&ecirc;";
      names[235] = "&euml;";
      names[236] = "&igrave;";
      names[237] = "&iacute;";
      names[238] = "&icirc;";
      names[239] = "&iuml;";
      names[240] = "&eth;";
      names[241] = "&ntilde;";
      names[242] = "&ograve;";
      names[243] = "&oacute;";
      names[244] = "&ocirc;";
      names[245] = "&otilde;";
      names[246] = "&ouml;";
      names[247] = "&divide;";
      names[248] = "&oslash;";
      names[249] = "&ugrave;";
      names[250] = "&uacute;";
      names[251] = "&ucirc;";
      names[252] = "&uuml;";
      names[253] = "&yacute;";
      names[254] = "&thorn;";
      names[255] = "&yuml;";
      rgbColorStrings = new HashMap();
   }
}
