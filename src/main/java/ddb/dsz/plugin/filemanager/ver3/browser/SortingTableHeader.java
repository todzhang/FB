package ddb.dsz.plugin.filemanager.ver3.browser;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SortingTableHeader {
   public static final String upIcon = "images/up.png";
   public static final String downIcon = "images/down.png";
   boolean ascending = true;
   TableColumn storedColumn;
   SortingTableHeader.SortingTableHeaderCallback callback;

   public SortingTableHeader(SortingTableHeader.SortingTableHeaderCallback var1) {
      this.callback = var1;
   }

   public void addMouseListenerToHeaderInTable(final JTable var1) {
      var1.setColumnSelectionAllowed(false);
      MouseAdapter var4 = new MouseAdapter() {
         public void mouseClicked(MouseEvent var1x) {
            if (var1x.getClickCount() == 1) {
               TableColumnModel var2 = var1.getColumnModel();
               int var3 = var1.columnAtPoint(var1x.getPoint());
               if (var3 != -1) {
                  SortingTableHeader.this.setSortingColumn(var2.getColumn(var3), var1, SortingTableHeader.this);
               }
            }
         }
      };
      JTableHeader var5 = var1.getTableHeader();
      var5.addMouseListener(var4);
   }

   void setSortingColumn(TableColumn var1, JTable var2, SortingTableHeader var3) {
      if (this.storedColumn != null) {
         this.storedColumn.setHeaderRenderer((TableCellRenderer)null);
      }

      if (var1 != null) {
         DefaultTableCellRenderer var4 = new DefaultTableCellRenderer();
         var1.setHeaderRenderer(var4);
         ImageIcon var5;
         if (var1 == this.storedColumn) {
            this.ascending = !this.ascending;
            if (this.ascending) {
               this.setNoSort();
               this.storedColumn = null;
               return;
            }
         } else {
            this.storedColumn = var1;
            this.ascending = true;
            var5 = new ImageIcon(this.getClass().getClassLoader().getResource("images/up.png"));
            var4.setIcon(var5);
         }

         if (this.ascending) {
            var5 = new ImageIcon(this.getClass().getClassLoader().getResource("images/down.png"));
            var4.setIcon(var5);
         } else {
            var5 = new ImageIcon(this.getClass().getClassLoader().getResource("images/up.png"));
            var4.setIcon(var5);
         }

         var4.setIconTextGap(10);
         Color var7 = var2.getSelectionBackground();
         Color var6 = var2.getSelectionForeground();
         var4.setBackground(var7);
         var4.setForeground(var6);
         var4.setHorizontalAlignment(0);
         var4.setHorizontalTextPosition(2);
         this.callback.setSorting(this.storedColumn, this.ascending);
      }
   }

   public void setNoSort() {
      this.setSortingColumn((TableColumn)null, (JTable)null, (SortingTableHeader)null);
   }

   public interface SortingTableHeaderCallback {
      void setSorting(TableColumn var1, boolean var2);
   }
}
