package ddb.dsz.core.command;

import ddb.dsz.core.task.TaskId;

public interface IdCallback {
   void idAcquired(TaskId taskId, Object var2);
}
