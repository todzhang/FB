package ddb.util;

import org.w3c.dom.Document;

public class IncompleteFileException extends Exception {
   Document document = null;

   public synchronized Document getDocument() {
      return this.document;
   }

   public synchronized void setDocument(Document document) {
      this.document = document;
   }

   public IncompleteFileException(Document doc) {
      this.document = doc;
   }
}
