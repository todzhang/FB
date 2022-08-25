package ddb.dsz.core.operation;

import ddb.util.Guid;
import java.util.Calendar;

public interface Operation extends Comparable<Operation> {
   Operation NULL = new Operation() {
      @Override
      public Guid getGuid() {
         return Guid.NULL;
      }

      @Override
      public Calendar getStartTime() {
         return null;
      }

      @Override
      public Calendar getLastTime() {
         return null;
      }

      @Override
      public int compareTo(Operation operation) {
         return this == operation ? 0 : 1;
      }
   };

   Guid getGuid();

   Calendar getStartTime();

   Calendar getLastTime();
}
