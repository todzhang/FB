package ddb.util.tablefilter;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class DefaultTableFilter extends AbstractTableModel implements TableFilterModel, TableModelListener {
   protected TableModel model;

   public DefaultTableFilter() {
      this.model = null;
   }

   public DefaultTableFilter(TableModel tableModel) {
      this.setModel(tableModel);
   }

   @Override
   public void setModel(TableModel tableModel) {
      if (this.model != null) {
         this.model.removeTableModelListener(this);
      }

      this.model = tableModel;
      if (tableModel != null) {
         this.model.addTableModelListener(this);
      }

      this.fireTableStructureChanged();
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return this.model == null ? Object.class : this.model.getColumnClass(columnIndex);
   }

   @Override
   public int getColumnCount() {
      return this.model == null ? 0 : this.model.getColumnCount();
   }

   @Override
   public String getColumnName(int column) {
      return this.model == null ? null : this.model.getColumnName(column);
   }

   @Override
   public int getRowCount() {
      return this.model == null ? 0 : this.model.getRowCount();
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      return this.model == null ? null : this.model.getValueAt(this.translateViewLocationToModelRow(rowIndex, columnIndex), this.translateViewLocationToModelColumn(rowIndex, columnIndex));
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return this.model == null ? false : this.model.isCellEditable(this.translateViewLocationToModelRow(rowIndex, columnIndex), this.translateViewLocationToModelColumn(rowIndex, columnIndex));
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (this.model != null) {
         this.model.setValueAt(aValue, this.translateViewLocationToModelRow(rowIndex, columnIndex), this.translateViewLocationToModelColumn(rowIndex, columnIndex));
      }
   }

   @Override
   public int translateViewLocationToModelColumn(int rowIndex, int columnIndex) {
      return columnIndex;
   }

   @Override
   public int translateViewLocationToModelRow(int rowIndex, int columnIndex) {
      return rowIndex;
   }

   @Override
   public void tableChanged(TableModelEvent var1) {
      if (var1.getFirstRow() == -1 && var1.getLastRow() == -1 && var1.getColumn() == -1) {
         this.fireTableStructureChanged();
      } else {
         this.fireTableDataChanged();
      }

   }

   @Override
   public void applyTableConfiguration(JTable jTable) {
   }

   @Override
   public void removeTableConfiguration(JTable jTable) {
   }
}
