package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.util.Pair;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.IdMapType;
import ds.jaxb.ipc.Message;
import java.util.logging.Level;

public class IdMapClosure extends MessageClosure {
   public IdMapClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getIdMap() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      IdMapType var2 = message.getInfo().getIdMap();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved id map");
      }

      TaskId var3 = this.createTaskId(var2.getCmdId());
      int var4 = var2.getTmpId();
      Task var5 = this.live.getMainSystem().registerId(var4, var3);
      if (var5 != null && var5 instanceof MutableTask) {
         MutableTask var6 = (MutableTask)var5;
         var6.setId(var3);
         Pair var7 = this.live.extractCallback(var4);
         if (var7 == null) {
            this.live.getMainSystem().logEvent(Level.WARNING, String.format("Id map received for unknown task:\r\n\tTmpId: %d == Id: %d", var2.getTmpId(), var2.getCmdId()));
         } else {
            IdCallback var8 = (IdCallback)var7.getFirst();
            Object var9 = var7.getSecond();
            if (var8 != null) {
               var8.idAcquired(var3, var9);
            }

         }
      } else {
         this.live.getMainSystem().logEvent(Level.WARNING, String.format("Id map received for unknown task:\r\n\tTmpId: %d == Id: %d", var2.getTmpId(), var2.getCmdId()));
      }
   }
}
