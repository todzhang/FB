package ddb.dsz.plugin.netmapviewer.display;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public abstract class CustomTableCellRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (var7 instanceof JLabel) {
         var7 = this.modifyComponent((JLabel)JLabel.class.cast(var7), value);
      }

      return var7;
   }

   protected Component modifyComponent(JLabel var1, Object var2) {
      return var1;
   }
}
