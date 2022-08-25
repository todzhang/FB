package ddb.util.checkedtablemodel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class CheckBoxEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
   JCheckBox check = new JCheckBox();
   Object tag;

   public CheckBoxEditor(TableModel var1) {
      this.check.setOpaque(false);
      this.check.addActionListener(this);
      this.check.setAlignmentX(0.0F);
   }

   public Component getTableCellEditorComponent(JTable var1, Object var2, boolean var3, int var4, int var5) {
      this.check.setSelected((Boolean)Boolean.class.cast(var2));
      this.check.setText(var1.getModel().getValueAt(var4, CheckedTableColumns.CAPTION.ordinal()).toString());
      this.tag = var1.getModel().getValueAt(var4, CheckedTableColumns.TAG.ordinal());
      return this.check;
   }

   public void actionPerformed(ActionEvent var1) {
      this.fireEditingStopped();
   }

   public Object getCellEditorValue() {
      return this.check.isSelected();
   }
}
