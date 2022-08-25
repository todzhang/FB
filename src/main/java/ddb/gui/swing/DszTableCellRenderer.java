package ddb.gui.swing;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class DszTableCellRenderer extends DefaultTableCellRenderer {
   private static final Border border = BorderFactory.createEmptyBorder(0, 15, 0, 10);

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (component instanceof JComponent) {
         ((JComponent)JComponent.class.cast(component)).setBorder(border);
      }

      return component;
   }
}
