package ddb.util;

import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.ReferenceMap;

public abstract class JaxbCache {
   private static ClassLoader loader = ClassLoader.getSystemClassLoader();
   private static Map<String, JAXBContext> jaxbContextMap = LazyMap.decorate(new ReferenceMap(), new Transformer() {
      @Override
      public Object transform(Object arg0) {
         try {
            return JAXBContext.newInstance(arg0.toString(), JaxbCache.loader);
         } catch (JAXBException var3) {
            var3.printStackTrace();
            return null;
         }
      }
   });

   private static JAXBContext getContext(String jaxb) {
      try {
         return jaxbContextMap.get(jaxb);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static JAXBContext getContext(Class<?> c) {
      if (c == null) {
         throw new NullPointerException();
      } else {
         return getContext(c.getPackage().getName());
      }
   }

   public static void setClassLoader(ClassLoader loader) {
      JaxbCache.loader = loader;
   }
}
