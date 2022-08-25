package ddb.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class QuotedStringTokenizer extends StringTokenizer {
   private static final String __delim = "\t\n\r";
   private String _string;
   private String _delim;
   private boolean _returnQuotes;
   private boolean _returnTokens;
   private StringBuffer _token;
   private boolean _hasToken;
   private int _i;
   private int _lastStart;

   public QuotedStringTokenizer(String str, String delim, boolean returnTokens, boolean returnQuotes) {
      super("");
      this._delim = "\t\n\r";
      this._returnQuotes = false;
      this._returnTokens = false;
      this._hasToken = false;
      this._i = 0;
      this._lastStart = 0;
      this._string = str;
      if (delim != null) {
         this._delim = delim;
      }

      this._returnTokens = returnTokens;
      this._returnQuotes = returnQuotes;
      if (this._delim.indexOf(39) < 0 && this._delim.indexOf(34) < 0) {
         this._token = new StringBuffer(this._string.length() > 1024 ? 512 : this._string.length() / 2);
      } else {
         throw new Error("Can't use quotes as delimiters:  " + this._delim);
      }
   }

   public QuotedStringTokenizer(String str, String delim, boolean returnTokens) {
      this(str, delim, returnTokens, false);
   }

   public QuotedStringTokenizer(String str, String delim) {
      this(str, delim, false, false);
   }

   public QuotedStringTokenizer(String str) {
      this(str, (String)null, false, false);
   }

   @Override
   public boolean hasMoreTokens() {
      if (this._hasToken) {
         return true;
      } else {
         this._lastStart = this._i;
         QuotedStringTokenizer.State state = QuotedStringTokenizer.State.Start;
         boolean escape = false;

         while(this._i < this._string.length()) {
            char c = this._string.charAt(this._i++);
            switch(state) {
            case Start:
               if (this._delim.indexOf(c) >= 0) {
                  if (this._returnTokens) {
                     this._token.append(c);
                     return this._hasToken = true;
                  }
               } else if (c == '\'') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                     state = QuotedStringTokenizer.State.SingleQuote;
                  }
               } else if (c == '"') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                     state = QuotedStringTokenizer.State.DoubleQuote;
                  }
               } else {
                  this._token.append(c);
                  this._hasToken = true;
                  state = QuotedStringTokenizer.State.Token;
               }
               break;
            case Token:
               this._hasToken = true;
               if (this._delim.indexOf(c) >= 0) {
                  if (this._returnTokens) {
                     --this._i;
                     return this._hasToken;
                  }
               } else if (c == '\'') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  state = QuotedStringTokenizer.State.SingleQuote;
               } else if (c == '"') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  state = QuotedStringTokenizer.State.DoubleQuote;
               } else {
                  this._token.append(c);
               }
               break;
            case SingleQuote:
               this._hasToken = true;
               if (escape) {
                  escape = false;
                  if (c == '\\') {
                     escape = true;
                  } else if (c != '\'' && c != '"') {
                     this._token.append('\\');
                  }

                  this._token.append(c);
               } else if (c == '\'') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  state = QuotedStringTokenizer.State.Token;
               } else if (c == '\\') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  escape = true;
               } else {
                  this._token.append(c);
               }
               break;
            case DoubleQuote:
               this._hasToken = true;
               if (escape) {
                  escape = false;
                  if (c == '\\') {
                     escape = true;
                  } else if (c != '\'' && c != '"') {
                     this._token.append('\\');
                  }

                  this._token.append(c);
               } else if (c == '"') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  state = QuotedStringTokenizer.State.Token;
               } else if (c == '\\') {
                  if (this._returnQuotes) {
                     this._token.append(c);
                  }

                  escape = true;
               } else {
                  this._token.append(c);
               }
            }
         }

         return this._hasToken;
      }
   }

   @Override
   public String nextToken() throws NoSuchElementException {
      if (this.hasMoreTokens() && this._token != null) {
         String t = this._token.toString();
         this._token.setLength(0);
         this._hasToken = false;
         return t;
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public String nextToken(String delim) throws NoSuchElementException {
      this._delim = delim;
      this._i = this._lastStart;
      this._token.setLength(0);
      this._hasToken = false;
      return this.nextToken();
   }

   @Override
   public boolean hasMoreElements() {
      return this.hasMoreTokens();
   }

   @Override
   public Object nextElement() throws NoSuchElementException {
      return this.nextToken();
   }

   @Override
   public int countTokens() {
      return -1;
   }

   public static String quote(String s, String delim) {
      if (s == null) {
         return null;
      } else if (s.length() == 0) {
         return "\"\"";
      } else {
         for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\' || c == '\'' || delim.indexOf(c) >= 0) {
               StringBuffer b = new StringBuffer(s.length() + 8);
               quote(b, s);
               return b.toString();
            }
         }

         return s;
      }
   }

   public static void quote(StringBuffer buf, String s) {
      synchronized(buf) {
         buf.append('"');

         for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '"') {
               buf.append("\\\"");
            } else if (c == '\\') {
               buf.append("\\\\");
            } else {
               buf.append(c);
            }
         }

         buf.append('"');
      }
   }

   public static String unquote(String s) {
      if (s == null) {
         return null;
      } else if (s.length() < 2) {
         return s;
      } else {
         char first = s.charAt(0);
         char last = s.charAt(s.length() - 1);
         if (first == last && (first == '"' || first == '\'')) {
            StringBuffer b = new StringBuffer(s.length() - 2);
            boolean quote = false;

            for(int i = 1; i < s.length() - 1; ++i) {
               char c = s.charAt(i);
               if (c == '\\' && !quote) {
                  quote = true;
               } else {
                  quote = false;
                  b.append(c);
               }
            }

            return b.toString();
         } else {
            return s;
         }
      }
   }

   static enum State {
      Start,
      Token,
      SingleQuote,
      DoubleQuote;
   }
}
