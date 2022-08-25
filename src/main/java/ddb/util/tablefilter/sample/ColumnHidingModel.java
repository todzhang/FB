package ddb.util.tablefilter.sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColumnHidingModel extends DefaultTableColumnModel implements TableColumnModel {
   JPopupMenu menu = new JPopupMenu();
   Map<Enum<?>, TableColumn> enumToColumn = new HashMap();
   Map<Enum<?>, ColumnHidingModel.ColumnMenuItem> enumToMenuItem = new HashMap();
   Class<? extends Enum<?>> enumeration;

   public ColumnHidingModel(Class<? extends Enum<?>> enumeration) {
      this.enumeration = enumeration;

      Arrays.stream(enumeration.getEnumConstants()).filter(anEnum -> !anEnum.toString().equals("") && this.canChangeState(anEnum)).forEach(anEnum -> {
         ColumnMenuItem menuItem = new ColumnMenuItem(anEnum, anEnum.toString());
         this.enumToMenuItem.put(anEnum, menuItem);
         this.menu.add(menuItem);
      });

   }

   protected boolean canChangeState(Enum<?> var1) {
      return true;
   }

   public void hide(int var1) {
      this.hide(((Enum[])this.enumeration.getEnumConstants())[var1]);
   }

   public void hide(Enum<?> column) {
      synchronized(this) {
         super.removeColumn((TableColumn)this.enumToColumn.get(column));
         if (this.enumToMenuItem.get(column) != null) {
            ((ColumnHidingModel.ColumnMenuItem)this.enumToMenuItem.get(column)).setSelected(false);
         }

      }
   }

   public boolean show(int column) {
      return this.show(((Enum[])this.enumeration.getEnumConstants())[column]);
   }

   public boolean show(Enum<?> column) {
      synchronized(this) {
         if (super.tableColumns.contains(this.enumToColumn.get(column))) {
            return false;
         } else {
            super.addColumn((TableColumn)this.enumToColumn.get(column));
            if (this.enumToMenuItem.get(column) != null) {
               ((ColumnHidingModel.ColumnMenuItem)this.enumToMenuItem.get(column)).setSelected(true);
            }

            return true;
         }
      }
   }

   @Override
   public void addColumn(TableColumn tableColumn) {
      synchronized(this) {
         int count = super.getColumnCount();
         super.addColumn(tableColumn);
         Enum anEnum = ((Enum[])this.enumeration.getEnumConstants())[count];
         this.enumToColumn.put(anEnum, tableColumn);
      }
   }

   public void applyToTable(JTable jTable) {
      if (jTable != null) {
         this.applyToTableHeader(jTable.getTableHeader());
      }
   }

   public void applyToTableHeader(final JTableHeader jTableHeader) {
      jTableHeader.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1x) {
            this.maybePopup(var1x);
         }

         @Override
         public void mousePressed(MouseEvent var1x) {
            this.maybePopup(var1x);
         }

         @Override
         public void mouseReleased(MouseEvent var1x) {
            this.maybePopup(var1x);
         }

         private void maybePopup(MouseEvent var1x) {
            if (var1x.isPopupTrigger()) {
               ColumnHidingModel.this.menu.show(jTableHeader, var1x.getX(), var1x.getY());
               var1x.consume();
            }

         }
      });
   }

   public void moveColumnBefore(Enum<?> var1, Enum<?> var2) {
      int var3 = -1;
      int var4 = -1;
      if (var1 != var2) {
         synchronized(this) {
            TableColumn var6 = (TableColumn)this.enumToColumn.get(var1);
            TableColumn var7 = (TableColumn)this.enumToColumn.get(var2);

            for(int var8 = 0; var8 < super.getColumnCount(); ++var8) {
               if (var6 == super.getColumn(var8)) {
                  var3 = var8;
               }

               if (var7 == super.getColumn(var8)) {
                  var4 = var8;
               }
            }

            if (var3 == -1 || var4 == -1) {
               return;
            }
         }

         this.moveColumn(var3, var4);
      }
   }

   public int translateViewToModel(Enum<?> var1) {
      synchronized(this) {
         TableColumn var3 = (TableColumn)this.enumToColumn.get(var1);
         return var3.getModelIndex();
      }
   }

   private class ColumnMenuItem extends JCheckBoxMenuItem implements ActionListener {
      Enum<?> e;

      ColumnMenuItem(Enum<?> var2, String var3) {
         this.e = var2;
         super.setText(var3);
         super.setSelected(true);
         this.addActionListener(this);
      }

      @Override
      public void actionPerformed(ActionEvent var1) {
         if (this.isSelected()) {
            ColumnHidingModel.this.show(this.e);
         } else {
            ColumnHidingModel.this.hide(this.e);
         }

      }
   }
}
