package ddb.util;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamingXMLReader implements XMLStreamReader {
   private InputStream inputStream;
   XMLStreamReader child = null;
   int level = 0;
   String root = null;
   boolean finished = false;
   boolean sentEndDoc = false;

   public String getCurrentNode() throws XMLStreamException {
      StringBuffer sb = new StringBuffer();
      sb.append(this.getCurrentNodeHelper());
      return sb.toString();
   }

   private StringBuffer getCurrentNodeHelper() throws XMLStreamException {
      StringBuffer results = new StringBuffer();
      if (this.isStartElement()) {
         results.append("<" + this.getName().getLocalPart());

         for(int i = 0; i < this.getAttributeCount(); ++i) {
            String name = this.getAttributeLocalName(i);
            String value = this.getAttributeValue(i);
            results.append(" " + xmlify(name) + "=\"" + xmlify(value) + "\"");
         }

         results.append(">");
         if (!this.isEndElement()) {
            this.next();

            while(!this.isEndElement()) {
               while(!this.hasNext()) {
                  try {
                     TimeUnit.MILLISECONDS.sleep(250L);
                  } catch (InterruptedException var5) {
                     var5.printStackTrace();
                     break;
                  }
               }

               results.append(this.getCurrentNodeHelper());
               this.next();
            }
         }

         results.append("</" + this.getName().getLocalPart() + ">");
      } else if (this.isCharacters()) {
         results.append(xmlify(this.getText()));
      } else if (this.isEndElement()) {
      }

      return results;
   }

   public static String xmlify(String str) {
      str = str.replaceAll("&", "&amp;");
      str = str.replaceAll("<", "&lt;");
      str = str.replaceAll(">", "&gt;");
      str = str.replaceAll("\"", "&quot;");
      return str;
   }

   @Override
   public boolean hasName() {
      return this.child.hasName();
   }

   @Override
   public boolean hasNext() throws XMLStreamException {
      if (this.finished) {
         return !this.sentEndDoc;
      } else {
         return this.child.hasNext();
      }
   }

   @Override
   public int next() throws XMLStreamException {
      if (this.finished) {
         this.sentEndDoc = true;
         return 8;
      } else {
         while(!this.hasNext()) {
            try {
               TimeUnit.MILLISECONDS.sleep(250L);
            } catch (InterruptedException var5) {
               var5.printStackTrace();
               break;
            }
         }

         int res;
         try {
            res = this.child.next();
         } catch (XMLStreamException var3) {
            var3.printStackTrace();
            throw var3;
         } catch (Exception var4) {
            var4.printStackTrace();
            throw new XMLStreamException("Unknown error?!");
         }

         if (res == 1) {
            System.err.println(this.child.getLocalName());
            ++this.level;
         } else if (res == 2) {
            --this.level;
         }

         if (this.root == null && res == 1) {
            this.root = this.child.getName().toString();
         } else if (this.root != null && res == 2 && this.level == 0) {
            String s = this.child.getName().toString();
            if (s.equals(this.root)) {
               this.finished = true;
            }

            return res;
         }

         return res;
      }
   }

   @Override
   public int nextTag() throws XMLStreamException {
      if (this.finished) {
         this.sentEndDoc = true;
         return 8;
      } else {
         int res = this.child.nextTag();
         if (res == 1) {
            ++this.level;
         } else if (res == 2) {
            --this.level;
         }

         if (this.root == null && res == 1) {
            this.root = this.child.getName().toString();
         } else if (this.root != null && res == 2 && this.level == 0) {
            String s = this.child.getName().toString();
            if (s.equals(this.root)) {
               this.finished = true;
            }

            return res;
         }

         if (this.level <= 0) {
            throw new XMLStreamException("Invalid document!");
         } else {
            return res;
         }
      }
   }

   @Override
   public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
      this.child.require(arg0, arg1, arg2);
   }

   public StreamingXMLReader(InputStream is) throws XMLStreamException {
      this.inputStream = is;
      if (is == null) {
         throw new NullPointerException("Parameter 'is' cannot be null!");
      } else {
         this.child = createXMLStreamReader(this.inputStream);
      }
   }

   @Override
   public void close() throws XMLStreamException {
      this.child.close();
   }

   @Override
   public int hashCode() {
      int PRIME = 1;
      int result = 1;
      result = 31 * result + (this.child == null ? 0 : this.child.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         StreamingXMLReader other = (StreamingXMLReader)obj;
         if (this.child == null) {
            if (other.child != null) {
               return false;
            }
         } else if (!this.child.equals(other.child)) {
            return false;
         }

         return true;
      }
   }

   @Override
   public int getAttributeCount() {
      return this.child.getAttributeCount();
   }

   @Override
   public String getAttributeLocalName(int arg0) {
      return this.child.getAttributeLocalName(arg0);
   }

   @Override
   public QName getAttributeName(int arg0) {
      return this.child.getAttributeName(arg0);
   }

   @Override
   public String getAttributeNamespace(int arg0) {
      return this.child.getAttributeNamespace(arg0);
   }

   @Override
   public String getAttributePrefix(int arg0) {
      return this.child.getAttributePrefix(arg0);
   }

   @Override
   public String getAttributeType(int arg0) {
      return this.child.getAttributeType(arg0);
   }

   @Override
   public String getAttributeValue(int arg0) {
      return this.child.getAttributeValue(arg0);
   }

   @Override
   public String getAttributeValue(String arg0, String arg1) {
      return this.child.getAttributeValue(arg0, arg1);
   }

   @Override
   public String getCharacterEncodingScheme() {
      return this.child.getCharacterEncodingScheme();
   }

   @Override
   public String getElementText() throws XMLStreamException {
      return this.child.getElementText();
   }

   @Override
   public String getEncoding() {
      return this.child.getEncoding();
   }

   @Override
   public int getEventType() {
      return this.child.getEventType();
   }

   @Override
   public String getLocalName() {
      return this.child.getLocalName();
   }

   @Override
   public Location getLocation() {
      return this.child.getLocation();
   }

   @Override
   public QName getName() {
      return this.child.getName();
   }

   @Override
   public NamespaceContext getNamespaceContext() {
      return this.child.getNamespaceContext();
   }

   @Override
   public int getNamespaceCount() {
      return this.child.getNamespaceCount();
   }

   @Override
   public String getNamespacePrefix(int arg0) {
      return this.child.getNamespacePrefix(arg0);
   }

   @Override
   public String getNamespaceURI() {
      return this.child.getNamespaceURI();
   }

   @Override
   public String getNamespaceURI(int arg0) {
      return this.child.getNamespaceURI(arg0);
   }

   @Override
   public String getNamespaceURI(String arg0) {
      return this.child.getNamespaceURI(arg0);
   }

   @Override
   public String getPIData() {
      return this.child.getPIData();
   }

   @Override
   public String getPITarget() {
      return this.child.getPITarget();
   }

   @Override
   public String getPrefix() {
      return this.child.getPrefix();
   }

   @Override
   public Object getProperty(String arg0) throws IllegalArgumentException {
      return this.child.getProperty(arg0);
   }

   @Override
   public String getText() {
      return this.child.getText();
   }

   @Override
   public char[] getTextCharacters() {
      return this.child.getTextCharacters();
   }

   @Override
   public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
      return this.child.getTextCharacters(arg0, arg1, arg2, arg3);
   }

   @Override
   public int getTextLength() {
      return this.child.getTextLength();
   }

   @Override
   public int getTextStart() {
      return this.child.getTextStart();
   }

   @Override
   public String getVersion() {
      return this.child.getVersion();
   }

   @Override
   public boolean hasText() {
      return this.child.hasText();
   }

   @Override
   public boolean isAttributeSpecified(int arg0) {
      return this.child.isAttributeSpecified(arg0);
   }

   @Override
   public boolean isCharacters() {
      return this.child.isCharacters();
   }

   @Override
   public boolean isEndElement() {
      return this.child.isEndElement();
   }

   @Override
   public boolean isStandalone() {
      return this.child.isStandalone();
   }

   @Override
   public boolean isStartElement() {
      return this.child.isStartElement();
   }

   @Override
   public boolean isWhiteSpace() {
      return this.child.isWhiteSpace();
   }

   @Override
   public boolean standaloneSet() {
      return this.child.standaloneSet();
   }

   @Override
   public String toString() {
      return this.child.toString();
   }

   private static XMLStreamReader createXMLStreamReader(InputStream is) {
      try {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLStreamReader xsr = factory.createXMLStreamReader(is);
         return xsr;
      } catch (XMLStreamException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
