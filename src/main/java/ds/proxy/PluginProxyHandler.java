package ds.proxy;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;
import ddb.util.proxy.DszProxyHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class PluginProxyHandler<DS extends Plugin> extends DszProxyHandler<DS> implements InvocationHandler {
   DS plugin;
   CoreController core;

   public static <DS extends Plugin> DS newInstance(DS var0, CoreController var1) {
      return (DS) DszProxyHandler.newInstance(var0, new PluginProxyHandler(var0, var1), new Class[0]);
   }

   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      try {
         try {
            Object var4 = var2.invoke(this.plugin, var3);
            return var4;
         } catch (InvocationTargetException var9) {
            this.core.logEvent(Level.SEVERE, var9.getMessage(), var9.getTargetException());
         } catch (Exception var10) {
            this.core.logEvent(Level.SEVERE, var10.getMessage(), var10);
         }

         return null;
      } finally {
         ;
      }
   }

   public PluginProxyHandler(DS var1, CoreController var2) {
      super(var1);
      this.plugin = var1;
      this.core = var2;
   }
}
