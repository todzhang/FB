package ddb.util.tablefilter;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public interface TableFilterModel extends TableModel, TableModelListener {
   int translateViewLocationToModelColumn(int rowIndex, int columnIndex);

   int translateViewLocationToModelRow(int rowIndex, int columnIndex);

   void setModel(TableModel tableModel);

   void applyTableConfiguration(JTable jTable);

   void removeTableConfiguration(JTable jTable);
}
