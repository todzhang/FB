package ds.core.impl.task;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaskStateAccess extends AbstractDataAccess implements TaskDataAccess {
   public TaskStateAccess(Task var1) {
      super(var1, DataType.STATE, -1);
   }

   @Override
   public InputStreamReader getReader() {
      return null;
   }

   /** @deprecated */
   @Override
   @Deprecated
   public InputStream getStream() {
      return null;
   }

   @Override
   public long getSize() {
      return 0L;
   }

   @Override
   public String getLocation() {
      return null;
   }

   @Override
   public String getLocationType() {
      return null;
   }

   @Override
   public String getRelativeLocation() {
      return null;
   }

   public String toString() {
      return String.format("State");
   }
}
