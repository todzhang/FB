package ds.util.contextmenu;

import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.contextmenu.CommandCallbackListener;
import ddb.dsz.core.task.TaskId;

final class RegisterIdCallback implements IdCallback {
   private final CommandCallbackListener callback;
   private final String Label;

   public RegisterIdCallback(CommandCallbackListener var1, String var2) {
      this.callback = var1;
      this.Label = var2;
   }

   public void idAcquired(TaskId taskId, Object var2) {
      this.callback.registerCommand(this.Label, taskId);
   }
}
