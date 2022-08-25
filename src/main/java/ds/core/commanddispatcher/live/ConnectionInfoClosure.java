package ds.core.commanddispatcher.live;

import ddb.dsz.core.operation.Operation;
import ddb.util.Guid;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.impl.OperationImpl;
import ds.jaxb.ipc.ConnectionInfoType;
import ds.jaxb.ipc.HostInfoType;
import ds.jaxb.ipc.Message;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;

public class ConnectionInfoClosure extends MessageClosure {
   public ConnectionInfoClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getConnectionInfo() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      ConnectionInfoType var2 = message.getInfo().getConnectionInfo();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved connection info message");
      }

      if (Operation.NULL.equals(this.live.getOperation()) && var2.getHost().size() > 0) {
         try {
            Guid var3 = Guid.GenerateGuid(((HostInfoType)var2.getHost().get(0)).getGUID());
            OperationImpl var4 = OperationImpl.GenerateOperation(var3, Calendar.getInstance(TimeZone.getTimeZone("GMT")));
            this.live.setOperation(var4);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      this.live.getMainSystem().updateConnectionInfo(message);
   }
}
