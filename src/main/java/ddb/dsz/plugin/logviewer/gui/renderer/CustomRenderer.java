package ddb.dsz.plugin.logviewer.gui.renderer;

import ddb.dsz.plugin.logviewer.gui.detail.ValueTableColumns;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import java.math.BigInteger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

public class CustomRenderer extends DszTableCellRenderer {
   int base = 10;
   String prefix = "";
   JTable values;

   public CustomRenderer(JTable values) {
      this.values = values;
   }

   public void setBase(int newBase, String prefix) {
      this.base = newBase;
      this.prefix = prefix;
      this.values.tableChanged(new TableModelEvent(this.values.getModel(), 0, this.values.getRowCount(), ValueTableColumns.VALUE.ordinal(), 0));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (column == ValueTableColumns.VALUE.ordinal() && value instanceof BigInteger && c instanceof JLabel) {
         BigInteger bi = (BigInteger)value;
         JLabel label = (JLabel)c;
         label.setText(String.format("%s%s", this.prefix, bi.toString(this.base)));
      }

      return c;
   }
}
