package ddb.dsz.plugin.transfermonitor.listeners.closure;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.listeners.RetrieveClosureInterface;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.model.TransferState;

public class FileStopClosure extends ObjectValueClosure {
   private final RetrieveClosureInterface listener;

   public FileStopClosure(RetrieveClosureInterface var1) {
      this.listener = var1;
   }

   public void execute(ObjectValue var1, Task var2) {
      String var3 = var1.getString("id");
      Long var4 = var1.getInteger("written");
      Boolean var5 = var1.getBoolean("Successful");
      TransferRecord var6 = this.listener.getRecord(var2.getId(), var3, true, true);
      if (var6 == null) {
         System.out.println("FileStop:  ABORT");
      } else {
         if (var5) {
            var6.setSize(var4);
            var6.setTransfered(var4);
            var6.setState(TransferState.DONE);
         } else {
            var6.setState(TransferState.DONE);
         }

         this.listener.recordChanged(var6);
      }
   }
}
