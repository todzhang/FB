package ddb.dsz.plugin.transfermonitor.listeners;

import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;

public interface RetrieveClosureInterface {
   void recordChanged(TransferRecord var1);

   TransferRecord getRecord(TaskId var1, String var2, boolean var3, boolean var4);

   boolean isResumable();
}
