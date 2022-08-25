package ddb.dsz.plugin.requesthandler;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.requesthandler.closures.ExecutionClosure;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.AutoApproveType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.BooleanValue;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ExecutedRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.IntegerValue;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ObjectFactory;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ObjectValueType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ParentType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestCompletedType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.StringValue;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.TaskDataType;
import ddb.dsz.plugin.requesthandler.model.RequestStatus;
import ddb.dsz.plugin.requesthandler.model.RequestTableColumns;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.CancelledRequest;
import ddb.dsz.plugin.requesthandler.tranformers.DeniedRequest;
import ddb.dsz.plugin.requesthandler.tranformers.RequestToResponse;
import ddb.util.Guid;
import ddb.util.JaxbCache;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedTableSelection;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Closure;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/antivirus.png")
@DszName("Request Receiver")
@DszDescription("Handles all things request related")
public class RequestReceiver extends RequestHandler {
   public static final String AUTO_APPROVE = "/RequestHandler/autoApprove.xml";
   public static final String TIME_BETWEEN = "-delay";
   public static final String MAX_RUNNING = "-max";
   int timeBetweenChecks = 2;
   int nextId = 0;
   final Object threadLock = new Object();
   CheckableFilterList<PeerTag> list;
   List<PeerTag> approvedList = new Vector();
   List<PeerTag> allPeers = new Vector();
   List<String> autoApprove = new Vector();
   final Set<Task> remotedTasks = new HashSet();
   private Runnable executeRequest = new Runnable() {
      @Override
      public void run() {
         boolean var1 = true;
         synchronized(RequestReceiver.this.remotedTasks) {
            HashSet var3 = new HashSet();
            Iterator var4 = RequestReceiver.this.remotedTasks.iterator();

            while(true) {
               Task var5;
               if (!var4.hasNext()) {
                  var4 = var3.iterator();

                  while(var4.hasNext()) {
                     var5 = (Task)var4.next();
                     RequestReceiver.this.remotedTasks.remove(var5);
                  }

                  var1 = RequestReceiver.this.remotedTasks.isEmpty();
                  break;
               }

               var5 = (Task)var4.next();
               if (!var5.isAlive()) {
                  var3.add(var5);
               }
            }
         }

         RequestReceiver.this.requests.startNextCommand(var1);
      }
   };

   public RequestReceiver() {
      super.setName("Request Receiver");
      super.prefferedSize = new Dimension(550, 200);
      super.setCareAboutLocalEvents(true);
   }

   @Override
   protected int init3() {
      this.requests.setStatus(this.getStatus());
      this.list = new CheckableFilterList("Requestors", new CheckedTableSelection<PeerTag>() {
         @Override
         public void selected(PeerTag var1, boolean var2) {
            synchronized(RequestReceiver.this.threadLock) {
               if (var2) {
                  RequestReceiver.this.approvedList.add(var1);
               } else {
                  while(true) {
                     if (RequestReceiver.this.approvedList.remove(var1)) {
                        continue;
                     }
                  }
               }

            }
         }
      }, new Comparator<PeerTag>() {
         @Override
         public int compare(PeerTag o1, PeerTag o2) {
            if (o1 == o2) {
               return 0;
            } else {
               return o1 == null ? 1 : o1.compareTo(o2);
            }
         }
      });
      JSplitPane var1 = new JSplitPane(1, this.list, this.mainPanel);
      super.setDisplay(var1);
      var1.setDividerLocation(200);
      var1.setOneTouchExpandable(true);

      try {
         JAXBContext var2 = null;
         Unmarshaller var3 = null;
         var2 = JaxbCache.getContext(ObjectFactory.class);
         var3 = var2.createUnmarshaller();
         Object var4 = var3.unmarshal(RequestReceiver.class.getResource("/RequestHandler/autoApprove.xml"));
         if (var4 instanceof JAXBElement) {
            var4 = ((JAXBElement)JAXBElement.class.cast(var4)).getValue();
         }

         if (var4 instanceof AutoApproveType) {
            this.autoApprove.addAll(((AutoApproveType)AutoApproveType.class.cast(var4)).getIdentifier());
         }
      } catch (Exception var5) {
      }

      this.core.scheduleWithFixedDelay(this.executeRequest, 10L, (long)this.timeBetweenChecks, TimeUnit.SECONDS);
      return 0;
   }

   public void registerRemoteCommand(TaskId var1) {
      Task var2 = this.core.getTaskById(var1);
      if (var2 != null) {
         synchronized(this.remotedTasks) {
            this.remotedTasks.add(var2);
         }
      }
   }

   @Override
   protected boolean parseArgument3(String var1, String var2) {
      int var3;
      if (var1.equalsIgnoreCase("-delay")) {
         try {
            var3 = Integer.parseInt(var2);
            if (var3 >= 0) {
               this.timeBetweenChecks = var3;
            }
         } catch (Exception var5) {
         }
      }

      if (var1.equalsIgnoreCase("-max")) {
         try {
            var3 = Integer.parseInt(var2);
            if (var3 >= 0) {
               this.requests.setMaximumRunning(var3);
            }
         } catch (Exception var4) {
         }
      }

      return false;
   }

   @Override
   public void handleRequest(RequestedOperation var1, PeerTag var2) {
      var1.setId(BigInteger.valueOf((long)(this.nextId++)));
      boolean var3 = true;
      synchronized(this.threadLock) {
         this.requests.addRequest(var1);
         this.publish(RequestToResponse.getInstance().transform(var1), (PeerTag)null);
         if (this.approvedList.contains(var2) || this.approveByName(var2.getPeerName())) {
            this.requests.enable(var1.getId());
            var3 = false;
         }
      }

      if (var3) {
         this.fireNewRequest();
      }

   }

   private boolean approveByName(String var1) {
      return this.autoApprove.contains(var1);
   }

   @Override
   public void executeRequest(RequestedOperation var1) {
   }

   @Override
   public void handleCancel(BigInteger var1, PeerTag var2) {
      synchronized(this.threadLock) {
         if (this.requests.cancelledRequest(var1, var2)) {
            this.publish(CancelledRequest.getInstance().transform(var1), (PeerTag)null);
         }

      }
   }

   @Override
   protected void approve(BigInteger var1) {
      synchronized(this.threadLock) {
         this.requests.enable(var1);
      }
   }

   @Override
   protected void force(BigInteger var1) {
      synchronized(this.threadLock) {
         this.requests.force(var1);
      }
   }

   @Override
   public boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback) {
      RequestedOperation var3 = this.generateOperation(commands);
      if (var3 != null) {
         var3.setId(BigInteger.valueOf((long)(this.nextId++)));
         var3.setLocal();
         var3.setSource("Live Operation");
         if (internalCommandCallback != null) {
            internalCommandCallback.taskingRecieved(commands, var3.getId());
            var3.setCallback(internalCommandCallback);
         }

         synchronized(this.threadLock) {
            this.requests.addRequest(var3);
            this.publish(RequestToResponse.getInstance().transform(var3), (PeerTag)null);
            this.requests.enable(var3.getId());
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected Closure getExecutor(CoreController var1) {
      return ExecutionClosure.getInstance(this, var1);
   }

   public void sendStarted(BigInteger var1, TaskId var2, PeerTag var3) {
      if (!var1.equals(RequestedOperation.NO_ID)) {
         this.requests.addTask(var2);
         ExecutedRequestType var4 = new ExecutedRequestType();
         var4.setReqId(var1);
         var4.setTaskId(BigInteger.valueOf((long)var2.getId()));
         var4.setOperation(var2.getOperation().getGuid().toString());
         ResponseType var5 = new ResponseType();
         var5.setExecutedRequest(var4);
         this.publish(objFact.createResponse(var5), var3);
      }
   }

   @Override
   protected String getCancelName() {
      return "Deny";
   }

   @Override
   protected boolean allowEnabled() {
      return true;
   }

   @Override
   protected boolean forceEnabled() {
      return true;
   }

   @Override
   protected void cancel(BigInteger var1) {
      this.handleCancel(var1, (PeerTag)null);
   }

   @Override
   public void fireNewRequest() {
      super.contentsChanged();
   }

   @Override
   public void newConnection(final PeerTag peerTag) {
      synchronized(this.threadLock) {
         this.allPeers.add(peerTag);
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               RequestReceiver.this.list.addElement(peerTag, false);
            }
         });

         for(int var3 = 0; var3 < this.requests.getRowCount(); ++var3) {
            RequestedOperation var4 = (RequestedOperation)RequestedOperation.class.cast(this.requests.getValueAt(var3, RequestTableColumns.DESCRIPTION));
            this.publish(RequestToResponse.getInstance().transform(var4), peerTag);
            RequestStatus var5 = (RequestStatus)RequestStatus.class.cast(this.requests.getValueAt(var3, RequestTableColumns.STATUS));
            switch(var5) {
            case CANCELLED:
               this.publish(CancelledRequest.getInstance().transform(var4.getId()), peerTag);
               break;
            case DENIED:
               this.publish(DeniedRequest.getInstance().transform(var4.getId()), peerTag);
               break;
            case EXECUTED:
               this.sendStarted(var4.getId(), var4.getTaskId(), peerTag);
               break;
            case PENDING:
               this.publish(RequestToResponse.getInstance().transform(var4), peerTag);
            }
         }

      }
   }

   @Override
   public void closedConnection(final PeerTag peerTag) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            RequestReceiver.this.list.deleteElement(peerTag);
         }
      });
      synchronized(this.threadLock) {
         this.allPeers.remove(peerTag);
      }
   }

   @Override
   public void sendData(PeerTag var1, Guid var2, BigInteger var3, boolean var4) {
      Operation var5 = this.core.getOperation();
      if (var2 != null) {
         var5 = this.core.getOperationById(var2);
      }

      Collection var6 = this.core.getTaskList();
      Iterator var7 = var6.iterator();

      while(true) {
         Task var8;
         do {
            do {
               if (!var7.hasNext()) {
                  return;
               }

               var8 = (Task)var7.next();
            } while(!var8.getId().idMatch(var3.intValue()));
         } while(var5 != null && !var8.getId().getOperation().equals(var5));

         this.sendData(var1, var8, var4);
      }
   }

   private void sendData(final PeerTag var1, final Task var2, boolean var3) {
      final DataTransformer var4 = DataTransformer.newInstance();
      if (var4 != null) {
         var4.addClosure(ClosureFactory.newVariableClosure(this.core, var2, new Closure() {
            public void execute(Object var1x) {
               boolean var2x;
               synchronized(RequestReceiver.this.threadLock) {
                  var2x = !RequestReceiver.this.allPeers.contains(var1);
               }

               if (var2x) {
                  var4.stop();
               } else {
                  if (var1x instanceof DataEvent) {
                     DataEvent var3 = (DataEvent)var1x;
                     TaskDataType var4x = new TaskDataType();
                     var4x.setOperation(var2.getId().getOperation().getGuid().toString());
                     var4x.setTaskId(BigInteger.valueOf((long)var2.getId().getId()));
                     if (var2.getParentId() != null && !var2.getParentId().equals(TaskId.NULL) && !var2.getParentId().equals(TaskId.UNINITIALIZED_ID)) {
                        ParentType var5 = new ParentType();
                        var5.setOperation(var2.getParentId().getOperation().getGuid().toString());
                        var5.setTaskId(BigInteger.valueOf((long)var2.getParentId().getId()));
                        var4x.setParent(var5);
                     }

                     ObjectValueType var8 = new ObjectValueType();
                     var4x.setData(var8);
                     this.recursivelyAdd(var3.getData(), var8);
                     ResponseType var6 = new ResponseType();
                     var6.setTaskData(var4x);
                     RequestReceiver.this.publish(RequestHandler.objFact.createResponse(var6), var1);
                  }

               }
            }

            private void recursivelyAdd(ObjectValue var1x, ObjectValueType var2x) {
               var2x.setName("");
               Iterator var3 = var1x.getObjectNames().iterator();

               String var4x;
               Iterator var5;
               while(var3.hasNext()) {
                  var4x = (String)var3.next();
                  var5 = var1x.getObjects(var4x).iterator();

                  while(var5.hasNext()) {
                     ObjectValue var6 = (ObjectValue)var5.next();
                     ObjectValueType var7 = new ObjectValueType();
                     this.recursivelyAdd(var6, var7);
                     var7.setName(var4x);
                     var2x.getObjectValue().add(var7);
                  }
               }

               var3 = var1x.getBooleanNames().iterator();

               while(var3.hasNext()) {
                  var4x = (String)var3.next();
                  var5 = var1x.getBooleans(var4x).iterator();

                  while(var5.hasNext()) {
                     Boolean var8 = (Boolean)var5.next();
                     BooleanValue var11 = new BooleanValue();
                     var11.setName(var4x);
                     var11.setValue(var8);
                     var2x.getBooleanValue().add(var11);
                  }
               }

               var3 = var1x.getIntegerNames().iterator();

               while(var3.hasNext()) {
                  var4x = (String)var3.next();
                  var5 = var1x.getIntegers(var4x).iterator();

                  while(var5.hasNext()) {
                     Long var9 = (Long)var5.next();
                     IntegerValue var12 = new IntegerValue();
                     var12.setName(var4x);
                     var12.setValue(BigInteger.valueOf(var9));
                     var2x.getIntegerValue().add(var12);
                  }
               }

               var3 = var1x.getStringNames().iterator();

               while(var3.hasNext()) {
                  var4x = (String)var3.next();
                  var5 = var1x.getStrings(var4x).iterator();

                  while(var5.hasNext()) {
                     String var10 = (String)var5.next();
                     StringValue var13 = new StringValue();
                     var13.setName(var4x);
                     var13.setValue(var10);
                     var2x.getStringValue().add(var13);
                  }
               }

            }
         }));
         var4.addTask(var2);
         if (var3) {
            Iterator var5 = this.core.getTaskList().iterator();

            while(var5.hasNext()) {
               Task var6 = (Task)var5.next();
               if (var6.getParentId().equals(var2.getId())) {
                  this.sendData(var1, var6, var3);
               }
            }
         }

      }
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      super.commandEnded(var1);
      this.requests.removeTask(var1.getId());
      Task var2 = this.core.getTaskById(var1.getId());
      if (var2 != null) {
         BigInteger var3 = this.requests.requestForTask(var2);
         if (var3 != null && var3 != RequestedOperation.NO_ID) {
            RequestCompletedType var4 = new RequestCompletedType();
            var4.setReqId(var3);
            var4.setStatus(var2.getStateString());
            var4.setTaskId(BigInteger.valueOf((long)var2.getId().getId()));
            var4.setOperation(var2.getId().getOperation().getGuid().toString());
            var4.setValue("");
            ResponseType var5 = new ResponseType();
            var5.setRequestCompleted(var4);
            this.publish(objFact.createResponse(var5), (PeerTag)null);
         }
      }
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
