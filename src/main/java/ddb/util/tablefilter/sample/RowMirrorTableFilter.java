package ddb.util.tablefilter.sample;

import ddb.util.tablefilter.DefaultTableFilter;

public class RowMirrorTableFilter extends DefaultTableFilter {
   @Override
   public int translateViewLocationToModelRow(int rowIndex, int columnIndex) {
      return this.getRowCount() - (rowIndex + 1);
   }
}
