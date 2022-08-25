package ddb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Factory {
   protected Factory() {
   }

   protected static Object newObject(String classname, Class<?>[] var1, Object... var2) {
      if (classname != null && var1 != null) {
         try {
            Class clazz = Factory.class.getClassLoader().loadClass(classname);
            if (clazz == null) {
               return null;
            } else {
               Constructor constructor = clazz.getDeclaredConstructor(var1);
               if (constructor == null) {
                  return null;
               } else {
                  constructor.setAccessible(true);
                  return constructor.newInstance(var2);
               }
            }
         } catch (Exception e) {
            System.err.println("Failed to create new " + classname);
            Logger.getLogger("ds.core").log(Level.SEVERE, (String)null, e);
            e.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public static void main(String[] args) throws Exception {
      Class clazz = Class.forName("ds.plugin.live.DSClientApp");
      Method method = clazz.getMethod("main", String[].class);
      method.invoke(null, args);
   }
}
