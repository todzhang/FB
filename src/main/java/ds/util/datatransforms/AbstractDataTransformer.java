package ds.util.datatransforms;

import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.util.UtilityConstants;
import ddb.util.XmlCache;
import ds.core.impl.task.DocumentBlobAccess;
import ds.core.impl.task.TaskStateAccess;
import java.io.Reader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class AbstractDataTransformer extends DataTransformer implements Observer, Runnable {
   protected static final int MAX_GENERATED = 5;
   protected static final Comparator<TaskDataAccess> TASK_DATA_SORTER = new Comparator<TaskDataAccess>() {
      public int compare(TaskDataAccess var1, TaskDataAccess var2) {
         if (var1.isGenerated()) {
            if (var2.isGenerated()) {
               if (var1.getSize() > var2.getSize()) {
                  return -1;
               } else {
                  return var1.getSize() == var2.getSize() ? 0 : 1;
               }
            } else {
               return -1;
            }
         } else if (var2.isGenerated()) {
            return 1;
         } else if (var1 instanceof TaskStateAccess) {
            return var2 instanceof TaskStateAccess ? 0 : -1;
         } else if (var2 instanceof TaskStateAccess) {
            return 1;
         } else if (var1.getOrdinal() < var2.getOrdinal()) {
            return -1;
         } else {
            return var1.getOrdinal() > var2.getOrdinal() ? 1 : var1.getTask().getCreated().compareTo(var2.getTask().getCreated());
         }
      }
   };
   private static final Object GENERATED_ACCESS_LOCK = new Object();
   private static int generatedAccessCount = 0;
   protected boolean stop = false;
   private final List<Task> subscribedTasks2 = new Vector();
   private Queue<TaskDataAccess> dataEvents;
   private Set<TaskDataAccess> pendingEvents;
   protected final Object TASK_LIST_LOCK = new Object();
   protected final Object DATA_LIST_LOCK = new Object();
   protected final boolean orderImportant;
   private ScheduledExecutorService DEQUEUER;
   protected Logger logger = Logger.getLogger("ds.core");

   private static String getName() {
      StackTraceElement[] var0 = Thread.currentThread().getStackTrace();
      if (var0 != null) {
         StackTraceElement[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            StackTraceElement var4 = var1[var3];
            if (var4 != null && var4.getClassName() != null && var4.getClassName().startsWith("ddb.dsz") && !var4.getClassName().contains("DataTransformer")) {
               return var4.getClassName();
            }
         }
      }

      return "Unknown Parent";
   }

   protected AbstractDataTransformer() {
      this.orderImportant = false;
      this.init();
   }

   protected AbstractDataTransformer(boolean var1) {
      this.orderImportant = var1;
      this.init();
   }

   protected AbstractDataTransformer(String var1) {
      this.orderImportant = false;
      this.init();
   }

   protected AbstractDataTransformer(String var1, boolean var2) {
      this.orderImportant = var2;
      this.init();
   }

   protected AbstractDataTransformer(ThreadFactory var1) {
      this.orderImportant = false;
      this.init();
   }

   protected AbstractDataTransformer(ThreadFactory var1, boolean var2) {
      this.orderImportant = var2;
      this.init();
   }

   private void init() {
      this.init("ADT:  " + getName());
   }

   private void init(String var1) {
      this.init(UtilityConstants.createThreadFactory(var1));
   }

   private void init(ThreadFactory var1) {
      if (this.orderImportant) {
         this.dataEvents = new LinkedList();
      } else {
         this.dataEvents = new PriorityBlockingQueue(50, TASK_DATA_SORTER);
      }

      this.pendingEvents = new HashSet();
      this.DEQUEUER = Executors.newSingleThreadScheduledExecutor(var1);
      this.DEQUEUER.submit(this);
   }

   public void addTask(Task var1) {
      if (var1 != null) {
         synchronized(this.TASK_LIST_LOCK) {
            int var3 = Collections.binarySearch(this.subscribedTasks2, var1, Task.TaskComparator);
            if (var3 >= 0) {
               return;
            }

            ++var3;
            var3 = -var3;
            this.subscribedTasks2.add(var3, var1);
         }

         var1.subscribe(this, true);
      }
   }

   public void removeAllTasks() {
      Task[] var1 = null;
      synchronized(this.TASK_LIST_LOCK) {
         var1 = (Task[])this.subscribedTasks2.toArray(new Task[this.subscribedTasks2.size()]);
         this.subscribedTasks2.clear();
      }

      Task[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Task var5 = var2[var4];
         var5.unsubscribe(this);
      }

      synchronized(this.DATA_LIST_LOCK) {
         this.dataEvents.clear();
      }
   }

   public void removeTask(Task var1) {
      synchronized(this.TASK_LIST_LOCK) {
         int var3 = Collections.binarySearch(this.subscribedTasks2, var1, Task.TaskComparator);
         if (var3 < 0) {
            return;
         }

         this.subscribedTasks2.remove(var3);
      }

      var1.unsubscribe(this);
      Vector var2;
      synchronized(this.DATA_LIST_LOCK) {
         var2 = new Vector();
         var2.addAll(this.dataEvents);
         this.dataEvents.clear();
      }

      Iterator var14 = var2.iterator();

      while(var14.hasNext()) {
         TaskDataAccess var4 = (TaskDataAccess)var14.next();
         if (var4.getTask().equals(var1)) {
            if (var4.isGenerated()) {
               synchronized(GENERATED_ACCESS_LOCK) {
                  --generatedAccessCount;
               }

               synchronized(this.DATA_LIST_LOCK) {
                  this.DATA_LIST_LOCK.notify();
               }
            }
         } else {
            synchronized(this.DATA_LIST_LOCK) {
               this.dataEvents.offer(var4);
            }
         }
      }

   }

   public void run() {
      boolean var1 = false;

      try {
         TaskDataAccess var2 = null;
         boolean var3 = false;

         int var48;
         try {
            synchronized(this.DATA_LIST_LOCK) {
               TaskDataAccess[] var5 = (TaskDataAccess[])this.pendingEvents.toArray(new TaskDataAccess[0]);
               int var6 = var5.length;
               int var7 = 0;

               while(true) {
                  if (var7 >= var6) {
                     var2 = (TaskDataAccess)this.dataEvents.poll();
                     var48 = this.dataEvents.size();
                     var1 = this.pendingEvents.isEmpty();
                     break;
                  }

                  TaskDataAccess var8 = var5[var7];
                  if (!var8.getTask().isAlive()) {
                     this.pendingEvents.remove(var8);
                     this.dataEvents.add(var8);
                  }

                  ++var7;
               }
            }
         } finally {
            if (!this.orderImportant) {
               this.reschedule(var2 != null);
            }

         }

         try {
            if (var2 != null) {
               if (!this.orderImportant && var2.getType() == ddb.dsz.core.task.TaskDataAccess.DataType.TASKING && var2.getTask().isAlive()) {
                  synchronized(this.DATA_LIST_LOCK) {
                     this.pendingEvents.add(var2);
                  }

                  if (var48 > 0) {
                     this.sleep();
                  }

                  return;
               }

               try {
                  this.logger.log(Level.FINE, String.format("%d:  Handling #%d => %s", var2.getTask().getId().getId(), var2.getOrdinal(), var2.toString()));
               } catch (NullPointerException var42) {
                  this.logger.log(Level.FINE, String.format("??:  Handling #?? => %s", var2));
               }

               if (var2.isGenerated()) {
                  synchronized(GENERATED_ACCESS_LOCK) {
                     --generatedAccessCount;
                  }

                  synchronized(this.DATA_LIST_LOCK) {
                     this.DATA_LIST_LOCK.notify();
                  }
               }

               try {
                  this.execute(var2);
               } catch (Exception var38) {
                  this.handleError(var38);
               } catch (Throwable var39) {
                  var39.printStackTrace();
               }

               return;
            }
         } finally {
            if (this.orderImportant) {
               this.reschedule(!var1);
            }

         }

         return;
      } catch (RejectedExecutionException var46) {
         var46.printStackTrace();
      } catch (Throwable var47) {
         var47.printStackTrace();
      }

   }

   private void reschedule(boolean var1) {
      try {
         if (var1) {
            this.DEQUEUER.submit(this);
         } else {
            this.DEQUEUER.schedule(this, 250L, TimeUnit.MILLISECONDS);
         }
      } catch (RejectedExecutionException var3) {
         System.err.println("Rescheduling action rejected: " + getName());
      }

   }

   public void update(Observable var1, Object var2) {
      TaskDataAccess var3 = (TaskDataAccess)var2;
      if (var3 != null) {
         try {
            this.logger.log(Level.FINE, String.format("%d:  Enqueing #d => %s", var3.getTask().getId().getId(), var3.getOrdinal(), var3.toString()));
         } catch (NullPointerException var7) {
            this.logger.log(Level.FINE, String.format("??:  Enqueing #?? => %s", var3));
         }

         synchronized(this.DATA_LIST_LOCK) {
            this.dataEvents.offer(var3);
         }
      }

   }

   void requeue(TaskDataAccess var1) {
      try {
         this.logger.log(Level.FINE, String.format("%d:  Requeuing #d => %s", var1.getTask().getId().getId(), var1.getOrdinal(), var1.toString()));
      } catch (NullPointerException var13) {
         this.logger.log(Level.FINE, String.format("??:  Requeuing %s", var1));
      }

      if (this.isOrderImportant()) {
         this.execute(var1);
      } else {
         if (var1 == null) {
            return;
         }

         if (!this.isValidTask(var1.getTask())) {
            return;
         }

         if (var1.isGenerated()) {
            while(true) {
               synchronized(GENERATED_ACCESS_LOCK) {
                  if (generatedAccessCount < 5) {
                     ++generatedAccessCount;
                     break;
                  }
               }

               synchronized(this.DATA_LIST_LOCK) {
                  try {
                     this.DATA_LIST_LOCK.wait(1000L);
                  } catch (InterruptedException var11) {
                     System.err.println(var11.getMessage());
                  }
               }
            }

            if (!this.isValidTask(var1.getTask())) {
               synchronized(GENERATED_ACCESS_LOCK) {
                  --generatedAccessCount;
               }

               synchronized(this.DATA_LIST_LOCK) {
                  this.DATA_LIST_LOCK.notify();
                  return;
               }
            }
         }

         synchronized(this.DATA_LIST_LOCK) {
            this.dataEvents.offer(var1);
         }
      }

   }

   protected boolean isValidTask(Task var1) {
      synchronized(this.TASK_LIST_LOCK) {
         int var3 = Collections.binarySearch(this.subscribedTasks2, var1, Task.TaskComparator);
         return var3 >= 0;
      }
   }

   public void stop() {
      this.stop = true;
      this.removeAllTasks();
      this.DEQUEUER.shutdown();
   }

   private void sleep() {
      try {
         TimeUnit.MILLISECONDS.sleep(150L);
      } catch (Exception var2) {
      }

   }

   protected abstract void execute(TaskDataAccess var1);

   protected void handleError(Exception var1) {
   }

   public static final Document parseDocument(TaskDataAccess var0) {
      if (var0 instanceof DocumentBlobAccess) {
         return ((DocumentBlobAccess)DocumentBlobAccess.class.cast(var0)).getDocument();
      } else {
         DocumentBuilder var1 = XmlCache.getBuilder();

         try {
            if (var1 != null) {
               var1.reset();
               Reader var2 = var0.getReader();
               Document var3;
               if (var2 == null) {
                  var3 = null;
                  return var3;
               }

               try {
                  var3 = var1.parse(new InputSource(var2));
                  return var3;
               } finally {
                  var2.close();
               }
            }
         } catch (Exception var13) {
            throw new IncompleteDataException(var13);
         } finally {
            XmlCache.releaseBuilder(var1);
         }

         return null;
      }
   }

   protected final boolean isOrderImportant() {
      return this.orderImportant;
   }
}
