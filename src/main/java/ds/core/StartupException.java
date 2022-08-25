package ds.core;

public class StartupException extends Exception {
   public StartupException() {
   }

   public StartupException(String message) {
      super(message);
   }

   public StartupException(String message, Throwable cause) {
      super(message, cause);
   }

   public StartupException(Throwable cause) {
      super(cause);
   }
}
