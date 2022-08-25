package ddb.predicate;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

public class PredicateTransformerImpl implements PredicateTransformer {
   Predicate pred;
   Transformer trans;

   public PredicateTransformerImpl(Predicate pred, Transformer trans) {
      this.pred = pred;
      this.trans = trans;
   }

   @Override
   public boolean evaluate(Object o) {
      return this.pred.evaluate(o);
   }

   @Override
   public Object transform(Object o) {
      return this.trans.transform(o);
   }
}
