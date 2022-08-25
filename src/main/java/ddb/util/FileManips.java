package ddb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class FileManips {
   static Charset utf8 = Charset.forName("UTF-8");
   static Charset utf16_le = Charset.forName("UTF-16LE");
   static Charset utf16_be = Charset.forName("UTF-16BE");
   private static final byte[] utf8_bytes = new byte[]{-17, -69, -65};
   private static final byte[] utf16_le_bytes = new byte[]{-1, -2};
   private static final byte[] utf16_be_bytes = new byte[]{-2, -1};

   private FileManips() {
   }

   public static InputStreamReader createFileReader(File file) throws IOException {
      if (!file.isDirectory() && file.exists()) {
         byte[] curr = new byte[4];
         Charset cs = utf8;
         PushbackInputStream input = new PushbackInputStream(new FileInputStream(file), curr.length);
         int pushback = input.read(curr);
         if (pushback != curr.length) {
            if (pushback != -1) {
               input.unread(curr, 0, pushback);
            }

            return new InputStreamReader(input, cs);
         } else {
            if (match(curr, utf8_bytes)) {
               cs = utf8;
               pushback -= utf8_bytes.length;
            } else if (match(curr, utf16_be_bytes)) {
               cs = utf16_be;
               pushback -= utf16_be_bytes.length;
            } else if (match(curr, utf16_le_bytes)) {
               cs = utf16_le;
               pushback -= utf16_le_bytes.length;
            } else {
               cs = utf8;
            }

            input.unread(curr, curr.length - pushback, pushback);
            return cs == null ? new InputStreamReader(input, utf8) : new InputStreamReader(input, cs);
         }
      } else {
         return null;
      }
   }

   public static OutputStreamWriter createFileWriter(File file) throws FileNotFoundException {
      return createFileWriter(file, true);
   }

   public static OutputStreamWriter createFileWriter(File file, boolean append) throws FileNotFoundException {
      return new OutputStreamWriter(new FileOutputStream(file, append), utf8);
   }

   private static boolean match(byte[] FileBytes, byte[] orderMarker) {
      for(int i = 0; i < Math.min(FileBytes.length, orderMarker.length); ++i) {
         if (FileBytes[i] != orderMarker[i]) {
            return false;
         }
      }

      return true;
   }

   public static InputStream createFileStream(File file) throws IOException {
      if (!file.isDirectory() && file.exists()) {
         FileInputStream fis = new FileInputStream(file);
         Charset cs = getEncoding((InputStream)fis);
         fis.close();
         if (cs == null) {
            return new FileInputStream(file);
         } else {
            fis = new FileInputStream(file);
            byte[] ch = new byte[0];
            if (cs == utf8) {
               ch = new byte[3];
            } else if (cs == utf16_be || cs == utf16_le) {
               ch = new byte[4];
            }

            fis.read(ch);
            return fis;
         }
      } else {
         return null;
      }
   }

   public static Charset getEncoding(InputStream is) throws IOException {
      if (is == null) {
         return null;
      } else {
         byte[] b = new byte[1];
         if (is.read(b) == 0) {
            return null;
         } else {
            if (b[0] == -17) {
               if (is.read(b) != 0 && b[0] == -69 && is.read(b) != 0 && b[0] == -65) {
                  return utf8;
               }
            } else if (b[0] == -1) {
               if (is.read(b) != 0 && b[0] == -2) {
                  return utf16_le;
               }
            } else if (b[0] == -2 && is.read(b) != 0 && b[0] == -1) {
               return utf16_be;
            }

            return null;
         }
      }
   }

   public static Charset getEncoding(File file) throws IOException {
      if (!file.isDirectory() && file.exists()) {
         FileInputStream fis = new FileInputStream(file);

         Charset var2;
         try {
            var2 = getEncoding((InputStream)(new FileInputStream(file)));
         } finally {
            fis.close();
         }

         return var2;
      } else {
         return null;
      }
   }

   public static boolean CopyFile(File source, File dest) throws IOException {
      if (dest.exists()) {
         dest.delete();
      }

      byte[] bytes = new byte[4092];
      FileInputStream fis = new FileInputStream(source);
      FileOutputStream fos = new FileOutputStream(dest);
      boolean var5 = false;

      int j;
      while(-1 != (j = fis.read(bytes))) {
         fos.write(bytes, 0, j);
      }

      fis.close();
      fos.close();
      return true;
   }

   public static boolean MoveFile(File source, File dest) throws IOException {
      if (dest.exists()) {
         dest.delete();
      }

      byte[] bytes = new byte[4092];
      FileInputStream fis = new FileInputStream(source);
      FileOutputStream fos = new FileOutputStream(dest);
      boolean var5 = false;

      int j;
      while(-1 != (j = fis.read(bytes))) {
         fos.write(bytes, 0, j);
      }

      fis.close();
      fos.close();
      return source.delete();
   }

   public static Document loadDocument(String input) throws ParserConfigurationException, SAXException, IOException {
      StringReader sr = new StringReader(input);
      InputSource is = new InputSource(sr);
      DocumentBuilder db = XmlCache.getBuilder();

      Document var4;
      try {
         var4 = db.parse(is);
      } finally {
         XmlCache.releaseBuilder(db);
      }

      return var4;
   }

   public static Document loadDocument(File inputFile) throws IncompleteFileException, IOException {
      StringBuffer interum = new StringBuffer();

      try {
         InputStreamReader isr = createFileReader(inputFile);
         char[] ch = new char[1024];

         int i;
         while(-1 != (i = isr.read(ch))) {
            interum.append(ch, 0, i);
         }

         isr.close();
         return loadDocument(interum.toString());
      } catch (NullPointerException var8) {
      } catch (SAXException var9) {
         try {
            interum.append("</Data>");
            Document doc = loadDocument(interum.toString());
            throw new IncompleteFileException(doc);
         } catch (IOException var5) {
            var5.printStackTrace();
         } catch (ParserConfigurationException var6) {
            var6.printStackTrace();
         } catch (SAXException var7) {
         }
      } catch (FileNotFoundException var10) {
         throw var10;
      } catch (ParserConfigurationException var11) {
      }

      return null;
   }
}
