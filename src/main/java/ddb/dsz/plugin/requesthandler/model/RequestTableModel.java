package ddb.dsz.plugin.requesthandler.model;

import ddb.detach.TabbableStatus;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.CommandTransformer;
import ddb.dsz.plugin.requesthandler.tranformers.DisplayTransformer;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.AbstractEnumeratedTableModel.FireTableDataChanged;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsDeleted;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.BoundedRangeModel;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public class RequestTableModel extends AbstractEnumeratedTableModel<RequestTableColumns> {
   private static final List<Set<RequestStatus>> ORDER = new Vector();
   private final List<RequestTableModel.RecordEntry> dataQueue = new ArrayList();
   RequestHandler handler;
   Closure execution;
   final Object editLock = new Object();
   int nextIndex = 0;
   int running = 0;
   int maxRunning = 5;
   int maximumShown = 0;
   TabbableStatus status = null;
   Set<TaskId> currentTasks = Collections.synchronizedSet(new HashSet());
   Executor EventLogger = Executors.newSingleThreadExecutor();
   File logFile;

   public RequestTableModel(RequestHandler var1, Closure var2) {
      super(RequestTableColumns.class);
      CoreController var3 = var1.getCoreController();
      Calendar var4 = Calendar.getInstance();
      this.logFile = new File(String.format("%s/GuiRequestLog/Requests_%04d-%02d-%02dT%02d-%02d-%02d.%03d.txt", var3.isLiveOperation() ? var3.getLogDirectory() : var3.getUserConfigDirectory(), var4.get(1), var4.get(2) + 1, var4.get(5), var4.get(11), var4.get(12), var4.get(13), var4.get(14)));
      this.logFile.getParentFile().mkdirs();
      this.handler = var1;
      this.execution = var2;
   }

   @Override
   public int getRowCount() {
      return this.dataQueue.size();
   }

   @Override
   public Object getValueAt(int i, RequestTableColumns e) {
      if (i < 0) {
         return null;
      } else {
         RequestTableModel.RecordEntry var3 = null;
         synchronized(this.dataQueue) {
            Iterator var5 = this.dataQueue.iterator();

            while(true) {
               if (i < 0) {
                  break;
               }

               if (!var5.hasNext()) {
                  return null;
               }

               var3 = (RequestTableModel.RecordEntry)var5.next();
               --i;
            }
         }

         if (var3 == null) {
            return null;
         } else {
            switch(e) {
            case DESCRIPTION:
               return var3.operation;
            case STATUS:
               return var3.status;
            case SOURCE:
               return var3.source;
            case SCOPE:
               return var3.operation;
            case HOST:
               return var3.operation.getData("host");
            default:
               return null;
            }
         }
      }
   }

   @Override
   public String getColumnName(RequestTableColumns e) {
      return e.getName();
   }

   @Override
   public Class<?> getColumnClass(RequestTableColumns e) {
      return e.getType();
   }

   public void setStatus(TabbableStatus var1) {
      this.status = var1;
   }

   public void startNextCommand(boolean var1) {
      if (this.currentTasks.size() < this.maxRunning) {
         try {
            RequestedOperation var2 = null;
            synchronized(this.dataQueue) {
               if (this.dataQueue.size() == 0) {
                  return;
               }

               RequestTableModel.RecordEntry var4 = (RequestTableModel.RecordEntry)this.dataQueue.get(0);
               if (var4 == null) {
                  return;
               }

               if (var4.status.equals(RequestStatus.ALLOWED)) {
                  this.EventLogger.execute(new RequestTableModel.StartRequest(var4.operation));
                  var4.status = RequestStatus.EXECUTED;
                  var2 = var4.operation;
                  Collections.sort(this.dataQueue);
                  EventQueue.invokeLater(new FireTableDataChanged());
               }
            }

            if (var2 != null) {
               this.handler.executeRequest(var2);
               this.execution.execute(var2);
            }

         } finally {
            this.updateStatus();
         }
      }
   }

   public BigInteger requestForTask(Task var1) {
      synchronized(this.editLock) {
         for(int var3 = 0; var3 < this.getRowCount(); ++var3) {
            RequestedOperation var4 = (RequestedOperation)RequestedOperation.class.cast(this.getValueAt(var3, RequestTableColumns.DESCRIPTION));
            if (var1.getId().equals(var4.getTaskId())) {
               return var4.getId();
            }
         }

         return RequestedOperation.NO_ID;
      }
   }

   public void clear() {
      synchronized(this.dataQueue) {
         this.dataQueue.clear();
         EventQueue.invokeLater(new FireTableDataChanged());
      }
   }

   public void addRequest(RequestedOperation var1) {
      synchronized(this.dataQueue) {
         RequestTableModel.RecordEntry var3 = new RequestTableModel.RecordEntry();
         var3.operation = var1;
         var3.source = var1.getSource();
         this.dataQueue.add(var3);
         Collections.sort(this.dataQueue);
         EventQueue.invokeLater(new FireTableDataChanged());
         this.EventLogger.execute(new RequestTableModel.NewRequest(var1));
      }
   }

   public void removeRequest(RequestTableModel.RecordEntry var1) {
      synchronized(this.dataQueue) {
         int var3 = this.dataQueue.indexOf(var1);
         if (var3 >= 0) {
            this.dataQueue.remove(var3);
            EventQueue.invokeLater(new FireTableRowsDeleted( var3, var3));
         }
      }
   }

   private boolean doAction(BigInteger var1, Predicate var2, Closure var3) {
      RequestTableModel.RecordEntry var4 = null;

      try {
         synchronized(this.dataQueue) {
            Iterator var6 = this.dataQueue.iterator();

            RequestTableModel.RecordEntry var7;
            do {
               if (!var6.hasNext()) {
                  return false;
               }

               var7 = (RequestTableModel.RecordEntry)var6.next();
            } while(!var7.operation.getId().equals(var1));

            var4 = var7;
            boolean var8 = true;
            if (var2 != null) {
               var8 = var2.evaluate(var7);
            }

            Collections.sort(this.dataQueue);
            EventQueue.invokeLater(new FireTableDataChanged());
            boolean var9 = var8;
            return var9;
         }
      } finally {
         if (var3 != null) {
            var3.execute(var4);
         }

      }
   }

   public boolean cancelledRequest(BigInteger var1, final Object var2) {
      return this.doAction(var1, new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            RequestTableModel.RecordEntry var2x = (RequestTableModel.RecordEntry)var1;
            if (!var2x.status.equals(RequestStatus.PENDING) && !var2x.status.equals(RequestStatus.ALLOWED) || var2 != null && var2x.operation.getTag() != null && !var2x.operation.getTag().equals(var2)) {
               return false;
            } else {
               RequestTableModel.this.EventLogger.execute(RequestTableModel.this.new CancelledRequest(var2x.operation));
               var2x.allow = Boolean.FALSE;
               var2x.status = RequestStatus.CANCELLED;
               return true;
            }
         }
      }, (Closure)null);
   }

   public boolean enable(BigInteger var1) {
      return this.doAction(var1, new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            RequestTableModel.RecordEntry var2 = (RequestTableModel.RecordEntry)var1;
            if (var2.status.equals(RequestStatus.PENDING)) {
               RequestTableModel.this.EventLogger.execute(RequestTableModel.this.new EnabledRequest(var2.operation));
               var2.allow = Boolean.TRUE;
               var2.status = RequestStatus.ALLOWED;
               return true;
            } else {
               return false;
            }
         }
      }, (Closure)null);
   }

   public boolean force(BigInteger var1) {
      return this.doAction(var1, new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            RequestTableModel.RecordEntry var2 = (RequestTableModel.RecordEntry)var1;
            if (var2.status.equals(RequestStatus.PENDING)) {
               RequestTableModel.this.EventLogger.execute(RequestTableModel.this.new StartRequest(var2.operation));
               var2.status = RequestStatus.EXECUTED;
               return true;
            } else {
               return false;
            }
         }
      }, new Closure() {
         @Override
         public void execute(Object var1) {
            if (var1 != null) {
               RequestTableModel.this.handler.executeRequest(((RequestTableModel.RecordEntry)RequestTableModel.RecordEntry.class.cast(var1)).operation);
               RequestTableModel.this.execution.execute(((RequestTableModel.RecordEntry)RequestTableModel.RecordEntry.class.cast(var1)).operation);
            }

         }
      });
   }

   public boolean deniedRequest(BigInteger var1) {
      return this.doAction(var1, new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            RequestTableModel.RecordEntry var2 = (RequestTableModel.RecordEntry)var1;
            if (!var2.status.equals(RequestStatus.PENDING) && !var2.status.equals(RequestStatus.ALLOWED)) {
               return false;
            } else {
               RequestTableModel.this.EventLogger.execute(RequestTableModel.this.new DeniedRequest(var2.operation));
               var2.allow = Boolean.FALSE;
               var2.status = RequestStatus.DENIED;
               RequestTableModel.this.handler.fireDeniedRequest();
               return true;
            }
         }
      }, (Closure)null);
   }

   public boolean executedRequest(BigInteger var1) {
      return this.doAction(var1, new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            RequestTableModel.RecordEntry var2 = (RequestTableModel.RecordEntry)var1;
            if (!var2.status.equals(RequestStatus.PENDING) && !var2.status.equals(RequestStatus.ALLOWED)) {
               return false;
            } else {
               RequestTableModel.this.EventLogger.execute(RequestTableModel.this.new ExecutedRequest(var2.operation));
               var2.status = RequestStatus.EXECUTED;
               RequestTableModel.this.handler.fireExecutedRequest();
               return true;
            }
         }
      }, (Closure)null);
   }

   public void setMaximum(int var1) {
      this.maximumShown = var1;
   }

   public List<RequestTableModel.RecordEntry> getPending() {
      Vector var1 = new Vector();
      synchronized(this.dataQueue) {
         Iterator var3 = this.dataQueue.iterator();

         while(var3.hasNext()) {
            RequestTableModel.RecordEntry var4 = (RequestTableModel.RecordEntry)var3.next();
            if (var4.status.equals(RequestStatus.PENDING)) {
               var1.add(var4);
            }
         }

         return var1;
      }
   }

   public void purge() {
      synchronized(this.dataQueue) {
         Iterator var2 = this.dataQueue.iterator();

         while(true) {
            RequestTableModel.RecordEntry var3;
            do {
               if (!var2.hasNext()) {
                  EventQueue.invokeLater(new FireTableDataChanged());
                  return;
               }

               var3 = (RequestTableModel.RecordEntry)var2.next();
            } while(!var3.status.equals(RequestStatus.PENDING) && !var3.status.equals(RequestStatus.ALLOWED));

            this.dataQueue.remove(var3);
         }
      }
   }

   public void removeTask(TaskId var1) {
      if (this.currentTasks.remove(var1)) {
         this.updateStatus();
      }

   }

   public void addTask(TaskId var1) {
      if (this.currentTasks.add(var1)) {
         this.updateStatus();
      }

   }

   private void updateStatus() {
      if (this.status != null) {
         if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
               @Override
               public void run() {
                  RequestTableModel.this.updateStatus();
               }
            });
         } else {
            BoundedRangeModel var1 = this.status.getProgressModel();
            var1.setMinimum(0);
            var1.setMaximum(this.maxRunning);
            if (this.currentTasks.size() > this.maxRunning) {
               var1.setValue(this.maxRunning);
            } else {
               var1.setValue(this.currentTasks.size());
            }

         }
      }
   }

   public void setMaximumRunning(int var1) {
      this.maxRunning = var1;
   }

   static {
      HashSet var0 = new HashSet();
      var0.add(RequestStatus.ALLOWED);
      ORDER.add(var0);
      var0 = new HashSet();
      var0.add(RequestStatus.PENDING);
      ORDER.add(var0);
      var0 = new HashSet();
      var0.add(RequestStatus.CANCELLED);
      var0.add(RequestStatus.DENIED);
      var0.add(RequestStatus.EXECUTED);
      ORDER.add(var0);
   }

   private class DeniedRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public DeniedRequest(RequestedOperation var2) {
         super(null);
         this.op = var2;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("Denied request: %d\n", this.op.getId()));
      }
   }

   private class ExecutedRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public ExecutedRequest(RequestedOperation var2) {
         super(null);
         this.op = var2;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("Executed request: %d\n", this.op.getId()));
      }
   }

   private class EnabledRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public EnabledRequest(RequestedOperation var2) {
         super(null);
         this.op = var2;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("Enabled request: %d\n", this.op.getId()));
      }
   }

   private class CancelledRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public CancelledRequest(RequestedOperation var2) {
         super(null);
         this.op = var2;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("Cancelled request: %d\n", this.op.getId()));
      }
   }

   private class NewRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public NewRequest(RequestedOperation var2) {
         super(null);
         this.op = var2;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("New Request:\n", this.op.getId()));
         Set var2 = this.op.getDataKeys();
         int var3 = 10;

         String var5;
         for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = Math.max(var3, var5.length())) {
            var5 = (String)var4.next();
         }

         String var7 = String.format("%%%ds : %%s\n", var3 + 4);
         var1.write(String.format(var7, "Id", this.op.getId().toString()));
         var1.write(String.format(var7, "Key", this.op.getKey()));
         Iterator var8 = var2.iterator();

         while(var8.hasNext()) {
            String var6 = (String)var8.next();
            var1.write(String.format(var7, var6, this.op.getData(var6)));
         }

         var1.write(String.format(var7, "Source", this.op.getSource()));
         var1.write(String.format(var7, "Display", DisplayTransformer.INSTANCE.transform(this.op)));
         var1.write(String.format(var7, "Command", CommandTransformer.INSTANCE.transform(this.op)));
      }
   }

   private class StartRequest extends RequestTableModel.RecordAction {
      RequestedOperation op;

      public StartRequest(RequestedOperation op) {
         super(null);
         this.op = op;
      }

      @Override
      protected void recordItem(FileWriter var1) throws IOException {
         var1.write(String.format("Start request: %d\n", this.op.getId()));
      }
   }

   private abstract class RecordAction implements Runnable {
      private RecordAction() {
      }

      @Override
      public final void run() {
         try {
            FileWriter var1 = new FileWriter(RequestTableModel.this.logFile, true);

            try {
               this.recordItem(var1);
            } finally {
               var1.close();
            }
         } catch (Exception var6) {
         }

      }

      protected abstract void recordItem(FileWriter var1) throws IOException;

      // $FF: synthetic method
      RecordAction(Object var2) {
         this();
      }
   }

   public class RecordEntry implements Comparable<RequestTableModel.RecordEntry> {
      public Boolean allow;
      public RequestedOperation operation;
      public RequestStatus status;
      public String source;
      private Calendar created;

      public RecordEntry() {
         this.allow = Boolean.FALSE;
         this.status = RequestStatus.PENDING;
         this.source = "";
         this.created = null;
         this.created = Calendar.getInstance();
      }

      @Override
      public int compareTo(RequestTableModel.RecordEntry recordEntry) {
         if (recordEntry.status != this.status) {
            Iterator iterator = RequestTableModel.ORDER.iterator();

            while(iterator.hasNext()) {
               Set set = (Set)iterator.next();
               if (set.contains(this.status) && !set.contains(recordEntry.status)) {
                  return -1;
               }

               if (!set.contains(this.status) && set.contains(recordEntry.status)) {
                  return 1;
               }
            }
         }

         return this.created.compareTo(recordEntry.created);
      }
   }
}
