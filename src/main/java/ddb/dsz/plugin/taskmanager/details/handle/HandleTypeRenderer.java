package ddb.dsz.plugin.taskmanager.details.handle;

import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;

public class HandleTypeRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof Handle.HandleType) {
         value = ((Handle.HandleType)Handle.HandleType.class.cast(value)).getType();
      }

      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }
}
