package ds.util.datatransforms;

import ddb.dsz.core.task.TaskDataAccess;
import ddb.util.BlockingInputStream.ReopenStream;
import java.io.InputStream;

class DataTransformerImpl2$3 implements ReopenStream {
   // $FF: synthetic field
   final TaskDataAccess val$tda;
   // $FF: synthetic field
   final DataTransformerImpl2 this$0;

   DataTransformerImpl2$3(DataTransformerImpl2 var1, TaskDataAccess var2) {
      this.this$0 = var1;
      this.val$tda = var2;
   }

   @Override
   public InputStream reopen() {
      return this.val$tda.getStream();
   }
}
