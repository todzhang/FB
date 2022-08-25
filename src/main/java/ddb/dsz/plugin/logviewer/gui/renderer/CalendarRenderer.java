package ddb.dsz.plugin.logviewer.gui.renderer;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;

public class CalendarRenderer extends CustomTableCellRenderer {
   private final DateFormat format = new SimpleDateFormat("hh:mm:ss aaa");

   protected Component modifyComponent(JLabel label, Object value) {
      if (value instanceof Calendar) {
         label.setText(this.format.format(((Calendar)Calendar.class.cast(value)).getTime()));
      }

      return label;
   }
}
