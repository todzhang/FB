package ddb.dsz.core.contextmenu;

import ddb.dsz.core.task.TaskId;

public interface CommandCallbackListener {
   void registerCommand(String var1, TaskId taskId);
}
