package ddb.dsz.plugin.requesthandler;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.TaskDataRequestType;
import ddb.dsz.plugin.requesthandler.model.RequestTableModel;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.CancelRequest;
import ddb.dsz.plugin.requesthandler.tranformers.RecordEntryToRequest;
import ddb.dsz.plugin.requesthandler.tranformers.RequestedOperationToRequestType;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/antivirus.png")
@DszName("Request Sender")
@DszDescription("Handles all things request related")
public class RequestSender extends RequestHandler {
   PeerTag connectedTo = null;
   private static final String NOT_CONNECTED = "Not connected:  Requests will be queued for submission";
   private static final String CONNECTED = "Connect:  Requests will be sent to peer";

   public RequestSender() {
      super.setName("Request Sender");
      super.prefferedSize = new Dimension(550, 200);
      super.setCareAboutLocalEvents(true);
   }

   @Override
   protected int init3() {
      this.setStatus("Not connected:  Requests will be queued for submission");
      return 0;
   }

   @Override
   protected void fini3() {
   }

   @Override
   public boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback) {
      RequestedOperation var3 = this.generateOperation(commands);
      if (var3 == null) {
         return false;
      } else {
         if (internalCommandCallback != null) {
            internalCommandCallback.taskingRecieved(commands, (Object)null);
            var3.setCallback(internalCommandCallback);
         }

         if (this.connectedTo == null) {
            this.requests.addRequest(var3);
            return true;
         } else {
            super.publish(RequestedOperationToRequestType.getInstance().transform(var3), (PeerTag)null);
            StringBuilder var4 = new StringBuilder();
            Iterator var5 = commands.iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               var4.append(var6 + "\r\n");
            }

            this.core.logEvent(Level.FINEST, "Received Message:\r\n" + var4.toString());
            return true;
         }
      }
   }

   @Override
   protected Closure getExecutor(CoreController var1) {
      return ClosureUtils.nopClosure();
   }

   @Override
   protected String getCancelName() {
      return "Cancel";
   }

   @Override
   protected void cancel(BigInteger var1) {
      super.publish(CancelRequest.getInstance().transform(var1), (PeerTag)null);
   }

   @Override
   public void fireDeniedRequest() {
      super.contentsChanged();
   }

   @Override
   public void closedConnection(PeerTag peerTag) {
      if (peerTag.equals(this.connectedTo)) {
         this.connectedTo = null;
         this.requests.purge();
      }

      this.setStatus("Not connected:  Requests will be queued for submission");
   }

   @Override
   public void newConnection(PeerTag peerTag) {
      this.connectedTo = peerTag;
      final List var2 = this.requests.getPending();
      this.core.schedule(new Runnable() {
         @Override
         public void run() {
            try {
               Iterator var1 = var2.iterator();

               while(var1.hasNext()) {
                  RequestTableModel.RecordEntry var2x = (RequestTableModel.RecordEntry)var1.next();
                  RequestSender.super.publish(RecordEntryToRequest.getInstance().transform(var2x), (PeerTag)null);
                  RequestSender.this.requests.removeRequest(var2x);
               }
            } catch (Throwable var3) {
               var3.printStackTrace();
            }

         }
      }, 5L, TimeUnit.SECONDS);
      this.setStatus("Connect:  Requests will be sent to peer");
   }

   @Override
   public void requestData(TaskId taskId) {
      RequestType var2 = new RequestType();
      TaskDataRequestType var3 = new TaskDataRequestType();
      var3.setIncludeChildren(true);
      var3.setOperation(taskId.getOperation().getGuid().toString());
      var3.setTaskId(BigInteger.valueOf((long) taskId.getId()));
      var2.setTaskDataRequest(var3);
      this.publish(objFact.createRequest(var2), (PeerTag)null);
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var2.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
