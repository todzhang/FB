package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.StopPromptType;
import java.util.logging.Level;

public class StopPromptClosure extends MessageClosure {
   public StopPromptClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getReq() == null) {
         return false;
      } else {
         StopPromptType var2 = message.getReq().getStopPrompt();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      StopPromptType var2 = message.getReq().getStopPrompt();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved stop prompt");
      }

      int var3 = message.getReq().getReqId();
      TaskId var4 = this.createTaskId(var2.getCmdId());
      MutableTask var5 = this.getTaskById(var4);
      if (var5 == null) {
         this.live.getMainSystem().logEvent(Level.SEVERE, "Received prompt request for unknown command");
         this.live.handleUnknownTaskPrompt(message);
      } else {
         var5.setInPromptMode(false);
         TaskId var6 = var5.getParentId();
         String var7 = var5.getCommandName();
         String var8 = var2.getValue();
         CommandEventImpl var9 = new CommandEventImpl(this, CommandEventType.STOP_PROMPT, var7, var8, var4, var6, var5.getTargetId(), var3);
         var9.setCurrentOperation(true);
         var9.setTargetAddress(var5.getTargetId());
         this.live.publishEvent(var9);

         try {
            this.live.sendPromptStopped(var4, var3);
         } catch (Exception var11) {
            this.live.getMainSystem().logEvent(Level.SEVERE, var11.getMessage(), var11);
         }

      }
   }
}
