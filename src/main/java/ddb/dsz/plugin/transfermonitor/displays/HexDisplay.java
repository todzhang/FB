package ddb.dsz.plugin.transfermonitor.displays;

import ddb.GuiConstants;
import ddb.dsz.plugin.transfermonitor.TransferTabbable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class HexDisplay extends TransferTabbable {
   byte[] data = null;
   HexTableModel model = new HexTableModel(8);
   JTable table;
   int row;
   int column;

   public HexDisplay() {
      super("Binary");
      this.table = new JTable(this.model);
      this.row = 0;
      this.column = 0;
      this.display.setLayout(new BorderLayout());
      JScrollPane var1 = new JScrollPane(this.table);
      this.display.add(var1, "Center");
      this.table.setRowSelectionAllowed(false);
      this.table.setShowHorizontalLines(false);
      this.table.setShowVerticalLines(false);
      this.table.setTableHeader((JTableHeader)null);
      this.recalc();
      this.display.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            HexDisplay.this.recalc();
         }

         public void componentShown(ComponentEvent var1) {
            HexDisplay.this.recalc();
         }
      });
   }

   void highlightMatchingCell() {
      synchronized(this) {
         if (this.column != 0) {
            int var2;
            if (this.column < this.model.getColumns() + 1) {
               var2 = this.column + this.model.getColumns();
               this.table.changeSelection(this.row, var2, false, true);
            } else if (this.column < this.model.getColumns() * 2 + 1) {
               var2 = this.column - this.model.getColumns();
               this.table.changeSelection(this.row, var2, false, true);
            }
         }
      }
   }

   public byte[] getData() {
      synchronized(this) {
         return this.data;
      }
   }

   public void setData(byte[] var1) {
      synchronized(this) {
         this.data = var1;
         this.model.setData(var1);
      }
   }

   void recalc() {
      synchronized(this) {
         JLabel var2 = new JLabel();
         var2.setFont(GuiConstants.FIXED_WIDTH_FONT.Basic);
         Dimension var3 = null;
         int var4 = 100;
         int var5 = 100;
         int var6 = 100;
         int var7 = this.display.getWidth();
         var2.setText("AAAAAAAA");
         var3 = var2.getMinimumSize();
         if (var3 != null) {
            var4 = var3.width;
         }

         var2.setText("AA");
         var3 = var2.getMinimumSize();
         if (var3 != null) {
            var5 = var3.width * 35 / 20;
         }

         var2.setText("W");
         var3 = var2.getMinimumSize();
         if (var3 != null) {
            var6 = var3.width * 35 / 20;
         }

         var7 -= var4;
         int var8 = var7 / (var6 + var5);
         this.model.setColumns(var8);
         TableColumnModel var9 = this.table.getColumnModel();

         for(int var10 = 0; var10 < this.model.getColumnCount(); ++var10) {
            if (var10 == 0) {
               var9.getColumn(0).setResizable(false);
               var9.getColumn(0).setMinWidth(var4);
               var9.getColumn(0).setMaxWidth(var4);
            } else if (var10 < var8 + 1) {
               var9.getColumn(var10).setResizable(false);
               var9.getColumn(var10).setMinWidth(var5);
               var9.getColumn(var10).setMaxWidth(var5);
            } else {
               var9.getColumn(var10).setResizable(false);
               var9.getColumn(var10).setMinWidth(var6);
               var9.getColumn(var10).setMaxWidth(var6);
            }
         }

      }
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isClosable() {
      return false;
   }
}
