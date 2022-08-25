package ddb.util.jar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import sun.misc.URLClassPath;

public class JarFileClassInspector {
   File jarFile;

   public static final List<JarFileClassInspector> getInspectors(ClassLoader loader) {
      List<JarFileClassInspector> retVal = new ArrayList();
      if (loader == null) {
         return retVal;
      } else {
         if (loader.getParent() != null) {
            retVal.addAll(getInspectors(loader.getParent()));
         }

         try {
            if (loader instanceof URLClassLoader) {
               Field f = URLClassLoader.class.getDeclaredField("ucp");
               f.setAccessible(true);
               URLClassPath path = (URLClassPath)f.get(loader);
               URL[] arr$ = path.getURLs();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  URL url = arr$[i$];

                  try {
                     File file = new File(url.toURI());
                     if (file.exists() && file.isFile()) {
                        retVal.add(new JarFileClassInspector(file));
                     }
                  } catch (Exception var9) {
                     var9.printStackTrace();
                  }
               }
            } else {
               System.out.println("Unexpected: " + loader.getClass());
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         return retVal;
      }
   }

   @Override
   public String toString() {
      String path = this.jarFile.getAbsolutePath();

      try {
         path = this.jarFile.getCanonicalPath();
      } catch (Throwable var3) {
      }

      return "Jar Inspector: " + path;
   }

   public JarFileClassInspector(String filename) {
      this(new File(filename));
   }

   public JarFileClassInspector(File jarFile) {
      this.jarFile = jarFile;
   }

   public List<Class<?>> getAllClasses() throws IOException, ClassNotFoundException {
      List<Class<?>> v = new Vector();
      JarFile jf = new JarFile(this.jarFile);
      Enumeration e = jf.entries();

      while(e.hasMoreElements()) {
         JarEntry jarEntry = (JarEntry)e.nextElement();
         if (jarEntry.getName().endsWith(".class")) {
            String className = jarEntry.getName();
            className = className.replaceAll(".class", "");
            className = className.replaceAll("/", ".");

            try {
               Class<?> c = Class.forName(className, false, this.getClass().getClassLoader());
               v.add(c);
            } catch (Throwable var7) {
            }
         }
      }

      return v;
   }

   public <U> List<Class<? extends U>> getAllClassesExtendingClass(Class<U> c) throws IOException, ClassNotFoundException {
      return this.getAllClassesExtendingClass(c, false);
   }

   public <U> List<Class<? extends U>> getAllClassesExtendingClass(Class<U> c, boolean strict) throws IOException, ClassNotFoundException {
      return this.getAllClassesExtendingClass(c, strict, new Vector());
   }

   public <U> List<Class<? extends U>> getAllClassesExtendingClass(Class<U> c, boolean strict, List<Class<?>> exclusions) throws IOException, ClassNotFoundException {
      List<Class<?>> v = this.getAllClasses();
      List<Class<? extends U>> matches = new Vector();
      synchronized(v) {
         Iterator i$ = v.iterator();

         while(true) {
            Class jarClass;
            do {
               do {
                  do {
                     if (!i$.hasNext()) {
                        return matches;
                     }

                     jarClass = (Class)i$.next();
                  } while(!c.isAssignableFrom(jarClass));
               } while(exclusions.contains(jarClass));
            } while(strict && jarClass.equals(c));

            matches.add(jarClass);
         }
      }
   }
}
