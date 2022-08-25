package ddb.dsz.plugin.taskmanager.details.group;

import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import ddb.dsz.plugin.taskmanager.processinformation.group.WindowsGroup;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class GroupRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (c instanceof JLabel) {
         JLabel label = (JLabel)JLabel.class.cast(c);
         if (value instanceof Group) {
         }

         if (value instanceof WindowsGroup) {
            WindowsGroup wg = (WindowsGroup)WindowsGroup.class.cast(value);
            label.setText(wg.getName());
         }
      }

      return c;
   }
}
