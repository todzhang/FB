package ds.util.datatransforms.transformers;

import org.apache.commons.collections.Transformer;

public class BooleanTransformer implements Transformer {
   static final String[] trueArray = new String[]{"1", "on", "yes", "true"};

   public Object transform(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.toString().toLowerCase();
         String[] var3 = trueArray;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var2.equals(var6)) {
               return Boolean.TRUE;
            }
         }

         return Boolean.FALSE;
      }
   }
}
