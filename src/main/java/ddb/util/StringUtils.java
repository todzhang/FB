package ddb.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {
   public static final String classVersion = "4.3.3";
   public static final int LEFT_PAD = 1;
   public static final int RIGHT_PAD = 2;

   private StringUtils() {
   }

   public static String alphanum(String s) {
      String r = s;
      if (s != null) {
         r = transliterate(s, "0123456789-", "0");
         r = transliterate(r, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "A");
         r = transliterate(r, "abcdefghijklmnopqrstuvwxyz", "a");
      }

      return r;
   }

   public static final StringBuffer append(StringBuffer text, String format, int value) {
      int pos = 0;
      int length = format.length();
      boolean formatting = false;
      boolean leadingZeroes = false;

      for(int digits = 0; pos < length; ++pos) {
         char c = format.charAt(pos);
         if (!formatting) {
            if (c == '%') {
               formatting = true;
               leadingZeroes = false;
               digits = 0;
            } else {
               text.append(c);
            }
         } else if (c >= '0' && c <= '9') {
            if (digits == 0) {
               if (c == '0') {
                  leadingZeroes = true;
               } else {
                  digits = c - 48;
               }
            } else {
               digits *= 10;
               digits += c - 48;
            }
         } else if (c != 'd') {
            if (c == '%') {
               text.append('%');
               formatting = false;
            }
         } else {
            String valueString = String.valueOf(value);
            if (digits > 0) {
               int numPad = digits - valueString.length();
               if (numPad > 0) {
                  int i;
                  if (leadingZeroes) {
                     for(i = 0; i < numPad; ++i) {
                        text.append('0');
                     }
                  } else {
                     for(i = 0; i < numPad; ++i) {
                        text.append(' ');
                     }
                  }
               }
            }

            text.append(valueString);
            formatting = false;
         }
      }

      return text;
   }

   public static String breakCap(String s) {
      String result = s;
      if (s != null && s.length() > 1) {
         StringBuffer sb = new StringBuffer();
         char[] ca = s.toCharArray();
         sb.append(ca[0]);

         for(int i = 1; i < ca.length; ++i) {
            if (Character.isUpperCase(ca[i]) && Character.isLowerCase(ca[i - 1])) {
               sb.append(" ");
            }

            sb.append(ca[i]);
         }

         result = sb.toString();
      }

      return result;
   }

   public static String capitalize(String s) {
      String result = s;
      if (s != null && s.length() > 0) {
         char[] ca = s.toCharArray();
         ca[0] = Character.toUpperCase(ca[0]);
         result = new String(ca);
      }

      return result;
   }

   public static final boolean containsOnlyAlphaNumerics(String text) {
      int length = text.length();

      for(int i = 0; i < length; ++i) {
         char ch = text.charAt(i);
         if (!Character.isLetterOrDigit(ch)) {
            return false;
         }
      }

      return true;
   }

   public static final boolean containsOnlyDigits(String text) {
      int length = text.length();

      for(int i = 0; i < length; ++i) {
         char ch = text.charAt(i);
         if (!Character.isDigit(ch)) {
            return false;
         }
      }

      return true;
   }

   public static final boolean containsOnlyHexDigits(String text) {
      int length = text.length();

      for(int i = 0; i < length; ++i) {
         char ch = text.charAt(i);
         if (!Character.isDigit(ch) && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F')) {
            return false;
         }
      }

      return true;
   }

   public static final boolean containsOnlyLetters(String text) {
      int length = text.length();

      for(int i = 0; i < length; ++i) {
         char ch = text.charAt(i);
         if (!Character.isLetter(ch)) {
            return false;
         }
      }

      return true;
   }

   public static final int count(String text, char ch) {
      int count = 0;

      for(int i = 0; i < text.length(); ++i) {
         if (text.charAt(i) == ch) {
            ++count;
         }
      }

      return count;
   }

   public static final boolean endsWithIgnoreCase(String text, String suffix) {
      return startsWithIgnoreCase(text, suffix, text.length() - suffix.length());
   }

   public static int indexOf(String text, String find, int offset, boolean ignoreCase, boolean wholeWord) {
      int findLength = find.length();
      int textLength = text.length();
      if (offset >= textLength) {
         return textLength == 0 && offset == 0 && findLength == 0 ? 0 : -1;
      } else if (offset < 0) {
         return -1;
      } else if (findLength == 0) {
         return offset;
      } else {
         int end = textLength - findLength;

         for(int i = offset; i <= end; ++i) {
            if (text.regionMatches(ignoreCase, i, find, 0, findLength)) {
               if (!wholeWord) {
                  return i;
               }

               if ((i == 0 || !Character.isLetterOrDigit(text.charAt(i - 1))) && (i == end || !Character.isLetterOrDigit(text.charAt(i + findLength)))) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static int indexOf(StringBuffer text, String find) {
      return indexOf(text, find, 0);
   }

   public static int indexOf(StringBuffer text, String find, int offset) {
      int max = text.length() - find.length();
      if (offset >= text.length()) {
         return text.length() == 0 && offset == 0 && find.length() == 0 ? 0 : -1;
      } else {
         if (offset < 0) {
            offset = 0;
         }

         if (find.length() == 0) {
            return offset;
         } else {
            char first = find.charAt(0);
            int i = offset;

            while(true) {
               while(i > max || text.charAt(i) == first) {
                  if (i > max) {
                     return -1;
                  }

                  int j = i + 1;
                  int end = j + find.length() - 1;
                  int var8 = 1;

                  do {
                     if (j >= end) {
                        return i;
                     }
                  } while(text.charAt(j++) == find.charAt(var8++));

                  ++i;
               }

               ++i;
            }
         }
      }
   }

   public static int indexOfIgnoreCase(String text, String find) {
      return indexOf(text, find, 0, true, false);
   }

   public static int lastIndexOf(String text, String find, int fromIndex, boolean ignoreCase, boolean wholeWord) {
      int findLength = find.length();
      int textLength = text.length();
      if (fromIndex >= textLength) {
         return textLength == 0 && fromIndex == textLength && findLength == 0 ? 0 : -1;
      } else if (fromIndex < 0) {
         return -1;
      } else if (findLength == 0) {
         return fromIndex;
      } else {
         int end = 0;

         for(int i = fromIndex; i >= end; --i) {
            if (text.regionMatches(ignoreCase, i, find, 0, findLength)) {
               if (!wholeWord) {
                  return i;
               }

               if ((i == 0 || !Character.isLetterOrDigit(text.charAt(i - 1))) && (i + findLength == text.length() || !Character.isLetterOrDigit(text.charAt(i + findLength)))) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static int lastIndexOfIgnoreCase(String text, String find) {
      return lastIndexOf(text, find, text.length(), true, false);
   }

   public static final StringBuffer pad(StringBuffer text, char padChar, int length, int location) {
      if (location == 1) {
         while(text.length() < length) {
            text.insert(0, padChar);
         }
      } else {
         if (location != 2) {
            throw new IllegalArgumentException("Location must be LEFT_PAD or RIGHT_PAD!");
         }

         while(text.length() < length) {
            text.append(padChar);
         }
      }

      return text;
   }

   public static final String pad(String text, char padChar, int length, int location) {
      return pad(new StringBuffer(text), padChar, length, location).toString();
   }

   public static final StringBuffer pad(StringBuffer text, int length, int location) {
      return pad(text, ' ', length, location);
   }

   public static final String pad(String text, int length, int location) {
      return pad(new StringBuffer(text), ' ', length, location).toString();
   }

   public static final StringBuffer pad(StringBuffer text, int length) {
      return pad((StringBuffer)text, ' ', length, 2);
   }

   public static final String pad(String s, int length) {
      return pad((String)s, length, 2);
   }

   public static StringBuffer replaceAll(StringBuffer text, String find, String replacement) {
      if (find.length() == 0) {
         throw new IllegalArgumentException("The string to find cannot be empty!");
      } else {
         for(int index = 0; index <= text.length() - find.length(); index += replacement.length()) {
            index = indexOf(text, find, index);
            if (index == -1) {
               return text;
            }

            text.replace(index, index + find.length(), replacement);
         }

         return text;
      }
   }

   public static String replaceAll(String text, String find, String replacement) {
      return replaceAll(new StringBuffer(text), find, replacement).toString();
   }

   public static final boolean startsWithIgnoreCase(String text, String prefix) {
      return startsWithIgnoreCase(text, prefix, 0);
   }

   public static final boolean startsWithIgnoreCase(String text, String find, int offset) {
      return text.regionMatches(true, offset, find, 0, find.length());
   }

   public static String transliterate(String s, String from, String to) {
      String result = s;
      if (s != null && from != null && to != null) {
         char[] af = from.toCharArray();
         char[] at = to.toCharArray();
         char[] a = s.toCharArray();

         for(int i = 0; i < a.length; ++i) {
            for(int j = 0; j < af.length; ++j) {
               if (a[i] == af[j]) {
                  a[i] = at[j % at.length];
                  break;
               }
            }
         }

         result = new String(a);
      }

      return result;
   }

   public static final String trimBegin(String text) {
      if (text != null) {
         int i;
         for(i = 0; i < text.length() && text.charAt(i) <= ' '; ++i) {
         }

         if (i < text.length()) {
            text = text.substring(i, text.length());
         } else {
            text = "";
         }
      }

      return text;
   }

   public static final String trimEnd(String text) {
      if (text != null) {
         int i;
         for(i = text.length() - 1; i >= 0 && text.charAt(i) <= ' '; --i) {
         }

         if (i >= 0) {
            text = text.substring(0, i + 1);
         } else {
            text = "";
         }
      }

      return text;
   }

   public static String truncateString(String s, int maxLength) {
      if (s.length() > maxLength) {
         String newS = s.substring(0, maxLength);
         int lastWhitespaceLoc = newS.lastIndexOf(" ");
         if (lastWhitespaceLoc == -1) {
            lastWhitespaceLoc = maxLength;
         }

         newS = s.substring(0, lastWhitespaceLoc);
         return newS + "...";
      } else {
         return s;
      }
   }

   public static final String valueOf(String format, int value) {
      return append(new StringBuffer(), format, value).toString();
   }

   public static final String valueOf(Throwable throwable) {
      StringWriter writer = new StringWriter();
      PrintWriter out = new PrintWriter(writer);
      throwable.printStackTrace(out);
      writer.flush();
      return writer.toString();
   }
}
