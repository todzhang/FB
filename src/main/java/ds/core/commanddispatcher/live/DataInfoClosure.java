package ds.core.commanddispatcher.live;

import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.Guid;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.impl.task.FileAccess;
import ds.jaxb.ipc.DataInfoType;
import ds.jaxb.ipc.Message;
import java.io.File;
import java.util.logging.Level;

public class DataInfoClosure extends MessageClosure {
   public DataInfoClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getDataInfo() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      DataInfoType var2 = message.getInfo().getDataInfo();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command data message");
      }

      String var3 = var2.getLog();
      Guid var4 = Guid.GenerateGuid(var2.getTaskId());
      MutableTask var5 = this.getTaskByTaskId(var4);
      if (var5 == null) {
         this.live.getMainSystem().logEvent(Level.WARNING, String.format("Command data received for unknown transaction %s\n LogFile: %s", var4.toString(), var3));
      } else {
         var5.addDataInformation(new FileAccess(var5, DataType.DATA, new File(String.format("%s/%s", this.live.getMainSystem().getLogDirectory(), var3)), var3, var5.getNextOrdinal()));
      }
   }
}
