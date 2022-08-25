package ddb.util.magicmodel;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComponentRenderer implements TableCellRenderer {
   private TableCellRenderer __defaultRenderer;

   public ComponentRenderer(TableCellRenderer tableCellRenderer) {
      this.__defaultRenderer = tableCellRenderer;
   }

   @Override
   public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      return value instanceof Component ? (Component)value : this.__defaultRenderer.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);
   }
}
