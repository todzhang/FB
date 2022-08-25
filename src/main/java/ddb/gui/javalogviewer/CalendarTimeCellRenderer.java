package ddb.gui.javalogviewer;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JTable;

public class CalendarTimeCellRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (var7 instanceof JLabel && value instanceof Calendar) {
         JLabel var8 = (JLabel)var7;
         Calendar var9 = (Calendar) value;
         StringBuffer var10 = new StringBuffer();
         var10.append(var9.get(11));
         var10.append(":");
         if (var9.get(12) < 10) {
            var10.append("0");
         }

         var10.append(var9.get(12));
         var10.append(":");
         if (var9.get(13) < 10) {
            var10.append("0");
         }

         var10.append(var9.get(13));
         var8.setText(var10.toString());
      }

      return var7;
   }
}
