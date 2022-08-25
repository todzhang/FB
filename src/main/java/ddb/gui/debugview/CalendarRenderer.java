package ddb.gui.debugview;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JTable;

public class CalendarRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (c instanceof JLabel && value instanceof Calendar) {
         JLabel label = (JLabel)c;
         Calendar cal = (Calendar)value;
         label.setText(String.format("%d:%d:%d.%03d", cal.get(11), cal.get(12), cal.get(13), cal.get(14)));
      }

      return c;
   }
}
