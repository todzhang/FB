package ddb.dsz.plugin.taskmanager.details.privilege;

import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.WindowsPrivilege;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class PrivilegeRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (c instanceof JLabel) {
         JLabel label = (JLabel)JLabel.class.cast(c);
         if (value instanceof Privilege) {
         }

         if (value instanceof WindowsPrivilege) {
            WindowsPrivilege wp = (WindowsPrivilege)WindowsPrivilege.class.cast(value);
            label.setText(wp.getName());
            label.setForeground(wp.isEnabled() ? Color.BLACK : Color.LIGHT_GRAY);
         }
      }

      return c;
   }
}
