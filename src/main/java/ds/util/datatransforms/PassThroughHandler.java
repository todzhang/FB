package ds.util.datatransforms;

import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PassThroughHandler extends DefaultHandler {
   protected boolean skip = false;
   protected TransformerHandler hd;

   public PassThroughHandler(TransformerHandler var1) {
      this.hd = var1;
      if (var1 == null) {
         this.skip = true;
      }

   }

   public PassThroughHandler(TransformerHandler var1, boolean var2) {
      this.hd = var1;
      this.skip = var2;
   }

   public void characters(char[] var1, int var2, int var3) throws SAXException {
      super.characters(var1, var2, var3);
      if (!this.skip) {
         this.hd.characters(var1, var2, var3);
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      super.endElement(var1, var2, var3);
      if (!this.skip) {
         this.hd.endElement(var1, var2, var3);
      }

   }

   public void endPrefixMapping(String var1) throws SAXException {
      super.endPrefixMapping(var1);
      if (!this.skip) {
         this.hd.endPrefixMapping(var1);
      }

   }

   public void ignorableWhitespace(char[] var1, int var2, int var3) throws SAXException {
      super.ignorableWhitespace(var1, var2, var3);
      if (!this.skip) {
         this.hd.ignorableWhitespace(var1, var2, var3);
      }

   }

   public void notationDecl(String var1, String var2, String var3) throws SAXException {
      super.notationDecl(var1, var2, var3);
      if (!this.skip) {
         this.hd.notationDecl(var1, var2, var3);
      }

   }

   public void processingInstruction(String var1, String var2) throws SAXException {
      super.processingInstruction(var1, var2);
      if (!this.skip) {
         this.hd.processingInstruction(var1, var2);
      }

   }

   public void setDocumentLocator(Locator var1) {
      super.setDocumentLocator(var1);
      if (!this.skip) {
         this.hd.setDocumentLocator(var1);
      }

   }

   public void skippedEntity(String var1) throws SAXException {
      super.skippedEntity(var1);
      if (!this.skip) {
         this.hd.skippedEntity(var1);
      }

   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      super.startElement(var1, var2, var3, var4);
      if (!this.skip) {
         this.hd.startElement(var1, var2, var3, var4);
      }

   }

   public void startPrefixMapping(String var1, String var2) throws SAXException {
      super.startPrefixMapping(var1, var2);
      if (!this.skip) {
         this.hd.startPrefixMapping(var1, var2);
      }

   }

   public void unparsedEntityDecl(String var1, String var2, String var3, String var4) throws SAXException {
      super.unparsedEntityDecl(var1, var2, var3, var4);
      if (!this.skip) {
         this.hd.unparsedEntityDecl(var1, var2, var3, var4);
      }

   }
}
