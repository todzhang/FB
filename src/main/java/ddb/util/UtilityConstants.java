package ddb.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public abstract class UtilityConstants {
   private static final Map<String, ThreadFactory> NameMap = new HashMap();

   public static Lock createLock() {
      return createLock(false);
   }

   public static Lock createLock(boolean var0) {
      return var0 ? new ReentrantLock(true) : new ReentrantLock();
   }

   public static ReadWriteLock createReadWriteLock() {
      return createReadWriteLock(false);
   }

   public static ReadWriteLock createReadWriteLock(boolean var0) {
      return var0 ? new ReentrantReadWriteLock(true) : new ReentrantReadWriteLock();
   }

   public static ThreadFactory createThreadFactory() {
      return createThreadFactory("UtilityConstants");
   }

   public static ThreadFactory createThreadFactory(final String var0) {
      synchronized(NameMap) {
         ThreadFactory var2 = (ThreadFactory)NameMap.get(var0);
         if (var2 == null) {
            var2 = new ThreadFactory() {
               int count = 0;

               @Override
               public Thread newThread(Runnable var1) {
                  Thread var2 = new Thread(var1, String.format("%s %d", var0, ++this.count));
                  var2.setPriority(1);
                  var2.setDaemon(true);
                  return var2;
               }
            };
            NameMap.put(var0, var2);
         }

         return var2;
      }
   }

   public static ExecutorService createExecutorService() {
      return createExecutorService("UtilityConstants");
   }

   public static ExecutorService createExecutorService(String var0) {
      ThreadPoolExecutor var1 = new ThreadPoolExecutor(1, 50, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(), createThreadFactory(var0));
      return var1;
   }

   public static ScheduledExecutorService createScheduledExecutorService() {
      return createScheduledExecutorService("UtilityConstants");
   }

   public static ScheduledExecutorService createScheduledExecutorService(String var0) {
      return createScheduledExecutorService(1, (String)var0);
   }

   public static ScheduledExecutorService createScheduledExecutorService(ThreadFactory var0) {
      return createScheduledExecutorService(1, (ThreadFactory)var0);
   }

   public static ScheduledExecutorService createScheduledExecutorService(int var0, ThreadFactory var1) {
      return Executors.newScheduledThreadPool(var0, var1);
   }

   public static ScheduledExecutorService createScheduledExecutorService(int corePoolSize, String var1) {
      return Executors.newScheduledThreadPool(corePoolSize, createThreadFactory(var1));
   }

   public static ExecutorService createSingleThreadExecutorService() {
      return createSingleThreadExecutorService("UtilityConstants");
   }

   public static ExecutorService createSingleThreadExecutorService(String var0) {
      return Executors.newSingleThreadExecutor(createThreadFactory(var0));
   }

   public static <O> O ShallowCopy(O var0, O var1, Class<?> var2) {
      if (var0 != null && var1 != null && var2 != null) {
         Field[] var3 = var2.getDeclaredFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field var6 = var3[var5];
            boolean var7 = var6.isAccessible();
            var6.setAccessible(true);

            try {
               var6.set(var0, var6.get(var1));
            } catch (Exception var9) {
               Logger.getLogger("dsz.core").log(Level.SEVERE, (String)null, var9);
            }

            var6.setAccessible(var7);
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static <E> JAXBElement<E> createRootElement(String var0, E var1) {
      return var0 != null && var1 != null ? new JAXBElement(new QName(var0), var1.getClass(), var1) : null;
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var4 = var2.getMethod("main", var0.getClass());
      var4.invoke((Object)null, var0);
   }
}
