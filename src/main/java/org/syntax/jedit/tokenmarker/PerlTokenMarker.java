package org.syntax.jedit.tokenmarker;

import javax.swing.text.Segment;
import org.syntax.jedit.KeywordMap;
import org.syntax.jedit.SyntaxUtilities;

public class PerlTokenMarker extends TokenMarker {
   public static final byte S_ONE = 100;
   public static final byte S_TWO = 101;
   public static final byte S_END = 102;
   private KeywordMap keywords;
   private byte token;
   private int lastOffset;
   private int lastKeyword;
   private char matchChar;
   private boolean matchCharBracket;
   private boolean matchSpacesAllowed;
   private static KeywordMap perlKeywords;

   public PerlTokenMarker() {
      this(getKeywords());
   }

   public PerlTokenMarker(KeywordMap var1) {
      this.keywords = var1;
   }

   public byte markTokensImpl(byte var1, Segment var2, int var3) {
      char[] var4 = var2.array;
      int var5 = var2.offset;
      this.token = var1;
      this.lastOffset = var5;
      this.lastKeyword = var5;
      this.matchChar = 0;
      this.matchCharBracket = false;
      this.matchSpacesAllowed = false;
      int var6 = var2.count + var5;
      if (this.token == 3 && var3 != 0 && this.lineInfo[var3 - 1].obj != null) {
         String var13 = (String)this.lineInfo[var3 - 1].obj;
         if (var13 != null && var13.length() == var2.count && SyntaxUtilities.regionMatches(false, var2, var5, var13)) {
            this.addToken(var2.count, this.token);
            return 0;
         } else {
            this.addToken(var2.count, this.token);
            this.lineInfo[var3].obj = var13;
            return this.token;
         }
      } else {
         boolean var7 = false;
         int var8 = var5;

         while(true) {
            label253: {
               if (var8 < var6) {
                  int var9 = var8 + 1;
                  char var10 = var4[var8];
                  if (var10 == '\\') {
                     var7 = !var7;
                     break label253;
                  }

                  label241:
                  switch(this.token) {
                  case 0:
                     int var11;
                     switch(var10) {
                     case '"':
                        if (!this.doKeyword(var2, var8, var10)) {
                           if (var7) {
                              var7 = false;
                           } else {
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.token = 3;
                              this.lineInfo[var3].obj = null;
                              this.lastOffset = this.lastKeyword = var8;
                           }
                        }
                        break label253;
                     case '#':
                        if (this.doKeyword(var2, var8, var10)) {
                           break label253;
                        }

                        if (var7) {
                           var7 = false;
                           break label253;
                        }

                        this.addToken(var8 - this.lastOffset, this.token);
                        this.addToken(var6 - var8, (byte)1);
                        this.lastOffset = this.lastKeyword = var6;
                        break label241;
                     case '$':
                     case '%':
                     case '&':
                     case '@':
                        var7 = false;
                        if (!this.doKeyword(var2, var8, var10) && var6 - var8 > 1) {
                           if (var10 != '&' || var4[var9] != '&' && !Character.isWhitespace(var4[var9])) {
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.lastOffset = this.lastKeyword = var8;
                              this.token = 7;
                           } else {
                              ++var8;
                           }
                        }
                        break label253;
                     case '\'':
                        if (var7) {
                           var7 = false;
                        } else {
                           var11 = this.lastKeyword;
                           if (!this.doKeyword(var2, var8, var10) && var8 == var11) {
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.token = 4;
                              this.lastOffset = this.lastKeyword = var8;
                           }
                        }
                        break label253;
                     case '-':
                        var7 = false;
                        if (!this.doKeyword(var2, var8, var10) && var8 == this.lastKeyword && var6 - var8 > 1) {
                           switch(var4[var9]) {
                           case 'A':
                           case 'B':
                           case 'C':
                           case 'M':
                           case 'O':
                           case 'R':
                           case 'S':
                           case 'T':
                           case 'W':
                           case 'X':
                           case 'b':
                           case 'c':
                           case 'd':
                           case 'e':
                           case 'f':
                           case 'g':
                           case 'k':
                           case 'l':
                           case 'o':
                           case 'p':
                           case 'r':
                           case 's':
                           case 't':
                           case 'u':
                           case 'w':
                           case 'x':
                           case 'z':
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.addToken(2, (byte)8);
                              this.lastOffset = this.lastKeyword = var8 + 2;
                              ++var8;
                           case 'D':
                           case 'E':
                           case 'F':
                           case 'G':
                           case 'H':
                           case 'I':
                           case 'J':
                           case 'K':
                           case 'L':
                           case 'N':
                           case 'P':
                           case 'Q':
                           case 'U':
                           case 'V':
                           case 'Y':
                           case 'Z':
                           case '[':
                           case '\\':
                           case ']':
                           case '^':
                           case '_':
                           case '`':
                           case 'a':
                           case 'h':
                           case 'i':
                           case 'j':
                           case 'm':
                           case 'n':
                           case 'q':
                           case 'v':
                           case 'y':
                           }
                        }
                        break label253;
                     case '/':
                     case '?':
                        if (!this.doKeyword(var2, var8, var10) && var6 - var8 > 1) {
                           var7 = false;
                           char var12 = var4[var9];
                           if (!Character.isWhitespace(var12)) {
                              this.matchChar = var10;
                              this.matchSpacesAllowed = false;
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.token = 100;
                              this.lastOffset = this.lastKeyword = var8;
                           }
                        }
                        break label253;
                     case ':':
                        var7 = false;
                        if (!this.doKeyword(var2, var8, var10) && this.lastKeyword == 0) {
                           this.addToken(var9 - this.lastOffset, (byte)5);
                           this.lastOffset = this.lastKeyword = var9;
                        }
                        break label253;
                     case '<':
                        if (!this.doKeyword(var2, var8, var10)) {
                           if (var7) {
                              var7 = false;
                           } else if (var6 - var8 > 2 && var4[var9] == '<' && !Character.isWhitespace(var4[var8 + 2])) {
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.lastOffset = this.lastKeyword = var8;
                              this.token = 3;
                              var11 = var6 - (var8 + 2);
                              if (var4[var6 - 1] == ';') {
                                 --var11;
                              }

                              this.lineInfo[var3].obj = this.createReadinString(var4, var8 + 2, var11);
                           }
                        }
                        break label253;
                     case '=':
                        var7 = false;
                        if (var8 != var5) {
                           this.doKeyword(var2, var8, var10);
                           break label253;
                        }

                        this.token = 2;
                        this.addToken(var6 - var8, this.token);
                        this.lastOffset = this.lastKeyword = var6;
                        break label241;
                     case '`':
                        if (!this.doKeyword(var2, var8, var10)) {
                           if (var7) {
                              var7 = false;
                           } else {
                              this.addToken(var8 - this.lastOffset, this.token);
                              this.token = 9;
                              this.lastOffset = this.lastKeyword = var8;
                           }
                        }
                        break label253;
                     default:
                        var7 = false;
                        if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                           this.doKeyword(var2, var8, var10);
                        }
                        break label253;
                     }
                  case 2:
                     var7 = false;
                     if (var8 != var5) {
                        break label253;
                     }

                     this.addToken(var2.count, this.token);
                     if (var6 - var8 > 3 && SyntaxUtilities.regionMatches(false, var2, var5, "=cut")) {
                        this.token = 0;
                     }

                     this.lastOffset = this.lastKeyword = var6;
                     break;
                  case 3:
                     if (var7) {
                        var7 = false;
                     } else if (var10 == '"') {
                        this.addToken(var9 - this.lastOffset, this.token);
                        this.token = 0;
                        this.lastOffset = this.lastKeyword = var9;
                     }
                     break label253;
                  case 4:
                     if (var7) {
                        var7 = false;
                     } else if (var10 == '\'') {
                        this.addToken(var9 - this.lastOffset, (byte)3);
                        this.token = 0;
                        this.lastOffset = this.lastKeyword = var9;
                     }
                     break label253;
                  case 7:
                     var7 = false;
                     if (!Character.isLetterOrDigit(var10) && var10 != '_' && var10 != '#' && var10 != '\'' && var10 != ':' && var10 != '&') {
                        if (var8 != var5 && var4[var8 - 1] == '$') {
                           this.addToken(var9 - this.lastOffset, this.token);
                           this.lastOffset = this.lastKeyword = var9;
                        } else {
                           this.addToken(var8 - this.lastOffset, this.token);
                           this.lastOffset = this.lastKeyword = var8;
                           --var8;
                           this.token = 0;
                        }
                     }
                     break label253;
                  case 9:
                     if (var7) {
                        var7 = false;
                     } else if (var10 == '`') {
                        this.addToken(var9 - this.lastOffset, this.token);
                        this.token = 0;
                        this.lastOffset = this.lastKeyword = var9;
                     }
                     break label253;
                  case 100:
                  case 101:
                     if (var7) {
                        var7 = false;
                     } else if (this.matchChar == 0) {
                        if (!Character.isWhitespace(this.matchChar) || this.matchSpacesAllowed) {
                           this.matchChar = var10;
                        }
                     } else {
                        switch(this.matchChar) {
                        case '(':
                           this.matchChar = ')';
                           this.matchCharBracket = true;
                           break;
                        case '<':
                           this.matchChar = '>';
                           this.matchCharBracket = true;
                           break;
                        case '[':
                           this.matchChar = ']';
                           this.matchCharBracket = true;
                           break;
                        case '{':
                           this.matchChar = '}';
                           this.matchCharBracket = true;
                           break;
                        default:
                           this.matchCharBracket = false;
                        }

                        if (var10 == this.matchChar) {
                           if (this.token == 101) {
                              this.token = 100;
                              if (this.matchCharBracket) {
                                 this.matchChar = 0;
                              }
                           } else {
                              this.token = 102;
                              this.addToken(var9 - this.lastOffset, (byte)4);
                              this.lastOffset = this.lastKeyword = var9;
                           }
                        }
                     }
                     break label253;
                  case 102:
                     var7 = false;
                     if (!Character.isLetterOrDigit(var10) && var10 != '_') {
                        this.doKeyword(var2, var8, var10);
                     }
                     break label253;
                  default:
                     throw new InternalError("Invalid state: " + this.token);
                  }
               }

               if (this.token == 0) {
                  this.doKeyword(var2, var6, '\u0000');
               }

               switch(this.token) {
               case 4:
                  this.addToken(var6 - this.lastOffset, (byte)3);
                  break;
               case 7:
                  this.addToken(var6 - this.lastOffset, this.token);
                  this.token = 0;
                  break;
               case 100:
               case 101:
                  this.addToken(var6 - this.lastOffset, (byte)10);
                  this.token = 0;
                  break;
               case 102:
                  this.addToken(var6 - this.lastOffset, (byte)4);
                  this.token = 0;
                  break;
               default:
                  this.addToken(var6 - this.lastOffset, this.token);
               }

               return this.token;
            }

            ++var8;
         }
      }
   }

   private boolean doKeyword(Segment var1, int var2, char var3) {
      int var4 = var2 + 1;
      if (this.token == 102) {
         this.addToken(var2 - this.lastOffset, (byte)4);
         this.token = 0;
         this.lastOffset = var2;
         this.lastKeyword = var4;
         return false;
      } else {
         int var5 = var2 - this.lastKeyword;
         byte var6 = this.keywords.lookup(var1, this.lastKeyword, var5);
         if (var6 != 100 && var6 != 101) {
            if (var6 != 0) {
               if (this.lastKeyword != this.lastOffset) {
                  this.addToken(this.lastKeyword - this.lastOffset, (byte)0);
               }

               this.addToken(var5, var6);
               this.lastOffset = var2;
            }

            this.lastKeyword = var4;
            return false;
         } else {
            if (this.lastKeyword != this.lastOffset) {
               this.addToken(this.lastKeyword - this.lastOffset, (byte)0);
            }

            this.addToken(var5, (byte)4);
            this.lastOffset = var2;
            this.lastKeyword = var4;
            if (Character.isWhitespace(var3)) {
               this.matchChar = 0;
            } else {
               this.matchChar = var3;
            }

            this.matchSpacesAllowed = true;
            this.token = var6;
            return true;
         }
      }
   }

   private String createReadinString(char[] var1, int var2, int var3) {
      int var4 = var2;

      int var5;
      for(var5 = var2 + var3 - 1; var4 <= var5 && !Character.isLetterOrDigit(var1[var4]); ++var4) {
      }

      while(var4 <= var5 && !Character.isLetterOrDigit(var1[var5])) {
         --var5;
      }

      return new String(var1, var4, var5 - var4 + 1);
   }

   private static KeywordMap getKeywords() {
      if (perlKeywords == null) {
         perlKeywords = new KeywordMap(false);
         perlKeywords.add("my", (byte)6);
         perlKeywords.add("local", (byte)6);
         perlKeywords.add("new", (byte)6);
         perlKeywords.add("if", (byte)6);
         perlKeywords.add("until", (byte)6);
         perlKeywords.add("while", (byte)6);
         perlKeywords.add("elsif", (byte)6);
         perlKeywords.add("else", (byte)6);
         perlKeywords.add("eval", (byte)6);
         perlKeywords.add("unless", (byte)6);
         perlKeywords.add("foreach", (byte)6);
         perlKeywords.add("continue", (byte)6);
         perlKeywords.add("exit", (byte)6);
         perlKeywords.add("die", (byte)6);
         perlKeywords.add("last", (byte)6);
         perlKeywords.add("goto", (byte)6);
         perlKeywords.add("next", (byte)6);
         perlKeywords.add("redo", (byte)6);
         perlKeywords.add("goto", (byte)6);
         perlKeywords.add("return", (byte)6);
         perlKeywords.add("do", (byte)6);
         perlKeywords.add("sub", (byte)6);
         perlKeywords.add("use", (byte)6);
         perlKeywords.add("require", (byte)6);
         perlKeywords.add("package", (byte)6);
         perlKeywords.add("BEGIN", (byte)6);
         perlKeywords.add("END", (byte)6);
         perlKeywords.add("eq", (byte)9);
         perlKeywords.add("ne", (byte)9);
         perlKeywords.add("not", (byte)9);
         perlKeywords.add("and", (byte)9);
         perlKeywords.add("or", (byte)9);
         perlKeywords.add("abs", (byte)8);
         perlKeywords.add("accept", (byte)8);
         perlKeywords.add("alarm", (byte)8);
         perlKeywords.add("atan2", (byte)8);
         perlKeywords.add("bind", (byte)8);
         perlKeywords.add("binmode", (byte)8);
         perlKeywords.add("bless", (byte)8);
         perlKeywords.add("caller", (byte)8);
         perlKeywords.add("chdir", (byte)8);
         perlKeywords.add("chmod", (byte)8);
         perlKeywords.add("chomp", (byte)8);
         perlKeywords.add("chr", (byte)8);
         perlKeywords.add("chroot", (byte)8);
         perlKeywords.add("chown", (byte)8);
         perlKeywords.add("closedir", (byte)8);
         perlKeywords.add("close", (byte)8);
         perlKeywords.add("connect", (byte)8);
         perlKeywords.add("cos", (byte)8);
         perlKeywords.add("crypt", (byte)8);
         perlKeywords.add("dbmclose", (byte)8);
         perlKeywords.add("dbmopen", (byte)8);
         perlKeywords.add("defined", (byte)8);
         perlKeywords.add("delete", (byte)8);
         perlKeywords.add("die", (byte)8);
         perlKeywords.add("dump", (byte)8);
         perlKeywords.add("each", (byte)8);
         perlKeywords.add("endgrent", (byte)8);
         perlKeywords.add("endhostent", (byte)8);
         perlKeywords.add("endnetent", (byte)8);
         perlKeywords.add("endprotoent", (byte)8);
         perlKeywords.add("endpwent", (byte)8);
         perlKeywords.add("endservent", (byte)8);
         perlKeywords.add("eof", (byte)8);
         perlKeywords.add("exec", (byte)8);
         perlKeywords.add("exists", (byte)8);
         perlKeywords.add("exp", (byte)8);
         perlKeywords.add("fctnl", (byte)8);
         perlKeywords.add("fileno", (byte)8);
         perlKeywords.add("flock", (byte)8);
         perlKeywords.add("fork", (byte)8);
         perlKeywords.add("format", (byte)8);
         perlKeywords.add("formline", (byte)8);
         perlKeywords.add("getc", (byte)8);
         perlKeywords.add("getgrent", (byte)8);
         perlKeywords.add("getgrgid", (byte)8);
         perlKeywords.add("getgrnam", (byte)8);
         perlKeywords.add("gethostbyaddr", (byte)8);
         perlKeywords.add("gethostbyname", (byte)8);
         perlKeywords.add("gethostent", (byte)8);
         perlKeywords.add("getlogin", (byte)8);
         perlKeywords.add("getnetbyaddr", (byte)8);
         perlKeywords.add("getnetbyname", (byte)8);
         perlKeywords.add("getnetent", (byte)8);
         perlKeywords.add("getpeername", (byte)8);
         perlKeywords.add("getpgrp", (byte)8);
         perlKeywords.add("getppid", (byte)8);
         perlKeywords.add("getpriority", (byte)8);
         perlKeywords.add("getprotobyname", (byte)8);
         perlKeywords.add("getprotobynumber", (byte)8);
         perlKeywords.add("getprotoent", (byte)8);
         perlKeywords.add("getpwent", (byte)8);
         perlKeywords.add("getpwnam", (byte)8);
         perlKeywords.add("getpwuid", (byte)8);
         perlKeywords.add("getservbyname", (byte)8);
         perlKeywords.add("getservbyport", (byte)8);
         perlKeywords.add("getservent", (byte)8);
         perlKeywords.add("getsockname", (byte)8);
         perlKeywords.add("getsockopt", (byte)8);
         perlKeywords.add("glob", (byte)8);
         perlKeywords.add("gmtime", (byte)8);
         perlKeywords.add("grep", (byte)8);
         perlKeywords.add("hex", (byte)8);
         perlKeywords.add("import", (byte)8);
         perlKeywords.add("index", (byte)8);
         perlKeywords.add("int", (byte)8);
         perlKeywords.add("ioctl", (byte)8);
         perlKeywords.add("join", (byte)8);
         perlKeywords.add("keys", (byte)8);
         perlKeywords.add("kill", (byte)8);
         perlKeywords.add("lcfirst", (byte)8);
         perlKeywords.add("lc", (byte)8);
         perlKeywords.add("length", (byte)8);
         perlKeywords.add("link", (byte)8);
         perlKeywords.add("listen", (byte)8);
         perlKeywords.add("log", (byte)8);
         perlKeywords.add("localtime", (byte)8);
         perlKeywords.add("lstat", (byte)8);
         perlKeywords.add("map", (byte)8);
         perlKeywords.add("mkdir", (byte)8);
         perlKeywords.add("msgctl", (byte)8);
         perlKeywords.add("msgget", (byte)8);
         perlKeywords.add("msgrcv", (byte)8);
         perlKeywords.add("no", (byte)8);
         perlKeywords.add("oct", (byte)8);
         perlKeywords.add("opendir", (byte)8);
         perlKeywords.add("open", (byte)8);
         perlKeywords.add("ord", (byte)8);
         perlKeywords.add("pack", (byte)8);
         perlKeywords.add("pipe", (byte)8);
         perlKeywords.add("pop", (byte)8);
         perlKeywords.add("pos", (byte)8);
         perlKeywords.add("printf", (byte)8);
         perlKeywords.add("print", (byte)8);
         perlKeywords.add("push", (byte)8);
         perlKeywords.add("quotemeta", (byte)8);
         perlKeywords.add("rand", (byte)8);
         perlKeywords.add("readdir", (byte)8);
         perlKeywords.add("read", (byte)8);
         perlKeywords.add("readlink", (byte)8);
         perlKeywords.add("recv", (byte)8);
         perlKeywords.add("ref", (byte)8);
         perlKeywords.add("rename", (byte)8);
         perlKeywords.add("reset", (byte)8);
         perlKeywords.add("reverse", (byte)8);
         perlKeywords.add("rewinddir", (byte)8);
         perlKeywords.add("rindex", (byte)8);
         perlKeywords.add("rmdir", (byte)8);
         perlKeywords.add("scalar", (byte)8);
         perlKeywords.add("seekdir", (byte)8);
         perlKeywords.add("seek", (byte)8);
         perlKeywords.add("select", (byte)8);
         perlKeywords.add("semctl", (byte)8);
         perlKeywords.add("semget", (byte)8);
         perlKeywords.add("semop", (byte)8);
         perlKeywords.add("send", (byte)8);
         perlKeywords.add("setgrent", (byte)8);
         perlKeywords.add("sethostent", (byte)8);
         perlKeywords.add("setnetent", (byte)8);
         perlKeywords.add("setpgrp", (byte)8);
         perlKeywords.add("setpriority", (byte)8);
         perlKeywords.add("setprotoent", (byte)8);
         perlKeywords.add("setpwent", (byte)8);
         perlKeywords.add("setsockopt", (byte)8);
         perlKeywords.add("shift", (byte)8);
         perlKeywords.add("shmctl", (byte)8);
         perlKeywords.add("shmget", (byte)8);
         perlKeywords.add("shmread", (byte)8);
         perlKeywords.add("shmwrite", (byte)8);
         perlKeywords.add("shutdown", (byte)8);
         perlKeywords.add("sin", (byte)8);
         perlKeywords.add("sleep", (byte)8);
         perlKeywords.add("socket", (byte)8);
         perlKeywords.add("socketpair", (byte)8);
         perlKeywords.add("sort", (byte)8);
         perlKeywords.add("splice", (byte)8);
         perlKeywords.add("split", (byte)8);
         perlKeywords.add("sprintf", (byte)8);
         perlKeywords.add("sqrt", (byte)8);
         perlKeywords.add("srand", (byte)8);
         perlKeywords.add("stat", (byte)8);
         perlKeywords.add("study", (byte)8);
         perlKeywords.add("substr", (byte)8);
         perlKeywords.add("symlink", (byte)8);
         perlKeywords.add("syscall", (byte)8);
         perlKeywords.add("sysopen", (byte)8);
         perlKeywords.add("sysread", (byte)8);
         perlKeywords.add("syswrite", (byte)8);
         perlKeywords.add("telldir", (byte)8);
         perlKeywords.add("tell", (byte)8);
         perlKeywords.add("tie", (byte)8);
         perlKeywords.add("tied", (byte)8);
         perlKeywords.add("time", (byte)8);
         perlKeywords.add("times", (byte)8);
         perlKeywords.add("truncate", (byte)8);
         perlKeywords.add("uc", (byte)8);
         perlKeywords.add("ucfirst", (byte)8);
         perlKeywords.add("umask", (byte)8);
         perlKeywords.add("undef", (byte)8);
         perlKeywords.add("unlink", (byte)8);
         perlKeywords.add("unpack", (byte)8);
         perlKeywords.add("unshift", (byte)8);
         perlKeywords.add("untie", (byte)8);
         perlKeywords.add("utime", (byte)8);
         perlKeywords.add("values", (byte)8);
         perlKeywords.add("vec", (byte)8);
         perlKeywords.add("wait", (byte)8);
         perlKeywords.add("waitpid", (byte)8);
         perlKeywords.add("wantarray", (byte)8);
         perlKeywords.add("warn", (byte)8);
         perlKeywords.add("write", (byte)8);
         perlKeywords.add("m", (byte)100);
         perlKeywords.add("q", (byte)100);
         perlKeywords.add("qq", (byte)100);
         perlKeywords.add("qw", (byte)100);
         perlKeywords.add("qx", (byte)100);
         perlKeywords.add("s", (byte)101);
         perlKeywords.add("tr", (byte)101);
         perlKeywords.add("y", (byte)101);
      }

      return perlKeywords;
   }
}
