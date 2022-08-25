package ddb.predicate;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Transformer;

public class TransformedClosure implements Closure {
   Transformer transformer;
   Closure closure;

   public TransformedClosure(Transformer var1, Closure var2) {
      this.transformer = var1;
      this.closure = var2;
   }

   public void execute(Object var1) {
      this.closure.execute(this.transformer.transform(var1));
   }
}
