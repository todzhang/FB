package ddb.dsz.plugin.taskmanager.details.group;

import ddb.dsz.plugin.taskmanager.processinformation.group.Attribute;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class AttributeRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (c instanceof JLabel) {
         JLabel label = (JLabel)JLabel.class.cast(c);
         if (value instanceof Attribute) {
            Attribute attr = (Attribute)Attribute.class.cast(value);
            label.setText(attr.getName());
            label.setForeground(attr.isEnabled() ? Color.BLACK : Color.LIGHT_GRAY);
         }
      }

      return c;
   }
}
