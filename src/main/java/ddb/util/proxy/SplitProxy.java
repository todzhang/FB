package ddb.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplitProxy implements InvocationHandler {
   Object[] objects;

   public static <T> T newInstance(Class<T> clazz, Object... objects) {
      try {
         SplitProxy handler = new SplitProxy(objects);
         Class<?> proxyClass = Proxy.getProxyClass(clazz.getClassLoader(), clazz);
         return (T) proxyClass.getConstructor(InvocationHandler.class).newInstance(handler);
      } catch (Throwable var4) {
         Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, var4);
         return null;
      }
   }

   private SplitProxy(Object... objects) {
      this.objects = objects;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (this.objects == null) {
         return null;
      } else {
         Object retVal = null;
         Object[] arr$ = this.objects;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];

            try {
               retVal = method.invoke(o, args);
            } catch (Throwable var10) {
               Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, var10);
            }
         }

         return retVal;
      }
   }
}
