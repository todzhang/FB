package ddb.util.predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Predicate;

public class MustMatchOnePredicate implements Predicate {
   private Map<Object, Boolean> validElementsMap;
   final boolean bCaseSensitive;

   public MustMatchOnePredicate() {
      this(false);
   }

   public MustMatchOnePredicate(Object... var1) {
      this(false, var1);
   }

   public MustMatchOnePredicate(boolean var1, Object... var2) {
      this(var1);
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         this.add(var6);
      }

   }

   public MustMatchOnePredicate(boolean caseSensitive) {
      this.validElementsMap = new HashMap();
      this.bCaseSensitive = caseSensitive;
   }

   private Object toLowerCase(Object var1) {
      if (!this.bCaseSensitive && var1 instanceof String) {
         var1 = (String.class.cast(var1)).toLowerCase();
      }

      return var1;
   }

   @Override
   public boolean evaluate(Object object) {
      object = this.toLowerCase(object);
      synchronized(this) {
         Boolean var3 = (Boolean)this.validElementsMap.get(object);
         if (var3 == null) {
            var3 = Boolean.FALSE;
         }

         return var3;
      }
   }

   public void add(Object var1) {
      var1 = this.toLowerCase(var1);
      synchronized(this) {
         this.validElementsMap.put(var1, Boolean.TRUE);
      }
   }

   public void remove(Object var1) {
      var1 = this.toLowerCase(var1);
      synchronized(this) {
         this.validElementsMap.remove(var1);
      }
   }

   public int getCount() {
      synchronized(this) {
         return this.validElementsMap.size();
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("MustMatchOne[");
      boolean var2 = true;
      Iterator var3 = this.validElementsMap.keySet().iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if ((Boolean)this.validElementsMap.get(var4)) {
            if (!var2) {
               var1.append(", ");
               var2 = false;
            }

            var1.append(var4.toString());
         }
      }

      var1.append("]");
      return var1.toString();
   }
}
