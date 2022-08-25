package ddb.util;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSorter extends TableMap {
   public static final String upIcon = "images/up.png";
   public static final String downIcon = "images/down.png";
   int modelLength = 0;
   Integer[] indexes;
   boolean ascending = true;
   TableColumn storedColumn;
   Comparator<Object> compareBy;
   int sortingColumn = -1;
   private final ReentrantReadWriteLock Lock = new ReentrantReadWriteLock();

   private void readLock() {
      this.Lock.readLock().lock();
   }

   private void readUnlock() {
      this.Lock.readLock().unlock();
   }

   private void writeLock() {
      this.Lock.writeLock().lock();
   }

   private void writeUnlock() {
      this.Lock.writeLock().unlock();
   }

   public TableSorter() {
      this.indexes = new Integer[0];
   }

   public void dump() {
      this.readLock();

      try {
         StringBuilder var1 = new StringBuilder();
         Integer[] var2 = this.indexes;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(this.indexes[var5]);
         }

         System.out.println("Indices:  [" + var1.toString() + "]");
      } finally {
         this.readUnlock();
      }

   }

   public TableSorter(TableModel var1) {
      this.setModel(var1);
   }

   @Override
   public void setModel(TableModel model) {
      this.writeLock();

      try {
         super.setModel(model);
         this.modelLength = model.getRowCount();
         this.reallocateIndexes();
      } finally {
         this.writeUnlock();
      }

      super.tableChanged(new TableModelEvent(this));
   }

   @Override
   public TableModel getModel() {
      return this.model;
   }

   @Override
   public String toString() {
      return String.format("Table Sorter [%s]", this.model.toString());
   }

   public int compareRowsByColumn(int rowIndex1, int rowIndex2, int columnIndex) {
      if (columnIndex == -1) {
         return 0;
      } else {
         TableModel model = this.model;
         Object var5 = model.getValueAt(rowIndex1, columnIndex);
         Object var6 = model.getValueAt(rowIndex2, columnIndex);
         if (this.compareBy != null) {
            return this.compareBy.compare(var5, var6);
         } else if (var5 == null && var6 == null) {
            return 0;
         } else if (var5 == null) {
            return -1;
         } else if (var6 == null) {
            return 1;
         } else if (var5 instanceof Comparable) {
            Comparable var7 = (Comparable)var5;
            return var7.compareTo(var6);
         } else {
            return var5.toString().compareTo(var6.toString());
         }
      }
   }

   public int compare(int rowIndex1, int rowIndex2) {
      int var3 = this.compareRowsByColumn(rowIndex1, rowIndex2, this.sortingColumn);
      return this.ascending ? var3 : -var3;
   }

   public void reallocateIndexes() {
      this.indexes = new Integer[this.modelLength];

      for(int var1 = 0; var1 < this.modelLength; ++var1) {
         this.indexes[var1] = var1;
      }

   }

   @Override
   public int getRowCount() {
      this.readLock();

      int var1;
      try {
         var1 = this.modelLength;
      } finally {
         this.readUnlock();
      }

      return var1;
   }

   @Override
   public void tableChanged(TableModelEvent e) {
      this.modelLength = this.model.getRowCount();
      this.writeLock();

      try {
         this.reallocateIndexes();
         if (this.sortingColumn != -1) {
            this.sort((Object)null);
         }
      } finally {
         this.writeUnlock();
      }

      super.tableChanged(e);
   }

   public void sort(Object var1) {
      this.writeLock();

      try {
         Arrays.sort(this.indexes, (rowIndex1, rowIndex2) -> {
            int var3 = TableSorter.this.compareRowsByColumn(rowIndex1, rowIndex2, TableSorter.this.sortingColumn);
            if (var3 != 0) {
               return TableSorter.this.ascending ? var3 : -var3;
            } else {
               return 0;
            }
         });
      } finally {
         this.writeUnlock();
      }

   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      this.readLock();

      Object var3;
      try {
         if (rowIndex < this.indexes.length) {
            var3 = this.model.getValueAt(this.convertViewRowToModel(rowIndex), columnIndex);
            return var3;
         }

         var3 = null;
      } finally {
         this.readUnlock();
      }

      return var3;
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      this.readLock();

      boolean var3;
      try {
         if (rowIndex < this.indexes.length && rowIndex >= 0) {
            var3 = this.model.isCellEditable(this.convertViewRowToModel(rowIndex), columnIndex);
            return var3;
         }

         var3 = false;
      } finally {
         this.readUnlock();
      }

      return var3;
   }

   public int convertViewRowToModel(int var1) {
      this.readLock();

      int var2;
      try {
         if (var1 >= this.indexes.length || var1 < 0) {
            byte var6 = -1;
            return var6;
         }

         var2 = this.indexes[var1];
      } finally {
         this.readUnlock();
      }

      return var2;
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.writeLock();

      try {
         if (rowIndex >= this.indexes.length) {
            return;
         }

         this.model.setValueAt(aValue, this.convertViewRowToModel(rowIndex), columnIndex);
      } finally {
         this.writeUnlock();
      }

   }

   public void sortByColumn(int var1) {
      this.sortByColumn(var1, true);
   }

   public void sortByColumn(int var1, boolean var2) {
      this.writeLock();

      try {
         this.ascending = var2;
         this.sortingColumn = var1;
         this.sort(this);
      } finally {
         this.writeUnlock();
      }

      super.tableChanged(new TableModelEvent(this));
   }

   public void addMouseListenerToHeaderInTable(final JTable var1) {
      var1.setColumnSelectionAllowed(false);
      MouseAdapter var4 = new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1x) {
            if (var1x.getClickCount() == 1) {
               TableColumnModel var2 = var1.getColumnModel();
               int var3 = var1.columnAtPoint(var1x.getPoint());
               if (var3 != -1) {
                  TableSorter.this.setSortingColumn(var2.getColumn(var3), var1, TableSorter.this);
               }
            }
         }
      };
      JTableHeader var5 = var1.getTableHeader();
      var5.addMouseListener(var4);
   }

   void setSortingColumn(TableColumn var1, JTable var2, TableSorter var3) {
      if (this.storedColumn != null) {
         this.storedColumn.setHeaderRenderer((TableCellRenderer)null);
      }

      if (var1 == null) {
         this.sortingColumn = -1;
      } else {
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
         var3.sortByColumn(this.storedColumn.getModelIndex(), this.ascending);
      }
   }

   public void setNoSort() {
      this.writeLock();

      try {
         this.setSortingColumn((TableColumn)null, (JTable)null, (TableSorter)null);
         this.reallocateIndexes();
      } finally {
         this.writeUnlock();
      }

      super.tableChanged(new TableModelEvent(this));
   }

   public void setComparator(Comparator<Object> var1) {
      this.writeLock();

      try {
         this.compareBy = var1;
         this.sort((Object)null);
      } finally {
         this.writeUnlock();
      }

      super.tableChanged(new TableModelEvent(this));
   }
}
