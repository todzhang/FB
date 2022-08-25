package ds.core.commanddispatcher.live;

import ddb.dsz.core.connection.events.ThrottleEvent;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.ThrottleInfoType;
import java.util.logging.Level;

public class ThrottleClosure extends MessageClosure {
   public ThrottleClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         ThrottleInfoType var2 = message.getInfo().getThrottleInfo();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      ThrottleInfoType var2 = message.getInfo().getThrottleInfo();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved throttle info message");
      }

      ThrottleEvent var3 = new ThrottleEvent(this, var2.getAddress(), var2.getBytesPerSecond());
      this.live.getMainSystem().fireThrottleEvent(var3);
   }
}
