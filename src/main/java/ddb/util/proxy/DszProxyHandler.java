package ddb.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

public abstract class DszProxyHandler<O> implements InvocationHandler {
   protected O object;

   public static <O> O newInstance(O item, DszProxyHandler<O> handler, Class<?>[] classes) {
      if (classes == null || classes.length == 0) {
         Class<?> clazz = item.getClass();
         List<Class<?>> interfaces = new Vector();
         Queue<Class<?>> currentInterface = new LinkedList();
         currentInterface.offer(clazz);

         label44:
         while(true) {
            Class clazz2;
            do {
               if (currentInterface.isEmpty()) {
                  classes = (Class[])interfaces.toArray(new Class[interfaces.size()]);
                  break label44;
               }

               clazz2 = (Class)currentInterface.poll();
            } while(clazz2 == null);

            if (clazz2.isInterface()) {
               if (!interfaces.contains(clazz2)) {
                  interfaces.add(clazz2);
               }
            } else {
               Class[] arr$ = clazz2.getInterfaces();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Class<?> c = arr$[i$];
                  if (!interfaces.contains(c)) {
                     interfaces.add(c);
                  }
               }
            }

            currentInterface.addAll(Arrays.<Class<?>>asList(clazz2.getInterfaces()));
            currentInterface.add(clazz2.getSuperclass());
         }
      }

      try {
         return (O) Proxy.newProxyInstance(item.getClass().getClassLoader(), classes, handler);
      } catch (Throwable var11) {
         return item;
      }
   }

   protected DszProxyHandler(O object) {
      this.object = object;
   }

   public static final Object Unwrap(Object obj) {
      return Unwrap(obj, true);
   }

   public static final Object Unwrap(Object obj, boolean recursive) {
      if (Proxy.isProxyClass(obj.getClass())) {
         Object o = Proxy.getInvocationHandler(obj);
         if (!recursive) {
            return ((DszProxyHandler)DszProxyHandler.class.cast(o)).object;
         }

         if (o instanceof DszProxyHandler) {
            return Unwrap(((DszProxyHandler)DszProxyHandler.class.cast(o)).object, recursive);
         }
      }

      return obj;
   }

   public static final Object GetHandler(Object obj) {
      return Proxy.isProxyClass(obj.getClass()) ? Proxy.getInvocationHandler(obj) : null;
   }

   public void stop() {
   }
}
