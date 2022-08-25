package ddb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SplitPrintStream extends PrintStream {
   private static final OutputStream VOID = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
      }
   };
   PrintStream[] others;
   Method ClearError;
   Method SetError;

   public SplitPrintStream(PrintStream... other) throws FileNotFoundException {
      super(VOID);
      this.others = other;

      try {
         this.ClearError = PrintStream.class.getDeclaredMethod("clearError");
         if (this.ClearError != null) {
            this.ClearError.setAccessible(true);
         }

         this.SetError = PrintStream.class.getDeclaredMethod("setError");
         if (this.SetError != null) {
            this.SetError.setAccessible(true);
         }
      } catch (Throwable var3) {
      }

   }

   private boolean isValid() {
      return this.others != null && this.others.length > 0;
   }

   @Override
   public PrintStream append(CharSequence csq) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.append(csq);
         }

         return this;
      }
   }

   @Override
   public PrintStream append(CharSequence csq, int start, int end) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.append(csq, start, end);
         }

         return this;
      }
   }

   @Override
   public PrintStream append(char c) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.append(c);
         }

         return this;
      }
   }

   @Override
   public boolean checkError() {
      if (!this.isValid()) {
         return false;
      } else {
         boolean error = false;
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            if (ps.checkError()) {
               error = true;
            }
         }

         return error;
      }
   }

   @Override
   protected void clearError() {
      if (this.isValid()) {
         if (this.ClearError != null) {
            PrintStream[] arr$ = this.others;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               PrintStream ps = arr$[i$];

               try {
                  this.ClearError.invoke(ps);
               } catch (Throwable var6) {
                  Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, var6);
               }
            }
         }

      }
   }

   @Override
   public void close() {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.close();
         }

      }
   }

   @Override
   public void flush() {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.flush();
         }

      }
   }

   @Override
   public PrintStream format(String format, Object... args) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.format(format, args);
         }

         return this;
      }
   }

   @Override
   public PrintStream format(Locale l, String format, Object... args) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.format(l, format, args);
         }

         return this;
      }
   }

   @Override
   public void print(boolean b) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(b);
         }

      }
   }

   @Override
   public void print(char c) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(c);
         }

      }
   }

   @Override
   public void print(int i) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(i);
         }

      }
   }

   @Override
   public void print(long l) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(l);
         }

      }
   }

   @Override
   public void print(float f) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(f);
         }

      }
   }

   @Override
   public void print(double d) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(d);
         }

      }
   }

   @Override
   public void print(char[] s) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(s);
         }

      }
   }

   @Override
   public void print(String s) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(s);
         }

      }
   }

   @Override
   public void print(Object obj) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.print(obj);
         }

      }
   }

   @Override
   public PrintStream printf(String format, Object... args) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.printf(format, args);
         }

         return this;
      }
   }

   @Override
   public PrintStream printf(Locale l, String format, Object... args) {
      if (!this.isValid()) {
         return this;
      } else {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.printf(l, format, args);
         }

         return this;
      }
   }

   @Override
   public void println() {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println();
         }

      }
   }

   @Override
   public void println(boolean x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(char x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(int x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(long x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(float x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(double x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(char[] x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(String x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   public void println(Object x) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.println(x);
         }

      }
   }

   @Override
   protected void setError() {
      if (this.isValid()) {
         if (this.ClearError != null) {
            PrintStream[] arr$ = this.others;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               PrintStream ps = arr$[i$];

               try {
                  this.SetError.invoke(ps);
               } catch (Throwable var6) {
                  Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, var6);
               }
            }
         }

      }
   }

   @Override
   public void write(int b) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.write(b);
         }

      }
   }

   @Override
   public void write(byte[] buf, int off, int len) {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];
            ps.write(buf, off, len);
         }

      }
   }

   @Override
   public void write(byte[] b) throws IOException {
      if (this.isValid()) {
         PrintStream[] arr$ = this.others;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PrintStream ps = arr$[i$];

            try {
               ps.write(b);
            } catch (IOException var7) {
            }
         }

      }
   }
}
