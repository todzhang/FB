package ddb.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class DszDefaultHandler extends DefaultHandler {
   private Stack<DszDefaultHandler.Element> elements = new Stack();

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      DszDefaultHandler.Element var5 = new DszDefaultHandler.Element(qName);

      for(int var6 = 0; var6 < attributes.getLength(); ++var6) {
         var5.attributes.put(attributes.getQName(var6), attributes.getValue(var6));
      }

      this.elements.push(var5);
   }

   @Override
   public final void characters(char[] ch, int start, int length) throws SAXException {
      if (this.elements.size() > 0) {
         ((DszDefaultHandler.Element)this.elements.peek()).append(new String(ch, start, length));
      }

   }

   @Override
   public final void endElement(String uri, String localName, String qName) throws SAXException {
      super.endElement(uri, localName, qName);
      if (this.elements.size() > 0) {
         this.handleElement((DszDefaultHandler.Element)this.elements.pop());
      }

   }

   protected abstract void handleElement(DszDefaultHandler.Element element) throws SAXException;

   public class Element {
      public final String name;
      private Map<String, String> attributes;
      private StringBuilder text;

      public Element(String var2) {
         this.name = var2;
         this.attributes = new HashMap();
         this.text = new StringBuilder();
      }

      public void append(String var1) {
         this.text.append(var1);
      }

      public String getText() {
         return this.text.toString();
      }

      public String getAttribute(String var1, String var2) {
         String var3 = (String)this.attributes.get(var1);
         return var3 == null ? var2 : var3;
      }

      public String getAttribute(String var1) {
         return this.getAttribute(var1, (String)null);
      }
   }
}
