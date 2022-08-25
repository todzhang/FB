package ddb.util;

public class XMLException extends Exception {
   public XMLException() {
   }

   public XMLException(String message) {
      super(message);
   }

   public XMLException(Throwable cause) {
      super(cause);
   }

   public XMLException(String message, Throwable cause) {
      super(message, cause);
   }
}
