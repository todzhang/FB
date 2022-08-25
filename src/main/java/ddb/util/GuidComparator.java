package ddb.util;

import java.util.Comparator;

public class GuidComparator implements Comparator<Object> {
   @Override
   public int compare(Object one, Object two) {
      if (one instanceof Guid && two instanceof Guid) {
         Guid jcb0 = (Guid)Guid.class.cast(one);
         Guid jcb1 = (Guid)Guid.class.cast(two);
         return ((Guid)Guid.class.cast(jcb0)).compareTo((Guid)Guid.class.cast(jcb1));
      } else if (one == two) {
         return 0;
      } else if (one == null) {
         return -1;
      } else {
         return two == null ? 1 : one.toString().compareToIgnoreCase(two.toString());
      }
   }
}
