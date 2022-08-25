package ds.util.datatransforms;

import ddb.dsz.core.data.ErrorEvent;
import ddb.dsz.core.data.ErrorEvent.ErrorEventType;
import ddb.dsz.core.data.ErrorEvent.ErrorLevel;
import ddb.dsz.core.data.TransformEvent.TransformEventType;

class DataTransformsErrorEvent implements ErrorEvent {
   ErrorEventType type;
   ErrorLevel level;
   String msg;
   int line;

   public DataTransformsErrorEvent(ErrorEventType var1, ErrorLevel var2, int var3, String var4) {
      this.type = var1;
      this.level = var2;
      this.msg = var4;
      this.line = var3;
   }

   public TransformEventType getType() {
      return TransformEventType.ERROR;
   }

   public int getLine() {
      return this.line;
   }

   public ErrorLevel getLevel() {
      return this.level;
   }

   public String getMessage() {
      return this.msg;
   }

   public ErrorEventType getErrorType() {
      return this.type;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      switch(this.type) {
      case STORAGE:
         var1.append("Storage - ");
         break;
      case TRANSFORM:
         var1.append("Transform - ");
         break;
      case DOCUMENTBUILDER:
         var1.append("DocumentBuilder - ");
         break;
      case NODOCUMENTS:
         var1.append("No Documents - ");
      }

      switch(this.level) {
      case ERROR:
         var1.append("Error");
         break;
      case FATAL:
         var1.append("Fatal Error");
         break;
      case WARNING:
         var1.append("Warning");
      }

      var1.append(String.format("(%d): %s", this.line, this.msg));
      return var1.toString();
   }
}
