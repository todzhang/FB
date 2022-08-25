package ddb.dsz.core.data;

public interface ErrorEvent extends TransformEvent {
   ErrorEvent.ErrorLevel getLevel();

   int getLine();

   String getMessage();

   ErrorEvent.ErrorEventType getErrorType();

   public enum ErrorLevel {
      WARNING,
      ERROR,
      FATAL;
   }

   public enum ErrorEventType {
      TRANSFORM,
      STORAGE,
      DOCUMENTBUILDER,
      NODOCUMENTS,
      EXCEPTION;
   }
}
