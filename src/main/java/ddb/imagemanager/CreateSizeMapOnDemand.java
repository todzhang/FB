package ddb.imagemanager;

import java.util.Hashtable;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

class CreateSizeMapOnDemand implements Transformer {
   ImageManager manager;

   public CreateSizeMapOnDemand(ImageManager var1) {
      this.manager = var1;
   }

   public Object transform(Object var1) {
      return LazyMap.decorate(new Hashtable(), new ResizeImageOnDemand(this.manager, var1));
   }
}
