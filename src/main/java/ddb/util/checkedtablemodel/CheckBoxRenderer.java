package ddb.util.checkedtablemodel;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class CheckBoxRenderer extends DefaultTableCellRenderer {
   JCheckBox stamp = new JCheckBox();

   public CheckBoxRenderer(TableModel var1) {
      this.stamp.setAlignmentX(0.0F);
      this.stamp.setOpaque(false);
   }

   public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
      this.stamp.setSelected((Boolean)Boolean.class.cast(var2));
      this.stamp.setText(var1.getModel().getValueAt(var5, CheckedTableColumns.CAPTION.ordinal()).toString());
      return this.stamp;
   }

   public boolean isCellEditable(EventObject var1) {
      return true;
   }

   public boolean shouldSelectCell(EventObject var1) {
      return true;
   }
}
