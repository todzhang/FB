package ddb.writequeue;

import java.awt.EventQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.Closure;

public class WriteQueue<E extends Writable> implements Runnable {
   static ThreadFactory factory = new ThreadFactory() {
      int count = 0;

      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(var1, "WriteQueue thread: " + ++this.count);
         var2.setPriority(1);
         var2.setDaemon(true);
         return var2;
      }
   };
   final ScheduledExecutorService executor;
   final LinkedList<E> list;
   Closure onWrite;
   boolean onEventQueue;
   boolean paused;

   public WriteQueue(TimeUnit var1, long var2, Closure var4, boolean var5) {
      this.executor = Executors.newSingleThreadScheduledExecutor(factory);
      this.list = new LinkedList();
      this.onEventQueue = false;
      this.paused = false;
      this.onWrite = var4;
      this.onEventQueue = var5;
      this.executor.scheduleWithFixedDelay(this, 0L, var2, var1);
   }

   public WriteQueue(Closure var1, boolean var2) {
      this(TimeUnit.MILLISECONDS, 150L, var1, var2);
   }

   public void setOnEventQueue(boolean var1) {
      this.onEventQueue = var1;
   }

   public void run() {
      if (!this.paused) {
         Vector var1 = new Vector();
         synchronized(this.list) {
            var1.addAll(this.list);
            this.list.clear();
         }

         if (!var1.isEmpty()) {
            WriteQueue.WriteExec var2 = new WriteQueue.WriteExec(var1);
            if (this.onEventQueue) {
               EventQueue.invokeLater(var2);
            } else {
               var2.run();
            }

         }
      }
   }

   public void enqueue(E var1) {
      synchronized(this.list) {
         while(this.list.size() != 0) {
            if (var1.resets()) {
               this.list.clear();
               break;
            }

            Writable var3 = (Writable)this.list.removeLast();
            if (!var3.combine(var1)) {
               this.list.offer((E) var3);
               break;
            }

            var1 = (E) var3;
         }

         this.list.offer(var1);
      }
   }

   public void reque(E var1) {
      synchronized(this.list) {
         this.list.offerFirst(var1);
      }
   }

   public void stop() {
      this.executor.shutdown();
   }

   public void setPaused(boolean var1) {
      this.paused = var1;
   }

   public boolean isPaused() {
      return this.paused;
   }

   public class WriteExec implements Runnable {
      List<E> items;

      public WriteExec(List<E> var2) {
         this.items = var2;
      }

      public void run() {
         Iterator var1 = this.items.iterator();

         while(var1.hasNext()) {
            Writable var2 = (Writable)var1.next();
            WriteQueue.this.onWrite.execute(var2);
         }

      }
   }
}
