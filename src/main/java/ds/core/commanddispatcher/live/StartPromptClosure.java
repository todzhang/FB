package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.StartPromptType;
import java.util.logging.Level;

public class StartPromptClosure extends MessageClosure {
   public StartPromptClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getReq() == null) {
         return false;
      } else {
         StartPromptType var2 = message.getReq().getStartPrompt();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      StartPromptType var2 = message.getReq().getStartPrompt();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved start prompt");
      }

      int var3 = message.getReq().getReqId();
      TaskId var4 = this.createTaskId(var2.getCmdId());
      MutableTask var5 = this.getTaskById(var4);
      if (var5 == null) {
         this.live.getMainSystem().logEvent(Level.SEVERE, "Received prompt request for unknown command");
         this.live.handleUnknownTaskPrompt(message);
      } else {
         var5.setInPromptMode(true);
         TaskId var6 = var5.getParentId();
         String var7 = var5.getCommandName();
         String var8 = var2.getValue();
         CommandEventImpl var9 = new CommandEventImpl(this, CommandEventType.START_PROMPT, var7, var8, var4, var6, var5.getTargetId(), var3);
         var9.setCurrentOperation(true);
         var9.setTargetAddress(var5.getTargetId());
         this.live.publishEvent(var9);
      }
   }
}
