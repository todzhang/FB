package ddb.dsz.plugin.netmapviewer.display;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;

public class CalendarRenderer extends CustomTableCellRenderer {
   private final DateFormat format = new SimpleDateFormat("hh:mm:ss aaa");

   protected Component modifyComponent(JLabel var1, Object var2) {
      if (var2 instanceof Calendar) {
         var1.setText(this.format.format(((Calendar)Calendar.class.cast(var2)).getTime()));
      }

      return var1;
   }
}
