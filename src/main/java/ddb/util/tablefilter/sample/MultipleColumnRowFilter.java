package ddb.util.tablefilter.sample;

import org.apache.commons.collections.Predicate;

public class MultipleColumnRowFilter extends RowFilter {
   int[] columns = new int[0];

   public MultipleColumnRowFilter(Predicate predicate, int... columns) {
      super(predicate);
      this.setColumns(columns);
   }

   public MultipleColumnRowFilter(Predicate predicate, Enum<?>... columns) {
      super(predicate);
      this.setColumns(columns);
   }

   @Override
   protected boolean show(int var1) {
      if (this.columns.length == 0) {
         return true;
      } else {
         for(int var2 = 0; var2 < this.columns.length; ++var2) {
            if (this.filter.evaluate(this.model.getValueAt(var1, this.columns[var2]))) {
               return true;
            }
         }

         return false;
      }
   }

   public void setColumns(int... columns) {
      this.writeLock();

      try {
         this.columns = columns;
      } finally {
         this.writeUnlock();
      }

   }

   public void setColumns(Enum<?>... enums) {
      int[] columns = new int[enums.length];

      for(int i = 0; i < enums.length; ++i) {
         columns[i] = enums[i].ordinal();
      }

      this.setColumns(columns);
   }
}
