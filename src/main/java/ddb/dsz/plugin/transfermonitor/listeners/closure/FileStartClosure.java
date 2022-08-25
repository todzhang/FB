package ddb.dsz.plugin.transfermonitor.listeners.closure;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.listeners.RetrieveClosureInterface;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.util.GeneralUtilities;
import java.util.Calendar;

public class FileStartClosure extends ObjectValueClosure {
   private final RetrieveClosureInterface listener;

   public FileStartClosure(RetrieveClosureInterface var1) {
      this.listener = var1;
   }

   public void execute(ObjectValue var1, Task var2) {
      String var3 = var1.getString("FileName");
      Long var4 = var1.getInteger("Size");
      String var5 = var1.getString("id");
      TransferRecord var6 = this.listener.getRecord(var2.getId(), var5, true, false);
      if (var6 == null) {
         System.out.println("FileStart:  ABORT");
      } else {
         var6.setResumable(this.listener.isResumable());
         var6.setSize(var4);
         var6.setRemote(var3);
         var6.setAccessed(GeneralUtilities.stringToCalendar(var1.getString("FileTimes::Accessed::Time"), (Calendar)null));
         var6.setCreated(GeneralUtilities.stringToCalendar(var1.getString("FileTimes::Created::Time"), (Calendar)null));
         var6.setModified(GeneralUtilities.stringToCalendar(var1.getString("FileTimes::Modified::Time"), (Calendar)null));
         this.listener.recordChanged(var6);
      }
   }
}
