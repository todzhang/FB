package ds.core.commanddispatcher.live;

import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import java.util.logging.Level;

public class ShutdownClosure extends MessageClosure {
   public ShutdownClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getCmd() == null) {
         return false;
      } else {
         return message.getCmd().getShutdown() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved shutdown message");
      }

      this.live.getMainSystem().shutdown(true);
   }
}
