package ddb.util.predicate;

import org.apache.commons.collections.Predicate;

public class StringContainsPredicate implements Predicate {
   boolean caseSensitive = true;
   String value = null;

   public StringContainsPredicate() {
   }

   public StringContainsPredicate(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   @Override
   public boolean evaluate(Object object) {
      if (object == null) {
         return false;
      } else if (this.value == null) {
         return true;
      } else if (this.value.trim().length() == 0) {
         return true;
      } else {
         if (this.caseSensitive) {
            if (object.toString().contains(this.value)) {
               return true;
            }
         } else if (object.toString().toLowerCase().contains(this.value.toLowerCase())) {
            return true;
         }

         return false;
      }
   }

   public void setString(String value) {
      this.value = value;
   }

   @Override
   public String toString() {
      return String.format("StringContains[%s]", this.value);
   }
}
