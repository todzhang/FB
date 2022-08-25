package ddb.dsz.plugin.filemanager.ver3.search;

import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.jaxb.mimetypes.MimeTypes;
import ddb.targetmodel.filemodel.jaxb.mimetypes.ObjectFactory;
import ddb.util.JaxbCache;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

public class MimeTypeMap {
   private static Map<String, MimeTypeMap.MimeType> nameToType;
   private static Map<String, MimeTypeMap.MimeType> suffixToType;

   private MimeTypeMap() {
   }

   public static void registerMimeType(String var0, String var1, List<String> var2) {
      registerMimeType(var0, var1, (String[])var2.toArray(new String[var2.size()]));
   }

   public static void registerMimeType(String var0, String var1, String... var2) {
      MimeTypeMap.MimeType var3 = new MimeTypeMap.MimeType(var0, var1, var2);
      nameToType.put(var0, var3);
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         suffixToType.put(var7.toLowerCase(), var3);
      }

   }

   public static MimeTypeMap.MimeType getMimeTypeForName(String var0) {
      return (MimeTypeMap.MimeType)nameToType.get(var0);
   }

   public static MimeTypeMap.MimeType getMimeTypeForSuffix(String var0) {
      return (MimeTypeMap.MimeType)suffixToType.get(var0);
   }

   public static Collection<MimeTypeMap.MimeType> getMimeTypes() {
      return nameToType.values();
   }

   static {
      try {
         JAXBContext var0 = JaxbCache.getContext(ObjectFactory.class);
         if (var0 != null) {
            Unmarshaller var1 = var0.createUnmarshaller();
            if (var1 != null) {
               Object var2 = var1.unmarshal(FileObject.class.getResource("/config/FileBrowser/MimeTypes.xml"));
               MimeTypes var3 = null;
               if (var2 instanceof JAXBElement) {
                  var2 = ((JAXBElement)JAXBElement.class.cast(var2)).getValue();
               }

               if (var2 instanceof MimeTypes) {
                  var3 = (MimeTypes)MimeTypes.class.cast(var2);
               }

               if (var3 != null) {
                  Iterator var4 = var3.getMimeType().iterator();

                  while(var4.hasNext()) {
                     MimeTypes.MimeType var5 = (MimeTypes.MimeType)var4.next();
                     registerMimeType(var5.getName(), var5.getIcon(), var5.getExtension());
                  }
               }
            }
         }
      } catch (Exception var6) {
      }

      nameToType = new HashMap();
      suffixToType = new HashMap();
   }

   public static class MimeType implements Comparable<MimeTypeMap.MimeType> {
      private String name;
      private String icon;
      private String[] suffixes;

      public MimeType(String var1, String var2, String... var3) {
         this.name = var1;
         this.icon = var2;
         this.suffixes = var3;
      }

      public String getIcon() {
         return this.icon;
      }

      public String getName() {
         return this.name;
      }

      public String[] getSuffixes() {
         return this.suffixes;
      }

      public int compareTo(MimeTypeMap.MimeType var1) {
         return this.name.compareToIgnoreCase(var1.name);
      }
   }
}
