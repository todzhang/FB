package ddb.util;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OrderedFutureTask<V> extends FutureTask<V> {
   public static final int MAX_PRIORITY = Integer.MAX_VALUE;
   public static final int MID_PRIORITY = 0;
   public static final int MIN_PRIORITY = Integer.MIN_VALUE;
   public static final Comparator<Runnable> COMPARE = (o1, o2) -> {
      if (o1 instanceof OrderedFutureTask && o2 instanceof OrderedFutureTask) {
         OrderedFutureTask<?> oft1 = (OrderedFutureTask)o1;
         OrderedFutureTask<?> oft2 = (OrderedFutureTask)o2;
         if (oft1.getPriority() > oft2.getPriority()) {
            return -1;
         } else {
            return oft1.getPriority() < oft2.getPriority() ? 1 : 0;
         }
      } else if (o1 instanceof OrderedFutureTask) {
         return -1;
      } else {
         return o2 instanceof OrderedFutureTask ? 1 : 0;
      }
   };
   int priority = 0;

   public static FutureTask<Void> decorate(Runnable r) {
      return new OrderedFutureTask(r, (Object)null, 0);
   }

   public static <V> FutureTask<V> decorate(Runnable r, V retVal, int priority) {
      return new OrderedFutureTask(r, retVal, priority);
   }

   public static FutureTask<Void> decorate(Runnable r, int priority) {
      return new OrderedFutureTask(r, (Object)null, priority);
   }

   public static <V> FutureTask<V> decorate(Callable<V> c) {
      return new OrderedFutureTask(c, 0);
   }

   public static <V> FutureTask<V> decorate(Callable<V> c, int priority) {
      return new OrderedFutureTask(c, priority);
   }

   public static ExecutorService createPriorityExecutorService() {
      return createPriorityExecutorService(5, 15);
   }

   public static ExecutorService createPriorityExecutorService(String threadFactoryName) {
      return createPriorityExecutorService(5, 15, threadFactoryName);
   }

   public static ExecutorService createPriorityExecutorService(int minThreads, int maxThreads) {
      return createPriorityExecutorService(5, 15, "OrderedFutureTask Priority Queue");
   }

   public static ExecutorService createPriorityExecutorService(int minThreads, int maxThreads, String threadFactoryName) {
      BlockingQueue<Runnable> TranslationQueue = new PriorityBlockingQueue(100, COMPARE);
      return new ThreadPoolExecutor(minThreads, maxThreads, 10L, TimeUnit.SECONDS, TranslationQueue, GeneralUtilities.createThreadFactory(threadFactoryName));
   }

   private OrderedFutureTask(Callable<V> c, int priority) {
      super(c);
      this.priority = priority;
   }

   private OrderedFutureTask(Runnable v, V r, int priority) {
      super(v, r);
      this.priority = priority;
   }

   public int getPriority() {
      return this.priority;
   }
}
