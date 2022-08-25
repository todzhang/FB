package ddb.imagemanager;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.apache.commons.collections.Transformer;

class ResizeImageOnDemand implements Transformer {
   private final ImageManager manager;
   Dimension size;

   public ResizeImageOnDemand(ImageManager var1, Object var2) {
      this.manager = var1;
      this.size = (Dimension)Dimension.class.cast(var2);
   }

   public Object transform(Object var1) {
      Image var2 = this.manager.getFullIcon(var1.toString()).getImage().getScaledInstance(this.size.width, this.size.height, 4);
      ImageIcon var3 = new ImageIcon();
      var3.setImage(var2);
      return var3;
   }
}
