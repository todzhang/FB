package ddb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class BlockingInputStream extends InputStream {
   static boolean ENDED = false;
   private static BlockingInputStream.ReopenStream NOOP = new BlockingInputStream.ReopenStream() {
      @Override
      public InputStream reopen() {
         return null;
      }
   };
   protected InputStream child;
   long amtRead;
   long markPosition;
   boolean stop;
   protected int dozeTime;
   private BlockingInputStream.ReopenStream reopenMethod;
   private static int DEFAULT_DOZE_TIME = 500;
   long location;

   public static void setEnded() {
      ENDED = true;
   }

   public BlockingInputStream(InputStream child) {
      this(child, DEFAULT_DOZE_TIME);
   }

   public BlockingInputStream(InputStream child, int millis) {
      this(child, millis, NOOP);
   }

   public BlockingInputStream(InputStream child, BlockingInputStream.ReopenStream reopen) {
      this(child, DEFAULT_DOZE_TIME, reopen);
   }

   public BlockingInputStream(InputStream child, int millis, BlockingInputStream.ReopenStream reopen) {
      this.child = null;
      this.amtRead = 0L;
      this.markPosition = 0L;
      this.stop = false;
      this.reopenMethod = null;
      this.location = 0L;
      this.child = child;
      if (this.child == null) {
         throw new NullPointerException("InputStream cannot be null");
      } else {
         if (millis < 0) {
            millis = DEFAULT_DOZE_TIME;
         }

         this.dozeTime = millis;
         this.reopenMethod = reopen;
         if (this.reopenMethod == null) {
            this.reopenMethod = NOOP;
         }

      }
   }

   public BlockingInputStream(File inputFile) throws FileNotFoundException {
      this(inputFile, DEFAULT_DOZE_TIME);
   }

   public BlockingInputStream(final File inputFile, int millis) throws FileNotFoundException {
      this.child = null;
      this.amtRead = 0L;
      this.markPosition = 0L;
      this.stop = false;
      this.reopenMethod = null;
      this.location = 0L;
      this.child = new FileInputStream(inputFile);
      if (millis < 0) {
         millis = DEFAULT_DOZE_TIME;
      }

      this.dozeTime = millis;
      this.reopenMethod = new BlockingInputStream.ReopenStream() {
         long lastSize = 0L;

         @Override
         public InputStream reopen() {
            if (inputFile.length() == this.lastSize) {
               return null;
            } else {
               try {
                  InputStream is = new FileInputStream(inputFile);
                  this.lastSize = inputFile.length();
                  return is;
               } catch (IOException var2) {
                  return null;
               }
            }
         }
      };
   }

   @Override
   public synchronized int read() throws IOException {
      int res;
      for(res = this.child.read(); res < 1 && !this.stop; res = this.child.read()) {
         if (ENDED) {
            return -1;
         }

         sleep(this.dozeTime);
         this.reopen();
      }

      ++this.amtRead;
      return res;
   }

   @Override
   public synchronized int read(byte[] b, int off, int len) throws IOException {
      int res;
      for(res = this.child.read(b, off, len); res < 1 && !this.stop; res = this.child.read(b, off, len)) {
         if (ENDED) {
            return 0;
         }

         sleep(this.dozeTime);
         this.reopen();
      }

      this.amtRead += (long)res;
      return res;
   }

   @Override
   public synchronized int read(byte[] b) throws IOException {
      int res;
      for(res = this.child.read(b); res == -1 && !this.stop; res = this.child.read(b)) {
         if (ENDED) {
            return 0;
         }

         sleep(this.dozeTime);
         this.reopen();
      }

      this.amtRead += (long)res;
      return res;
   }

   @Override
   public synchronized int available() throws IOException {
      return ENDED ? 0 : this.child.available();
   }

   @Override
   public synchronized void close() throws IOException {
      this.child.close();
   }

   @Override
   public synchronized boolean equals(Object obj) {
      return this.child.equals(obj);
   }

   @Override
   public synchronized int hashCode() {
      return this.child.hashCode();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.child.mark(readlimit);
      this.markPosition = this.amtRead;
   }

   @Override
   public synchronized boolean markSupported() {
      return this.child.markSupported();
   }

   @Override
   public synchronized void reset() throws IOException {
      this.child.reset();
      this.amtRead = this.markPosition;
   }

   @Override
   public synchronized long skip(long n) throws IOException {
      long res = this.child.skip(n);
      this.amtRead += res;
      return res;
   }

   @Override
   public synchronized String toString() {
      return this.child.toString();
   }

   private void reopen() {
   }

   private static void sleep(int time) {
      try {
         TimeUnit.MILLISECONDS.sleep((long)time);
      } catch (InterruptedException var2) {
      }

   }

   public interface ReopenStream {
      InputStream reopen();
   }
}
