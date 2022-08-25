package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JTable;

public class LogRenderer extends DszTableCellRenderer {
   DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   NumberFormat LONG_FORMAT = new DecimalFormat();

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!(c instanceof JLabel)) {
         return c;
      } else {
         JLabel label = (JLabel)c;
         if (value instanceof File) {
            label.setHorizontalAlignment(2);
            label.setText(this.getName((File)File.class.cast(value)));
         } else if (value instanceof Long) {
            label.setHorizontalAlignment(4);
            label.setText(this.LONG_FORMAT.format(Long.class.cast(value)));
         } else if (value instanceof Calendar) {
            label.setHorizontalAlignment(4);
            label.setText(this.DATE_FORMAT.format(((Calendar)Calendar.class.cast(value)).getTime()));
         } else if (value == null) {
            label.setText("");
         }

         return label;
      }
   }

   private String getName(File file) {
      String retVal = file.getName();
      int index = retVal.indexOf("_");
      if (index != -1) {
         retVal = retVal.substring(0, index);
      }

      return retVal;
   }
}
