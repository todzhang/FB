package ddb.dsz.plugin.transfermonitor.listeners.closure;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.listeners.RetrieveClosureInterface;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;

public class LocalNameClosure extends ObjectValueClosure {
   private final RetrieveClosureInterface listener;

   public LocalNameClosure(RetrieveClosureInterface var1) {
      this.listener = var1;
   }

   public void execute(ObjectValue var1, Task var2) {
      String var3 = var1.getString("LocalName");
      String var4 = var1.getString("subdir");
      String var5 = var1.getString("id");
      TransferRecord var6 = this.listener.getRecord(var2.getId(), var5, true, true);
      if (var6 == null) {
         System.out.println("LocalName:  ABORT");
      } else {
         var6.setSubDir(String.format("%s/%s", var2.getHost().getId(), var4));
         var6.setLocal(var3);
         this.listener.recordChanged(var6);
      }
   }
}
