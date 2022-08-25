package ddb.imagemanager;

import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.commons.collections.Transformer;

class LoadImageOnDemand implements Transformer {
   private final ImageManager manager;

   LoadImageOnDemand(ImageManager var1) {
      this.manager = var1;
   }

   public Object transform(Object var1) {
      String var2 = var1.toString();
      URL var3 = this.getClass().getClassLoader().getResource(var2);
      if (var3 == null) {
         return this.manager.getBrokenFile(var2, ImageManager.BANNER_SIZE);
      } else {
         try {
            return new ImageIcon(var3);
         } catch (Exception var5) {
            return this.manager.getBrokenFile(var2, ImageManager.BANNER_SIZE);
         }
      }
   }
}
