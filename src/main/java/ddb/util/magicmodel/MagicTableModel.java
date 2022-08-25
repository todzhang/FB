package ddb.util.magicmodel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.Predicate;

public class MagicTableModel extends AbstractTableModel {
   public static final String UP_ICON_PATH = "images/1downarrow.png";
   public static final String DOWN_ICON_PATH = "images/1downarrow1.png";
   public static final String DELETE_ICON_PATH = "images/agt_action_fail.png";
   private static final Icon UP_ICON = new ImageIcon(ClassLoader.getSystemResource("images/1downarrow.png"));
   private static final Icon DOWN_ICON = new ImageIcon(ClassLoader.getSystemResource("images/1downarrow1.png"));
   private static final Icon DELETE_ICON = new ImageIcon(ClassLoader.getSystemResource("images/agt_action_fail.png"));
   private static final Insets BORDER = new Insets(0, 0, 0, 0);
   public static final int UP = 0;
   public static final int DOWN = 1;
   public static final int DELETE = 2;
   List<List<Object>> data = new Vector();
   List<JButton> up = new Vector();
   List<JButton> down = new Vector();
   List<JButton> delete = new Vector();
   String[] columns;
   Class<?>[] clazzes;
   Predicate[] valid;
   List<Object> exemplar = null;

   public void setClasses(Class<?>... var1) {
      if (var1.length == this.columns.length) {
         this.clazzes = (Class[])Arrays.copyOf(var1, var1.length);
      }
   }

   public void setExemplar(Object... var1) {
      this.setExemplar(Arrays.asList(var1));
   }

   public void setExemplar(List<Object> var1) {
      if (var1.size() == this.columns.length) {
         this.exemplar = new Vector();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            this.exemplar.add(var3);
         }

      }
   }

   public void setPredicate(Predicate... var1) {
      if (var1.length == this.columns.length) {
         this.valid = (Predicate[])Arrays.copyOf(var1, var1.length);
      }
   }

   public MagicTableModel(String... var1) {
      this.columns = var1;
   }

   @Override
   public int getRowCount() {
      return this.data.size() + 1;
   }

   @Override
   public int getColumnCount() {
      return this.columns.length + 3;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex < 0) {
         return null;
      } else if (rowIndex == this.data.size()) {
         return columnIndex <= this.columns.length ? "" : null;
      } else if (columnIndex < this.columns.length) {
         return ((List)this.data.get(rowIndex)).get(columnIndex);
      } else {
         columnIndex -= this.columns.length;
         switch(columnIndex) {
         case 0:
            if (rowIndex == 0) {
               return null;
            }

            return this.up.get(rowIndex);
         case 1:
            if (rowIndex + 2 == this.getRowCount()) {
               return null;
            }

            return this.down.get(rowIndex);
         case 2:
            if (rowIndex + 2 == this.getRowCount()) {
               return null;
            }

            return this.delete.get(rowIndex);
         default:
            return null;
         }
      }
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      if (rowIndex == this.data.size()) {
         return columnIndex < this.columns.length;
      } else {
         return true;
      }
   }

   public static int getButtonWidth() {
      JButton var0 = new JButton(" + ");
      return var0.getPreferredSize().width;
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (columnIndex < this.columns.length) {
         if (this.valid == null || this.valid[columnIndex].evaluate(aValue)) {
            if (rowIndex == this.data.size()) {
               this.data.add(this.createEntry(columnIndex, aValue));
               if (this.data.size() > this.up.size()) {
                  this.up.add(this.addNewUp(this.up.size()));
               }

               if (this.data.size() > this.down.size()) {
                  this.down.add(this.addNewDown(this.down.size()));
               }

               if (this.data.size() > this.delete.size()) {
                  this.delete.add(this.addNewDelete(this.delete.size()));
               }

               this.fireTableRowsInserted(rowIndex + 1, rowIndex + 1);
            } else {
               ((List)this.data.get(rowIndex)).set(columnIndex, aValue);
            }

            this.fireTableRowsUpdated(rowIndex, rowIndex);
         }
      }
   }

   private List<Object> createEntry(int var1, Object var2) {
      Vector var3 = new Vector();
      if (this.exemplar != null) {
         Iterator var4 = this.exemplar.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var3.add(var5);
         }
      } else {
         String[] var8 = this.columns;
         int var9 = var8.length;

         for(int var6 = 0; var6 < var9; ++var6) {
            String var10000 = var8[var6];
            var3.add("");
         }
      }

      var3.set(var1, var2);
      return var3;
   }

   void swap(int var1, int var2) {
      List var3 = (List)this.data.get(var1);
      this.data.set(var1, this.data.get(var2));
      this.data.set(var2, var3);
      this.fireTableRowsUpdated(var1, var2);
   }

   public void addData(Object... var1) {
      int var2 = this.data.size();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         this.setValueAt(var1[var3], var2, var3);
      }

   }

   private JButton addNewDelete(final int var1) {
      JButton var2 = new JButton(DELETE_ICON);
      var2.addActionListener(var1x -> MagicTableModel.this.deleteRow(var1));
      var2.setMargin(BORDER);
      return var2;
   }

   private JButton addNewUp(final int var1) {
      JButton var2 = new JButton(UP_ICON);
      var2.addActionListener(var1x -> MagicTableModel.this.swap(var1, var1 - 1));
      var2.setMargin(BORDER);
      return var2;
   }

   private JButton addNewDown(final int var1) {
      JButton var2 = new JButton(DOWN_ICON);
      var2.addActionListener(var1x -> MagicTableModel.this.swap(var1, var1 + 1));
      var2.setMargin(BORDER);
      return var2;
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex < this.columns.length) {
         return this.clazzes != null ? this.clazzes[columnIndex] : String.class;
      } else {
         columnIndex -= this.columns.length;
         switch(columnIndex) {
         case 0:
         case 1:
         case 2:
            return JButton.class;
         default:
            return null;
         }
      }
   }

   @Override
   public String getColumnName(int column) {
      return column < this.columns.length ? this.columns[column] : "";
   }

   private void deleteRow(int var1) {
      this.data.remove(var1);
      this.fireTableRowsDeleted(var1, var1);
   }

   public List<List<Object>> getValues() {
      return Collections.unmodifiableList(this.data);
   }

   public void customize(JTable var1) {
      var1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
      var1.setDefaultRenderer(JButton.class, new ComponentRenderer(new DefaultTableCellRenderer()));
      var1.setDefaultEditor(JButton.class, new JButtonCellEditor());
      this.setColumnWidth(var1.getColumnModel().getColumn(this.columns.length + 0));
      this.setColumnWidth(var1.getColumnModel().getColumn(this.columns.length + 1));
      this.setColumnWidth(var1.getColumnModel().getColumn(this.columns.length + 2));
   }

   private void setColumnWidth(TableColumn var1) {
      int var2 = getButtonWidth();
      var1.setMaxWidth(var2);
      var1.setMinWidth(var2);
   }
}
