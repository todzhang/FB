package ddb.dsz.plugin.logviewer.gui.renderer;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public abstract class CustomTableCellRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (c instanceof JLabel) {
         c = this.modifyComponent((JLabel)JLabel.class.cast(c), value);
      }

      return c;
   }

   protected Component modifyComponent(JLabel label, Object value) {
      return label;
   }
}
