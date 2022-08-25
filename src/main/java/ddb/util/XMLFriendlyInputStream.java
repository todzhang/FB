package ddb.util;

import java.io.IOException;
import java.io.InputStream;

public class XMLFriendlyInputStream extends InputStream {
   InputStream is = null;

   public XMLFriendlyInputStream(InputStream is) {
      this.is = is;
   }

   @Override
   public int read() throws IOException {
      return this.is.read();
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      int shortLen = len - 2048;
      if (shortLen < 0) {
         shortLen = len / 2;
      }

      int res = this.is.read(b, off, shortLen);
      if (res < shortLen) {
         return res;
      } else {
         for(int i = off + res; i < off + len && i != -1; ++i) {
            int j = this.read();
            if (j == -1) {
               break;
            }

            ++res;
            b[i] = Integer.valueOf(j).byteValue();
            if (j == 62) {
               break;
            }
         }

         return res;
      }
   }

   @Override
   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   @Override
   public int available() throws IOException {
      return this.is.available();
   }

   @Override
   public void close() throws IOException {
      this.is.close();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.is.mark(readlimit);
   }

   @Override
   public boolean markSupported() {
      return this.is.markSupported();
   }

   @Override
   public synchronized void reset() throws IOException {
      this.is.reset();
   }

   @Override
   public long skip(long n) throws IOException {
      return this.is.skip(n);
   }
}
