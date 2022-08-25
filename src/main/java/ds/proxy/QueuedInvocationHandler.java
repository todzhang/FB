package ds.proxy;

import ddb.dsz.annotations.DszQueuableMethod;
import ddb.util.UtilityConstants;
import ddb.util.proxy.DszProxyHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueuedInvocationHandler<O> extends DszProxyHandler<O> implements InvocationHandler, Runnable {
   final ExecutorService exec;
   Logger log;

   public static <O> O newInstance(O clazz) {
      return newInstance(clazz, Logger.getLogger("dsz.core"));
   }

   public static <O> O newInstance(O clazz, Logger logger) {
      return newInstance(clazz, logger, null);
   }

   public static <O> O newInstance(O clazz, Class<?>... var1) {
      return newInstance(clazz, Logger.getLogger("dsz.core"), var1);
   }

   public static <O> O newInstance(O clazz, Logger logger, Class<?>... args) {
      return (O) DszProxyHandler.newInstance(clazz, new QueuedInvocationHandler(clazz, logger), args);
   }

   public QueuedInvocationHandler(O clazz, Logger logger) {
      super(clazz);
      this.log = logger;
      this.exec = UtilityConstants.createSingleThreadExecutorService(String.format("QueuedInvocationHandler: %s", clazz));
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         try {
            Object var4;
            if (method.getAnnotation(DszQueuableMethod.class) != null) {
               this.exec.submit(new QueuedInvocationHandler.QueuedObject(this.object, method, args));
               var4 = null;
               return var4;
            }

            var4 = method.invoke(this.object, args);
            return var4;
         } catch (RejectedExecutionException var10) {
         } catch (InvocationTargetException var11) {
            this.log.log(Level.SEVERE, var11.getMessage(), var11.getTargetException());
         } catch (Exception var12) {
            this.log.log(Level.SEVERE, var12.getMessage(), var12);
         }

         return null;
      } finally {
         ;
      }
   }

   @Override
   public void run() {
   }

   @Override
   public void stop() {
      this.exec.shutdown();
   }

   class QueuedObject implements Runnable {
      Object obj;
      Method m;
      Object[] args;

      public QueuedObject(Object obj, Method method, Object[] args) {
         this.obj = obj;
         this.m = method;
         this.args = args;
      }

      @Override
      public void run() {
         try {
            this.m.invoke(this.obj, this.args);
         } catch (InvocationTargetException invocationTargetException) {
            QueuedInvocationHandler.this.log.log(Level.SEVERE, invocationTargetException.getMessage(), invocationTargetException.getTargetException());
         } catch (Exception e) {
            QueuedInvocationHandler.this.log.log(Level.SEVERE, e.getMessage(), e);
         }

      }
   }
}
