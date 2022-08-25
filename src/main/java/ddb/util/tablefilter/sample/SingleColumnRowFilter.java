package ddb.util.tablefilter.sample;

import org.apache.commons.collections.Predicate;

public class SingleColumnRowFilter extends MultipleColumnRowFilter {
   public SingleColumnRowFilter(Predicate predicate, int column) {
      super(predicate, column);
   }

   public SingleColumnRowFilter(Predicate predicate, Enum<?> column) {
      super(predicate, column);
   }

   public void setFilter(Predicate filter, int column) {
      super.setFilter(filter);
      super.setColumns(column);
   }

   public void setFilter(Predicate filter, Enum<?> column) {
      this.setFilter(filter, column.ordinal());
   }
}
