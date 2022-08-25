package ds.util.datatransforms.transformers;

import org.apache.commons.collections.Transformer;

public class LowerCaseTransformer implements Transformer {
   public Object transform(Object var1) {
      return var1 == null ? null : var1.toString().toLowerCase();
   }
}
