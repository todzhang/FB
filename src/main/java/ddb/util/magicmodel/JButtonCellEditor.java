package ddb.util.magicmodel;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class JButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
   JButton button = new JButton("+");

   @Override
   public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column) {
      if (value == null) {
         return null;
      } else {
         return (Component)(value instanceof Component ? (Component)value : this.button);
      }
   }

   @Override
   public Object getCellEditorValue() {
      return null;
   }
}
