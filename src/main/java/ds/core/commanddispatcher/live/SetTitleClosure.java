package ds.core.commanddispatcher.live;

import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.SetTitleType;
import java.util.logging.Level;

public class SetTitleClosure extends MessageClosure {
   public SetTitleClosure(LiveCommandDispatcher liveCommandDispatcher) {
      super(liveCommandDispatcher);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message == null) {
         return false;
      } else if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getSetTitle() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      SetTitleType setTitle = message.getInfo().getSetTitle();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved set title: " + setTitle.getValue());
      }

      this.live.getMainSystem().setTitle(setTitle.getValue());
   }
}
