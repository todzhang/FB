package ddb.dsz.plugin.verifier;

import org.apache.commons.collections.Predicate;

public class OptionalHidingPredicate implements Predicate {
   Object object;
   boolean show;

   public OptionalHidingPredicate(Object var1) {
      this(var1, true);
   }

   public OptionalHidingPredicate(Object var1, boolean var2) {
      this.object = var1;
      this.show = var2;
   }

   public boolean evaluate(Object var1) {
      if (this.object == null) {
         return true;
      } else {
         return this.object.equals(var1) ? this.show : true;
      }
   }

   public void setShow(boolean var1) {
      this.show = var1;
   }
}
