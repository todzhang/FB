package ds.core.commanddispatcher;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.command.CommandEventManager;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.util.UtilityConstants;
import ds.core.commandevents.CommandEventImpl;
import ds.core.controller.MutableCoreController;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.event.EventListenerList;
import javax.xml.bind.JAXBException;

public class MultipleCommandDispatcherClient implements EventPublisher, CommandEventManager {
   protected ExecutorService executor = Executors.newSingleThreadExecutor(UtilityConstants.createThreadFactory("MultipleCommandDispatcher"));
   protected MutableCoreController mutableCore;
   protected EventListenerList commandEventListenerList;
   protected Map<Operation, CommandDispatcher> operationToDispatcher;
   protected Collection<CommandDispatcher> dispatchers;
   protected LiveCommandDispatcher liveConnection;
   final Object LOCK = new Object();

   public MultipleCommandDispatcherClient(MutableCoreController mutableCore) {
      this.mutableCore = mutableCore;
      this.operationToDispatcher = new HashMap();
      this.dispatchers = new Vector();
      this.commandEventListenerList = new EventListenerList();
   }

   @Override
   public void publish(CommandEvent commandEvent) {
      this.executor.submit(new MultipleCommandDispatcherClient.DispatchEvent(commandEvent));
   }

   public boolean isEventThreadInterested(CommandEventListener var1, CommandEvent var2) {
      if (var1 == null) {
         return false;
      } else {
         return this.isEventCurrentOperation(var2) || var1.caresAboutRepeatedEvents();
      }
   }

   @Override
   public void addCommandEventListener(CommandEventListener commandEventListener) {
      this.commandEventListenerList.add(CommandEventListener.class, commandEventListener);
   }

   @Override
   public void removeCommandEventListener(CommandEventListener commandEventListener) {
      this.commandEventListenerList.remove(CommandEventListener.class, commandEventListener);
   }

   public void addLiveCommandDispatcher(Socket socket) throws IOException, JAXBException {
      LiveCommandDispatcher liveCommandDispatcher = new LiveCommandDispatcher(this, this.mutableCore, socket, new MultipleCommandDispatcherClient.ObtainedOperationCallback() {
         @Override
         public boolean obtainedOperation(Operation operation, CommandDispatcher commandDispatcher) {
            synchronized(MultipleCommandDispatcherClient.this) {
               if (MultipleCommandDispatcherClient.this.operationToDispatcher.get(operation) != null) {
                  ((CommandDispatcher)MultipleCommandDispatcherClient.this.operationToDispatcher.get(operation)).stop();
               }

               MultipleCommandDispatcherClient.this.operationToDispatcher.put(operation, commandDispatcher);
               MultipleCommandDispatcherClient.this.mutableCore.offerOperation(operation);
               return true;
            }
         }
      });
      this.dispatchers.add(liveCommandDispatcher);
      this.liveConnection = liveCommandDispatcher;
   }

   public boolean addReplayCommandDispatcher(File file) {
      ReplayCommandDispatcher replayCommandDispatcher = new ReplayCommandDispatcher(this, this.mutableCore, file);
      synchronized(this.LOCK) {
         if (Operation.NULL.equals(replayCommandDispatcher.getOperation())) {
            return false;
         } else if (this.operationToDispatcher.get(replayCommandDispatcher.getOperation()) != null) {
            return false;
         } else {
            this.operationToDispatcher.put(replayCommandDispatcher.getOperation(), replayCommandDispatcher);
            this.dispatchers.add(replayCommandDispatcher);
            return true;
         }
      }
   }

   public boolean addReplayCommandDispatcher(ReplayCommandDispatcher replayCommandDispatcher) {
      synchronized(this.LOCK) {
         if (replayCommandDispatcher == null) {
            return false;
         } else if (Operation.NULL.equals(replayCommandDispatcher.getOperation())) {
            return false;
         } else if (this.operationToDispatcher.get(replayCommandDispatcher.getOperation()) != null) {
            return false;
         } else if (this.liveConnection != null && this.liveConnection.getOperation().equals(replayCommandDispatcher.getOperation())) {
            return false;
         } else {
            this.operationToDispatcher.put(replayCommandDispatcher.getOperation(), replayCommandDispatcher);
            this.dispatchers.add(replayCommandDispatcher);
            return true;
         }
      }
   }

   public void addDirectoryMonitor(File dir) {
      this.dispatchers.add(new DirectoryMonitoringCommandDispatcher(this, this.mutableCore, dir, this));
   }

   public void requestCommandListUpdate() throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.requestCommandListUpdate();
      }
   }

   public void requestHelpStatement(String helpType, HostInfo hostInfo) throws JAXBException, IOException {
      if (this.liveConnection != null) {
         this.liveConnection.requestHelpStatement(helpType, hostInfo);
      }
   }

   public void restartCommandOutput(TaskId taskId) throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.restartCommandOutput(taskId);
      }
   }

   public void sendPing() throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.sendPing();
      }
   }

   public void sendPromptReply(int reqId, TaskId taskId, String cmdValue) throws IOException, JAXBException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.sendPromptReply(reqId, taskId, cmdValue);
      }
   }

   public void sendPromptStopped(TaskId taskId, int reqId) throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.sendPromptStopped(taskId, reqId);
      }
   }

   public void sendShutdownNotification() throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection == null) {
         this.mutableCore.shutdown(true);
      } else {
         this.liveConnection.sendShutdownNotification();
      }
   }

   public void setUseHelpCache(boolean useHelpCache) {
      if (this.liveConnection != null) {
         this.liveConnection.setUseHelpCache(useHelpCache);
      }
   }

   public void startCommand(String fullCommand, IdCallback idCallback, Object o, HostInfo prospectiveHost) throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.startCommand(fullCommand, idCallback, o, prospectiveHost);
      }
   }

   public void stopCommand(TaskId taskId) throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.stopCommand(taskId);
      }
   }

   public void stopCommandOutput(TaskId taskId) throws JAXBException, IOException, DispatcherException {
      if (this.liveConnection != null) {
         this.liveConnection.stopCommandOutput(taskId);
      }
   }

   public void disconnect() {
      if (this.liveConnection != null) {
         this.liveConnection.disconnect();
      }
   }

   public boolean isConnected() {
      return this.liveConnection == null ? false : this.liveConnection.isConnected();
   }

   public void addPrefixes(TaskId taskId, List<String> prefixes) throws DispatcherException, IOException, JAXBException {
      if (this.liveConnection != null) {
         this.liveConnection.addPrefixes(taskId, prefixes);
      }
   }

   public void interruptCommand(TaskId taskId) throws JAXBException, IOException {
      if (this.liveConnection != null) {
         this.liveConnection.interruptCommand(taskId);
      }
   }

   public void repeatTasks(CommandEventListener commandEventListener) {
      if (commandEventListener != null) {
         Vector vector = new Vector();
         vector.addAll(this.mutableCore.getTaskList());
         Iterator iterator = vector.iterator();

         while(iterator.hasNext()) {
            Task var4 = (Task)iterator.next();
            CommandEventImpl var5 = new CommandEventImpl(this, CommandEventType.INFO, var4);
            var5.setCurrentOperation(this.isTaskCurrentOperation(var4));
            if (this.isEventThreadInterested(commandEventListener, var5)) {
               commandEventListener.commandEventReceived(var5);
            }
         }

      }
   }

   private boolean isTaskCurrentOperation(Task task) {
      return this.liveConnection == null ? false : this.liveConnection.getOperation().equals(task.getId().getOperation());
   }

   private boolean isEventCurrentOperation(CommandEvent commandEvent) {
      return this.liveConnection == null ? false : this.liveConnection.getOperation().equals(commandEvent.getId().getOperation());
   }

   public int requestStatistics() throws JAXBException, IOException {
      return this.liveConnection == null ? -1 : this.liveConnection.requestStatistics();
   }

   public void sendGuiCommandResponse(int reqId, boolean success) {
      if (this.liveConnection != null) {
         this.liveConnection.sendGuiCommandResponse(reqId, success);
      }
   }

   public interface ObtainedOperationCallback {
      boolean obtainedOperation(Operation operation, CommandDispatcher commandDispatcher);
   }

   private class DispatchEvent implements Runnable {
      CommandEvent event;

      DispatchEvent(CommandEvent event) {
         this.event = event;
      }

      @Override
      public void run() {
         CommandEventListener[] var1 = (CommandEventListener[])MultipleCommandDispatcherClient.this.commandEventListenerList.getListeners(CommandEventListener.class);
         Task var2 = MultipleCommandDispatcherClient.this.mutableCore.getTaskById(this.event.getId());
         if (CommandEventType.START_PROMPT.equals(this.event.getType())) {
            boolean var3 = false;
            synchronized(MultipleCommandDispatcherClient.this.LOCK) {
               CommandEventListener[] var5 = var1;
               int var6 = var1.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  CommandEventListener var8 = var5[var7];
                  if (var8.handlesPromptsForTask(var2, this.event.getReqId())) {
                     var3 = true;
                  }
               }
            }

            if (!var3) {
               MultipleCommandDispatcherClient.this.mutableCore.showPrompt(var2, this.event.getReqId(), this.event.getText());
            }
         }

         if (CommandEventType.STOP_PROMPT.equals(this.event.getType())) {
            MultipleCommandDispatcherClient.this.mutableCore.stopPrompt(var2);
         }

         if (CommandEventType.ENDED.equals(this.event.getType())) {
            MultipleCommandDispatcherClient.this.mutableCore.taskEnded(var2);
         }

         CommandEventListener[] var11 = var1;
         int var4 = var1.length;

         for(int var12 = 0; var12 < var4; ++var12) {
            CommandEventListener var13 = var11[var12];
            if (MultipleCommandDispatcherClient.this.isEventThreadInterested(var13, this.event)) {
               var13.commandEventReceived(this.event);
            }
         }

      }
   }
}
