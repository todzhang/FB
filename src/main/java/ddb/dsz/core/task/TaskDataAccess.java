package ddb.dsz.core.task;

import java.io.InputStream;
import java.io.Reader;

public interface TaskDataAccess {
   Reader getReader();

   /** @deprecated */
   @Deprecated
   InputStream getStream();

   long getSize();

   String getLocation();

   String getLocationType();

   String getRelativeLocation();

   Task getTask();

   TaskDataAccess.DataType getType();

   int getOrdinal();

   boolean isGenerated();

   public enum DataType {
      LOG,
      TASKING,
      DATA,
      STATE;
   }
}
