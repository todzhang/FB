package ddb.util;

import org.xml.sax.SAXException;

public class FinishedProcessingException extends RuntimeException {
   public static final SAXException FINISHED = new SAXException(new FinishedProcessingException());

   private FinishedProcessingException() {
   }
}
