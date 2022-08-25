package ddb.util;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableMap extends AbstractTableModel implements TableModelListener {
   protected TableModel model;

   public TableModel getModel() {
      return this.model;
   }

   public void setModel(TableModel model) {
      this.model = model;
      model.addTableModelListener(this);
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      return this.model.getValueAt(rowIndex, columnIndex);
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.model.setValueAt(aValue, rowIndex, columnIndex);
   }

   @Override
   public int getRowCount() {
      return this.model == null ? 0 : this.model.getRowCount();
   }

   @Override
   public int getColumnCount() {
      return this.model == null ? 0 : this.model.getColumnCount();
   }

   @Override
   public String getColumnName(int column) {
      return this.model.getColumnName(column);
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return this.model.getColumnClass(columnIndex);
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return this.model.isCellEditable(rowIndex, columnIndex);
   }

   @Override
   public void tableChanged(TableModelEvent e) {
      this.fireTableChanged(e);
   }
}
