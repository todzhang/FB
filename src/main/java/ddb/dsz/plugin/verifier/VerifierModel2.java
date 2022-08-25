package ddb.dsz.plugin.verifier;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.GeneralUtilities;
import ddb.util.AbstractEnumeratedTableModel.FireTableDataChanged;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsUpdated;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.table.TableModel;

public class VerifierModel2 extends AbstractEnumeratedTableModel<VerifierColumn> implements TableModel {
   private static final Comparator<VerifierModel2.TaskEntry> TaskCompare = new Comparator<VerifierModel2.TaskEntry>() {
      public int compare(VerifierModel2.TaskEntry var1, VerifierModel2.TaskEntry var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 != null && var2 == null) {
            return -1;
         } else {
            return var1 == null ? 1 : var1.task.getId().compareTo(var2.task.getId());
         }
      }
   };
   final ScheduledExecutorService exec = Executors.newScheduledThreadPool(10, GeneralUtilities.createThreadFactory(this.getClass().getSimpleName()));
   final List<VerifierModel2.TaskEntry> entries;
   final CoreController core;
   boolean stopped = false;
   int maximumLength = 10000000;
   Verifier verifier;
   private final Set<Integer> PENDING_CHANGES = new TreeSet();
   private boolean bScheduled = false;
   private boolean bRunAgain = false;
   private final Runnable ANNOUNCE_CHANGES = new Runnable() {
      public void run() {
         boolean var17 = false;

         label230: {
            label231: {
               try {
                  label232: {
                     var17 = true;
                     Vector var1 = null;
                     synchronized(VerifierModel2.this.PENDING_CHANGES) {
                        if (VerifierModel2.this.PENDING_CHANGES.size() == 0) {
                           var17 = false;
                           break label232;
                        }

                        var1 = new Vector(VerifierModel2.this.PENDING_CHANGES);
                        VerifierModel2.this.PENDING_CHANGES.clear();
                        VerifierModel2.this.bRunAgain = false;
                     }

                     if (var1.size() == 0) {
                        var17 = false;
                        break label230;
                     }

                     int var2 = -1;
                     int var3 = -1;
                     Iterator var4 = var1.iterator();

                     while(var4.hasNext()) {
                        Integer var5 = (Integer)var4.next();
                        if (var5 != null) {
                           if (var2 == -1) {
                              var2 = var5;
                           }

                           if (var3 == -1) {
                              var3 = var5;
                           } else if (var3 + 1 != var5) {
                              EventQueue.invokeLater(new FireTableRowsUpdated(var2, var3));
                              var2 = var3 = var5;
                           } else {
                              var3 = var5;
                           }
                        }
                     }

                     if (var2 != -1) {
                        if (var3 != -1) {
                           EventQueue.invokeLater(new FireTableRowsUpdated(var2, var3));
                           var17 = false;
                        } else {
                           var17 = false;
                        }
                     } else {
                        var17 = false;
                     }
                     break label231;
                  }
               } finally {
                  if (var17) {
                     synchronized(VerifierModel2.this.PENDING_CHANGES) {
                        if (VerifierModel2.this.bRunAgain) {
                           VerifierModel2.this.core.schedule(VerifierModel2.this.ANNOUNCE_CHANGES, 100L, TimeUnit.MILLISECONDS);
                        } else {
                           VerifierModel2.this.bScheduled = false;
                        }

                     }
                  }
               }

               synchronized(VerifierModel2.this.PENDING_CHANGES) {
                  if (VerifierModel2.this.bRunAgain) {
                     VerifierModel2.this.core.schedule(VerifierModel2.this.ANNOUNCE_CHANGES, 100L, TimeUnit.MILLISECONDS);
                  } else {
                     VerifierModel2.this.bScheduled = false;
                  }

                  return;
               }
            }

            synchronized(VerifierModel2.this.PENDING_CHANGES) {
               if (VerifierModel2.this.bRunAgain) {
                  VerifierModel2.this.core.schedule(VerifierModel2.this.ANNOUNCE_CHANGES, 100L, TimeUnit.MILLISECONDS);
               } else {
                  VerifierModel2.this.bScheduled = false;
               }

               return;
            }
         }

         synchronized(VerifierModel2.this.PENDING_CHANGES) {
            if (VerifierModel2.this.bRunAgain) {
               VerifierModel2.this.core.schedule(VerifierModel2.this.ANNOUNCE_CHANGES, 100L, TimeUnit.MILLISECONDS);
            } else {
               VerifierModel2.this.bScheduled = false;
            }

         }
      }
   };

   private final void QueueChanges(int var1) {
      synchronized(this.PENDING_CHANGES) {
         this.PENDING_CHANGES.add(var1);
         if (!this.bScheduled) {
            this.bScheduled = true;
            this.core.schedule(this.ANNOUNCE_CHANGES, 100L, TimeUnit.MILLISECONDS);
         } else {
            this.bRunAgain = true;
         }

      }
   }

   public void setMaximumLength(int var1) {
      if (var1 >= 0) {
         this.maximumLength = var1;
      }

   }

   public VerifierModel2(Verifier var1, Collection<Task> var2, CoreController var3) {
      super(VerifierColumn.class);
      this.verifier = var1;
      this.entries = new Vector();
      this.core = var3;
   }

   public void addTask(Task var1) {
      if (!this.stopped) {
         VerifierModel2.TaskEntry var2 = new VerifierModel2.TaskEntry(var1, false);
         int var3;
         synchronized(this.entries) {
            var3 = Collections.binarySearch(this.entries, var2, TaskCompare);
            if (var3 >= 0) {
               return;
            }

            var2 = new VerifierModel2.TaskEntry(var1, true);
            ++var3;
            var3 = 0 - var3;
            this.entries.add(var3, var2);
         }

         this.QueueChanges(var3);
      }
   }

   public void reverify(int var1) {
      if (!this.stopped) {
         synchronized(this.entries) {
            if (var1 < 0 || var1 >= this.entries.size()) {
               return;
            }

            VerifierModel2.TaskEntry var3 = (VerifierModel2.TaskEntry)this.entries.get(var1);
            if (var3 == null) {
               return;
            }

            var3.delete();
            VerifierModel2.TaskEntry var4 = new VerifierModel2.TaskEntry(var3.task, true);
            this.entries.set(var1, var4);
         }

         this.QueueChanges(var1);
      }
   }

   void TaskUpdated(Task var1) {
      int var2;
      synchronized(this.entries) {
         VerifierModel2.TaskEntry var4 = new VerifierModel2.TaskEntry(var1, false);
         var2 = Collections.binarySearch(this.entries, var4, TaskCompare);
         if (var2 < 0 || var2 >= this.entries.size()) {
            return;
         }
      }

      this.QueueChanges(var2);
   }

   public void clear() {
      synchronized(this.entries) {
         Iterator var2 = this.entries.iterator();

         while(true) {
            if (!var2.hasNext()) {
               this.entries.clear();
               this.stopped = true;
               break;
            }

            VerifierModel2.TaskEntry var3 = (VerifierModel2.TaskEntry)var2.next();
            var3.task.unsubscribe(var3);
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
      synchronized(this.entries) {
         return this.entries.size();
      }
   }

   public Object getValueAt(int i, VerifierColumn e) {
      if (i >= 0 && i < this.entries.size()) {
         VerifierModel2.TaskEntry var3 = null;
         synchronized(this.entries) {
            var3 = (VerifierModel2.TaskEntry)this.entries.get(i);
         }

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
      } else {
         return null;
      }
   }

   private class TaskFileCheck implements Runnable {
      final VerifierModel2.TaskEntry entry;
      final TaskDataAccess access;
      boolean firstTry = true;

      public TaskFileCheck(VerifierModel2.TaskEntry var2, TaskDataAccess var3) {
         this.entry = var2;
         this.access = var3;
      }

      public void run() {
         if (!this.entry.bDeleted) {
            if (VerifierModel2.this.verifier.isSuspended()) {
               try {
                  VerifierModel2.this.exec.schedule(this, 1L, TimeUnit.MINUTES);
               } catch (Exception var2) {
               }

            } else {
               VerificationDelegate var1 = new VerificationDelegate(this.entry.task, this.access, VerifierModel2.this.core);
               var1.verify();
               if (var1.isValid()) {
                  this.entry.addSuccessfullyVerified();
               } else {
                  if (this.firstTry && var1.shouldRepeat()) {
                     this.firstTry = false;
                     VerifierModel2.this.exec.schedule(this, 10L, TimeUnit.SECONDS);
                     return;
                  }

                  this.entry.addFailure(var1.getDetails());
               }

               if (!this.entry.bDeleted) {
                  VerifierModel2.this.TaskUpdated(this.entry.task);
               }

            }
         }
      }
   }

   private class TaskEntry implements Observer {
      final Task task;
      private boolean bFailure = false;
      private int fileCount = 0;
      private int fileVerified = 0;
      private List<String> outputs = new ArrayList();
      private boolean bDeleted = false;
      private boolean bTasking = false;

      public TaskEntry(Task var2, boolean var3) {
         this.task = var2;
         if (var3) {
            this.task.subscribe(this, true);
         } else {
            this.bDeleted = true;
         }

      }

      public void delete() {
         this.bDeleted = true;
      }

      public boolean isDeleted() {
         return this.bDeleted;
      }

      public void update(Observable var1, Object var2) {
         if (var2 != null && var2 instanceof TaskDataAccess) {
            TaskDataAccess var3 = (TaskDataAccess)var2;
            VerifierModel2.TaskFileCheck var4 = null;
            if (var3.getType() == DataType.DATA) {
               this.addFile();
               var4 = VerifierModel2.this.new TaskFileCheck(this, var3);
            } else if (!this.bTasking && (var3.getType() == DataType.STATE || var3.getType() == DataType.TASKING) && !var3.getTask().isAlive()) {
               this.bTasking = true;
               this.addFile();
               var4 = VerifierModel2.this.new TaskFileCheck(this, var3.getTask().getTaskingAccess());
            }

            if (var4 != null) {
               VerifierModel2.this.exec.submit(var4);
            }

         }
      }

      public synchronized String getOutput() {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.outputs.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.append(var3);
            var1.append("\n");
         }

         return var1.toString();
      }

      public synchronized VerifierState getState() {
         if (this.fileCount == 0 && this.task.isAlive()) {
            return VerifierState.NotVerified;
         } else if (this.bFailure) {
            return VerifierState.VerifyFailure;
         } else {
            return this.fileCount == this.fileVerified && !this.task.isAlive() ? VerifierState.VerifySuccess : VerifierState.Verifying;
         }
      }

      synchronized void addFile() {
         ++this.fileCount;
      }

      synchronized void addSuccessfullyVerified() {
         ++this.fileVerified;
      }

      synchronized void addFailure(String var1) {
         this.bFailure = true;
         if (this.outputs.isEmpty()) {
            this.outputs.add(var1);
         }

      }
   }
}
