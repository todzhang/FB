package ddb.dsz.plugin.transfermonitor.displays;

import javax.swing.table.AbstractTableModel;

public class HexTableModel extends AbstractTableModel {
   int columns = 0;
   byte[] data = null;
   byte[] empty = "File Loading...".getBytes();

   public HexTableModel(int var1) {
      this.setColumns(var1);
   }

   public int getColumnCount() {
      return this.columns * 2 + 1;
   }

   public int getRowCount() {
      return this.data == null ? this.empty.length / this.columns + 1 : this.data.length / this.columns + 1;
   }

   public Object getValueAt(int var1, int var2) {
      synchronized(this) {
         if (var2 == 0) {
            return this.data != null ? this.Hexify(Integer.toHexString(var1 * this.columns), Integer.toHexString(this.data.length).length()) : this.Hexify(Integer.toHexString(var1 * this.columns), Integer.toHexString(this.empty.length).length());
         } else {
            int var4 = var2 - 1;
            int var5 = var4 + var1 * this.columns;
            if (var2 <= this.columns) {
               if (this.data != null) {
                  if (var5 < this.data.length) {
                     return this.Hexify(Integer.toHexString(this.data[var5]), 2);
                  }
               } else if (var5 < this.empty.length) {
                  return this.Hexify(Integer.toHexString(this.empty[var5]), 2);
               }

               return "";
            } else {
               var4 -= this.columns;
               var5 = var4 + var1 * this.columns;
               if (var2 < this.columns * 2 + 1) {
                  if (this.data != null) {
                     if (var5 < this.data.length) {
                        if (this.data[var5] > 32 && this.data[var5] < 127) {
                           return Character.toString((char)this.data[var5]);
                        }

                        return ".";
                     }
                  } else if (var5 < this.empty.length) {
                     if (this.empty[var5] > 32 && this.empty[var5] < 127) {
                        return Character.toString((char)this.empty[var5]);
                     }

                     return ".";
                  }
               }

               return "";
            }
         }
      }
   }

   public void setData(byte[] var1) {
      this.data = var1;
      this.fireTableDataChanged();
   }

   private String Hexify(String var1, int var2) {
      StringBuffer var3 = new StringBuffer();

      while(var2 > var1.length()) {
         --var2;
         var3.append('0');
      }

      return var3.toString() + var1.toUpperCase();
   }

   public void setColumns(int var1) {
      synchronized(this) {
         if (var1 < 1) {
            this.columns = 1;
            return;
         }

         this.columns = var1;
      }

      this.fireTableStructureChanged();
   }

   public int getColumns() {
      return this.columns;
   }

   public int getDataLength() {
      return this.data == null ? 0 : this.data.length;
   }
}
