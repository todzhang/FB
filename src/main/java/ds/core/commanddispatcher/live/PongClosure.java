package ds.core.commanddispatcher.live;

import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import java.util.logging.Level;

public class PongClosure extends MessageClosure {
   public PongClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getRes() == null) {
         return false;
      } else {
         return message.getRes().getPong() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved pong");
      }

   }
}
