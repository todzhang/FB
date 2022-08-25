package ddb.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class StringCompletor {
   public static <O> List<Object> complete(String prefix, Collection<O> candidates) {
      if (prefix == null) {
         prefix = "";
      }

      List<Object> completions = new Vector(100, 25);
      Iterator i = candidates.iterator();

      while(i.hasNext()) {
         Object o = i.next();
         String candidate = o.toString();
         if (candidate.toLowerCase().startsWith(prefix.toLowerCase())) {
            completions.add(o);
         }
      }

      return completions;
   }
}
