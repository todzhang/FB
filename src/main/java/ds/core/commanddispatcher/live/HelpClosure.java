package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.HelpType;
import ds.jaxb.ipc.Message;
import java.util.logging.Level;

public class HelpClosure extends MessageClosure {
   public HelpClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getRes() == null) {
         return false;
      } else {
         return message.getRes().getHelp() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      HelpType var2 = message.getRes().getHelp();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved help message");
      }

      String var3 = var2.getCommand();
      String var4 = var2.getHelpStatement();
      int var5 = message.getRes().getReqId();
      this.live.installHelp(var3, var4);
      CommandEventImpl var6 = new CommandEventImpl(this, CommandEventType.HELP, var3, var4, this.createTaskId(0), this.createTaskId(0), (String)null, var5);
      var6.setCurrentOperation(true);
      this.live.publishEvent(var6);
   }
}
