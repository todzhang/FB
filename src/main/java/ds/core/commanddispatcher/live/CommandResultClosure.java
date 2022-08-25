package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.CommandResultType;
import ds.jaxb.ipc.InfoType;
import ds.jaxb.ipc.Message;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

public class CommandResultClosure extends MessageClosure {
   public CommandResultClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getCommandResult() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      InfoType var2 = message.getInfo();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command result message");
      }

      TaskId var3 = this.createTaskId(var2.getCmdId());
      MutableTask var4 = this.getTaskById(var3);
      if (var4 == null) {
         try {
            this.live.getMainSystem().logEvent(Level.WARNING, "Command result received for unknown command" + this.live.getFormatter().formatMessageAsString(message));
         } catch (JAXBException var10) {
            this.live.getMainSystem().logEvent(Level.WARNING, var10.getMessage(), var10);
         }

      } else {
         CommandResultType var5 = var2.getCommandResult();
         var4.setResultString(var5.getValue());
         var4.setState(TaskState.parseResult(var5.getValue()));
         String var6 = var4.getCommandName();
         String var7 = var5.getValue();
         TaskId var8 = var4.getParentId();
         CommandEventImpl var9 = new CommandEventImpl(this, CommandEventType.ENDED, var6, var7, var3, var8, var4.getTargetId());
         var9.setCurrentOperation(true);
         var9.setTargetAddress(var4.getTargetId());
         this.live.publishEvent(var9);
         var4.notifyObservers();
      }
   }
}
