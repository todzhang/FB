package ddb.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;

public class BasicProxy implements InvocationHandler {
   protected final Object obj;

   public static <T> T newInstance(T var0) {
      BasicProxy var1 = new BasicProxy(var0);
      Class var2 = var0.getClass();
      HashSet var3 = new HashSet();

      for(Class var4 = var2; var4 != Object.class; var4 = var4.getSuperclass()) {
         var3.addAll(Arrays.asList(var4.getInterfaces()));
      }

      Class[] var5 = (Class[])var3.toArray(new Class[var3.size()]);
      Object var6 = Proxy.newProxyInstance(var2.getClassLoader(), var5, var1);
      return (T) var6;
   }

   protected BasicProxy(Object var1) {
      this.obj = var1;
   }

   public final Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      try {
         this.beforeMethod(var2, var3);
         Object var4 = this.invokeMethod(var2, var3);
         return var4;
      } catch (InvocationTargetException var9) {
         if (!this.handleException(var9.getTargetException())) {
            throw var9.getTargetException();
         }
      } catch (Exception var10) {
         if (!this.handleException(var10)) {
            throw new RuntimeException("unexpected invocation exception: " + var10.getMessage());
         }
      } finally {
         this.afterMethod(var2, var3);
      }

      return null;
   }

   protected boolean handleException(Throwable var1) {
      return false;
   }

   protected void beforeMethod(Method var1, Object[] var2) {
   }

   protected void afterMethod(Method var1, Object[] var2) {
   }

   protected Object invokeMethod(Method var1, Object[] var2) throws InvocationTargetException, Exception {
      return var1.invoke(this.obj, var2);
   }
}
