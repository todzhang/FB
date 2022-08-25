package ddb.dsz.plugin.transfermonitor.listeners.closure;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.listeners.RetrieveClosureInterface;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;

public class FileWriteClosure extends ObjectValueClosure {
   private final RetrieveClosureInterface listener;

   public FileWriteClosure(RetrieveClosureInterface var1) {
      this.listener = var1;
   }

   public void execute(ObjectValue var1, Task var2) {
      String var3 = var1.getString("id");
      Long var4 = var1.getInteger("bytes");
      TransferRecord var5 = this.listener.getRecord(var2.getId(), var3, true, true);
      if (var5 == null) {
         System.out.println("FileWrite:  ABORT");
      } else {
         var5.addTransfered(var4);
         this.listener.recordChanged(var5);
      }
   }
}
