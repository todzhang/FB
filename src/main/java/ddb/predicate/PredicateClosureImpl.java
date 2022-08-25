package ddb.predicate;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public class PredicateClosureImpl implements PredicateClosure {
   protected Predicate pred;
   protected Closure clos;

   public PredicateClosureImpl(Predicate var1, Closure var2) {
      this.pred = var1;
      this.clos = var2;
   }

   public boolean evaluate(Object var1) {
      return this.pred.evaluate(var1);
   }

   public void execute(Object var1) {
      this.clos.execute(var1);
   }
}
