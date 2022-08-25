package ddb.gui.debugview;

import org.apache.commons.collections.Predicate;

public class ImportancePredicate implements Predicate {
   private Importance importance;

   public ImportancePredicate() {
      this.importance = Importance.INFO;
   }

   public boolean evaluate(Object value) {
      if (value instanceof Importance) {
         Importance imp = (Importance)Importance.class.cast(value);
         return imp.compareTo(this.importance) >= 0;
      } else {
         return false;
      }
   }

   public Importance getImportance() {
      return this.importance;
   }

   public void setImportance(Importance importance) {
      this.importance = importance;
   }
}
