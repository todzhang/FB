package ds.util.datatransforms;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class URIResolverImpl implements URIResolver {
   String resourceDir;
   String suffix;
   String[] paths = new String[0];
   File path;

   public URIResolverImpl(File var1, String var2, String[] var3) {
      this.path = var1;
      this.suffix = var1.getName();
      this.resourceDir = var2;
      this.paths = var3;
   }

   public URIResolverImpl(String var1, String var2, String... var3) {
      this.resourceDir = var1;
      this.suffix = var2;
      this.paths = var3;
   }

   public Source resolve(String var1, String var2) throws TransformerException {
      try {
         File var3 = null;

         try {
            var3 = new File(new URI(var2));
         } catch (URISyntaxException var10) {
            var3 = new File(var2);
            if (var2.startsWith("file:/")) {
               var3 = new File(var2.substring(6));
            }
         }

         File var4 = new File(var3.getParentFile(), var1);
         if (var4.exists()) {
            return new StreamSource(var4);
         } else {
            if (this.path != null) {
               File var5 = new File(this.path, var1);
               if (var5.exists()) {
                  return new StreamSource(var5);
               }
            }

            String[] var12 = this.paths;
            int var6 = var12.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var12[var7];
               File var9 = new File(String.format("%s/%s/Commands/%s/%s", this.resourceDir, var8, this.suffix, var1));
               if (var9.exists()) {
                  return new StreamSource(var9);
               }
            }

            System.out.println("Returning a null here");
            return null;
         }
      } catch (NullPointerException var11) {
         System.out.println("Resolve null pointer");
         return null;
      }
   }
}
