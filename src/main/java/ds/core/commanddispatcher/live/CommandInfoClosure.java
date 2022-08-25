package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.Guid;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.core.impl.task.FileAccess;
import ds.jaxb.ipc.CommandInfoType;
import ds.jaxb.ipc.Message;
import java.io.File;
import java.util.logging.Level;

public class CommandInfoClosure extends MessageClosure {
   public CommandInfoClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         CommandInfoType var2 = message.getInfo().getCommandInfo();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      CommandInfoType var2 = message.getInfo().getCommandInfo();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command info message");
      }

      TaskId var3 = this.createTaskId(message.getInfo().getCmdId());
      String var4 = var2.getDisplayTransform();
      String var5 = var2.getStorageTransform();
      String var6 = var2.getLog();
      String var7 = var2.getScreenLog();
      String var8 = var2.getTargetAddress();
      String var9 = var2.getResourceDirectory();
      MutableTask var10 = this.getTaskById(var3);
      if (var10 == null) {
         this.live.getMainSystem().logEvent(Level.WARNING, "Command info received for unknown command\n LogFile: " + var6);
      } else {
         var10.setHost(this.live.getMainSystem().getHostById(var8));
         var10.setTaskingInformation(new FileAccess(var10, DataType.TASKING, new File(this.live.getMainSystem().getLogDirectory(), var6), var6, var10.getNextOrdinal()));
         if (var7 != null) {
            var10.setTaskLog(new FileAccess(var10, DataType.LOG, new File(this.live.getMainSystem().getLogDirectory(), var7), var7, -1));
         }

         var10.setDisplayTransform(var4);
         var10.setStorageTransform(var5);
         if (var9 != null) {
            var10.setResourceDirectory(var9);
         }

         var10.setTaskId(Guid.GenerateGuid(var2.getTaskId()));
         this.registerTaskId(var10);
         var10.notifyObservers();
         CommandEventImpl var11 = new CommandEventImpl(this, CommandEventType.INFO, var10.getCommandName(), "Info received", var3, var10.getParentId(), var10.getTargetId());
         var11.setCurrentOperation(true);
         var11.setTargetAddress(var10.getTargetId());
         this.live.publishEvent(var11);
      }
   }
}
