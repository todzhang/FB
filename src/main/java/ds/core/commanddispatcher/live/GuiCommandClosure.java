package ds.core.commanddispatcher.live;

import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.GuiCommandImpl;
import ds.jaxb.ipc.GuiCommandRequest;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.RequestType;
import java.util.logging.Level;

public class GuiCommandClosure extends MessageClosure {
   public GuiCommandClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getReq() == null) {
         return false;
      } else {
         return message.getReq().getGuiCommand() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      RequestType var2 = message.getReq();
      GuiCommandRequest var3 = var2.getGuiCommand();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved gui command message");
      }

      TaskId var4 = this.createTaskId(var3.getCmdId());
      GuiCommandImpl var5 = new GuiCommandImpl(this, var4, var3.getArgument(), var2.getReqId());
      var5.setCurrentOperation(true);
      this.live.publishEvent(var5);
   }
}
