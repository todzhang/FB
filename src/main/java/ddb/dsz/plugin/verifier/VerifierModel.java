package ddb.dsz.plugin.verifier;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.GeneralUtilities;
import ddb.util.AbstractEnumeratedTableModel.FireTableDataChanged;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsInserted;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsUpdated;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.table.TableModel;

public class VerifierModel extends AbstractEnumeratedTableModel<VerifierColumn> implements TableModel {
   final ScheduledExecutorService exec = Executors.newScheduledThreadPool(10, GeneralUtilities.createThreadFactory(this.getClass().getSimpleName()));
   List<VerifierModel.TaskEntry> entries;
   CoreController core;
   List<Operation> guids;
   boolean stopped = false;
   int maximumLength = 10000000;
   Set<Task> handledTasks = new HashSet();
   Verifier verifier;

   public void setMaximumLength(int var1) {
      if (var1 >= 0) {
         this.maximumLength = var1;
      }

   }

   public VerifierModel(Verifier var1, Collection<Task> var2, CoreController var3) {
      super(VerifierColumn.class);
      this.verifier = var1;
      this.entries = new Vector();
      this.core = var3;
      this.guids = new Vector();
   }

   public void addTask(Task var1) {
      if (!this.stopped) {
         if (this.handledTasks.add(var1)) {
            boolean var2 = true;
            int var7;
            synchronized(this) {
               if (this.stopped) {
                  return;
               }

               var7 = this.entries.size();
               VerifierModel.TaskEntry var4 = new VerifierModel.TaskEntry(var1, var7);
               this.entries.add(var7, var4);
            }

            EventQueue.invokeLater(new FireTableRowsInserted(var7, var7));
         }
      }
   }

   public void reverify(int var1) {
      if (!this.stopped) {
         synchronized(this) {
            VerifierModel.TaskEntry var3 = (VerifierModel.TaskEntry)this.entries.get(var1);
            var3.reverify();
         }

         EventQueue.invokeLater(new FireTableRowsUpdated(var1, var1));
      }
   }

   public void clear() {
      synchronized(this) {
         Iterator var2 = this.entries.iterator();

         while(true) {
            if (!var2.hasNext()) {
               this.entries.clear();
               this.stopped = true;
               break;
            }

            VerifierModel.TaskEntry var3 = (VerifierModel.TaskEntry)var2.next();
            var3.stop();
         }
      }

      this.exec.shutdownNow();
      EventQueue.invokeLater(new FireTableDataChanged());
   }

   @Override
   public Class<?> getColumnClass(VerifierColumn e) {
      return e.getClazz();
   }

   @Override
   public String getColumnName(VerifierColumn e) {
      return e.getName();
   }

   public int getRowCount() {
      return this.entries.size();
   }

   public Object getValueAt(int i, VerifierColumn e) {
      if (i >= 0 && i < this.entries.size()) {
         VerifierModel.TaskEntry var3 = (VerifierModel.TaskEntry)this.entries.get(i);
         synchronized(this) {
            switch(e) {
            case ID:
               return var3.task.getId().getId();
            case COMMAND:
               return var3.task.getCommandName();
            case FULLCOMMAND:
               return var3.task.getTypedCommand();
            case TASK_STATUS:
               return var3.task.getState();
            case VERIFY_STATUS:
               return var3.getState();
            case GUID:
               return var3.task.getId().getOperation();
            case OP:
               return var3.task.getId().getOperation();
            case OUTPUT:
               return var3.getOutput();
            default:
               return null;
            }
         }
      } else {
         return null;
      }
   }

   class TaskFileEntry implements Runnable {
      TaskDataAccess data;
      VerifierState state;
      String output;
      VerifierModel.TaskEntry parent;

      public TaskFileEntry(TaskDataAccess var2, VerifierModel.TaskEntry var3) {
         this.data = var2;
         this.parent = var3;
         this.state = VerifierState.NotVerified;
         this.output = "";
      }

      public void run() {
         if (this.parent.hasEntry(this)) {
            if (VerifierModel.this.verifier.isSuspended()) {
               try {
                  VerifierModel.this.exec.schedule(this, 1L, TimeUnit.MINUTES);
               } catch (Throwable var2) {
               }

            } else {
               this.state = VerifierState.Verifying;
               this.output = "";
               VerificationDelegate var1 = new VerificationDelegate(this.parent.task, this.data, VerifierModel.this.core);
               EventQueue.invokeLater(new FireTableRowsUpdated( this.parent.index, this.parent.index));
               var1.verify();
               if (var1.isValid()) {
                  this.state = VerifierState.VerifySuccess;
               } else {
                  this.state = VerifierState.VerifyFailure;
                  this.output = var1.getDetails();
               }

               this.parent.setState(this.state, this);
               EventQueue.invokeLater(new FireTableRowsUpdated(this.parent.index, this.parent.index));
            }
         }
      }
   }

   class TaskEntry implements Observer {
      Task task;
      Set<VerifierModel.TaskFileEntry> files;
      int index;
      VerifierState defaultState;

      public TaskEntry(Task var2, int var3) {
         this.task = var2;
         this.files = new HashSet();
         this.index = var3;
         this.defaultState = VerifierState.NotVerified;
         var2.subscribe(this, true);
      }

      public void update(Observable var1, Object var2) {
         EventQueue.invokeLater(new FireTableRowsUpdated(this.index, this.index));
         if (var2 instanceof TaskDataAccess) {
            TaskDataAccess var3 = (TaskDataAccess)var2;
            if (var3.getType() != DataType.LOG) {
               VerifierModel.TaskFileEntry var4 = null;
               if (var3.getType() == DataType.DATA) {
                  var4 = VerifierModel.this.new TaskFileEntry(var3, this);
               } else if ((var3.getType() == DataType.STATE || var3.getType() == DataType.TASKING) && !this.task.isAlive()) {
                  var4 = VerifierModel.this.new TaskFileEntry(this.task.getTaskingAccess(), this);
               }

               if (var4 != null) {
                  synchronized(this) {
                     this.files.add(var4);
                  }

                  VerifierModel.this.exec.schedule(var4, 10L, TimeUnit.SECONDS);
               }

            }
         }
      }

      public void reverify() {
         if (this.task != null) {
            this.task.unsubscribe(this);
            synchronized(this) {
               this.files.clear();
               this.defaultState = VerifierState.NotVerified;
            }

            this.task.subscribe(this, true);
         }
      }

      public void setState(VerifierState var1, VerifierModel.TaskFileEntry var2) {
         if (this.task != null) {
            if (VerifierState.VerifySuccess == var1) {
               synchronized(this) {
                  this.defaultState = VerifierState.VerifySuccess;
                  this.files.remove(var2);
               }
            }

         }
      }

      public VerifierState getState() {
         if (this.task == null) {
            return VerifierState.NoLog;
         } else {
            synchronized(this) {
               if (this.files.size() == 0) {
                  if (this.task.isAlive()) {
                     return VerifierState.NotVerified;
                  } else {
                     return this.task.hasTaskingInformation() ? this.defaultState : VerifierState.NoLog;
                  }
               } else {
                  VerifierState var2 = VerifierState.VerifySuccess;
                  Iterator var3 = this.files.iterator();

                  while(var3.hasNext()) {
                     VerifierModel.TaskFileEntry var4 = (VerifierModel.TaskFileEntry)var3.next();
                     switch(var4.state) {
                     case NoLog:
                        return VerifierState.NoLog;
                     case NotVerified:
                        if (var2 == VerifierState.VerifySuccess) {
                           var2 = VerifierState.NotVerified;
                        }
                        break;
                     case Verifying:
                        var2 = VerifierState.Verifying;
                        break;
                     case VerifyFailure:
                        return VerifierState.VerifyFailure;
                     }
                  }

                  return var2;
               }
            }
         }
      }

      public synchronized void stop() {
         this.task.unsubscribe(this);
         this.files.clear();
         this.task = null;
      }

      public synchronized boolean hasEntry(VerifierModel.TaskFileEntry var1) {
         return this.files.contains(var1);
      }

      public String getOutput() {
         if (this.task == null) {
            return "";
         } else {
            StringBuilder var1 = new StringBuilder();
            synchronized(this) {
               Iterator var3 = this.files.iterator();

               while(var3.hasNext()) {
                  VerifierModel.TaskFileEntry var4 = (VerifierModel.TaskFileEntry)var3.next();
                  if (var4.output != null && var4.output.length() != 0) {
                     var1.append(String.format("%s\n", var4.output));
                  }
               }

               return var1.toString();
            }
         }
      }
   }
}
