package ddb.dsz.core.data;

import ddb.dsz.core.task.TaskId;
import java.util.Calendar;

public interface DataEvent extends TransformEvent {
   DataEvent.DataEventType getDataType();

   ObjectValue getData();

   TaskId getTaskId();

   Calendar getTimestamp();

   public enum DataEventType {
      START,
      PARAMETERS,
      DATA,
      DATA_SET_FINISHED,
      FINISH;
   }
}
