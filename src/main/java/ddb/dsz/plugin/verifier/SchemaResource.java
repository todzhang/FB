package ddb.dsz.plugin.verifier;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

public class SchemaResource implements LSInput {
   File file;

   public SchemaResource(File var1) {
      this.file = var1;
   }

   public Reader getCharacterStream() {
      return null;
   }

   public InputStream getByteStream() {
      return null;
   }

   public String getStringData() {
      return null;
   }

   public String getSystemId() {
      return this.file.toURI().toString();
   }

   public String getPublicId() {
      return null;
   }

   public String getBaseURI() {
      return null;
   }

   public String getEncoding() {
      return null;
   }

   public boolean getCertifiedText() {
      return false;
   }

   public void setCharacterStream(Reader var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setByteStream(InputStream var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setStringData(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setSystemId(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setPublicId(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setBaseURI(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setEncoding(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setCertifiedText(boolean var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
