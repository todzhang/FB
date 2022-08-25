package ddb.dsz.core.data;

public interface TransformEvent {
   TransformEvent.TransformEventType getType();

   public enum TransformEventType {
      DATA,
      ERROR;
   }
}
