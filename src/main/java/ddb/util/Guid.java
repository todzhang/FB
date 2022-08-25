package ddb.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Guid implements Comparable<Guid> {
   private static final List<Guid> ALL_SORTED_GUIDS = new ArrayList();
   public static final Guid NULL;
   private final BigInteger value;

   public static Guid GenerateGuid(Guid other) {
      return other;
   }

   public static Guid GenerateGuid(String guid) {
      return GenerateGuid(new BigInteger(guid.replaceAll("-", ""), 16));
   }

   public static Guid GenerateGuid(BigInteger guid) {
      return addGuid(new Guid(guid));
   }

   private static synchronized Guid addGuid(Guid g) {
      int index = Collections.binarySearch(ALL_SORTED_GUIDS, g);
      if (index >= 0) {
         return (Guid)ALL_SORTED_GUIDS.get(index);
      } else {
         ++index;
         index = 0 - index;
         if (index < ALL_SORTED_GUIDS.size()) {
            ALL_SORTED_GUIDS.add(index, g);
         } else {
            ALL_SORTED_GUIDS.add(g);
         }

         return g;
      }
   }

   public Guid(BigInteger guid) {
      this.value = guid;
   }

   @Override
   public String toString() {
      return convert(this.value);
   }

   public BigInteger asInteger() {
      return this.value;
   }

   public String asString() {
      return convert(this.value);
   }

   @Override
   public int compareTo(Guid o) {
      if (this == o) {
         return 0;
      } else {
         return o == null ? 1 : this.value.compareTo(o.value);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return  31 * result + (this.value == null ? 0 : this.value.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Guid other = (Guid)obj;
         return this.value.equals(other.value);
      }
   }

   public static String convert(BigInteger value) {
      if (value == null) {
         return null;
      } else {
         String input = String.format("%032x", value);
         return String.format("%s-%s-%s-%s", input.substring(0, 8), input.substring(8, 12), input.substring(12, 16), input.substring(16, 32));
      }
   }

   static {
      NULL = GenerateGuid(BigInteger.ZERO);
   }
}
