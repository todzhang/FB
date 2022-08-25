package ddb.util.predicate;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.commons.collections.Predicate;

public class UnionListPredicate<E> implements Predicate {
   List<E> objects = new Vector();
   boolean allowAllOnEmpty;

   public UnionListPredicate(boolean allowAllOnEmpty) {
      this.allowAllOnEmpty = allowAllOnEmpty;
   }

   @Override
   public boolean evaluate(Object object) {
      synchronized(this) {
         if (this.objects.size() == 0) {
            return this.allowAllOnEmpty;
         } else {
            Iterator var3 = this.objects.iterator();

            Object var4;
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               var4 = var3.next();
            } while(!var4.equals(object));

            return true;
         }
      }
   }

   public void addItem(E var1) {
      synchronized(this) {
         if (!this.objects.contains(var1)) {
            this.objects.add(var1);
         }
      }
   }

   public void removeItem(E var1) {
      synchronized(this) {
         this.objects.remove(var1);
      }
   }

   public void clear() {
      synchronized(this) {
         this.objects.clear();
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("UnionList[");
      if (this.allowAllOnEmpty && this.objects.isEmpty()) {
         var1.append("-all-");
      } else {
         boolean var2 = true;

         Object var4;
         for(Iterator var3 = this.objects.iterator(); var3.hasNext(); var1.append(var4.toString())) {
            var4 = var3.next();
            if (!var2) {
               var1.append(", ");
               var2 = false;
            }
         }
      }

      var1.append("]");
      return var1.toString();
   }
}
