package ddb.util.tablefilter;

import ddb.util.UtilityConstants;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.collections.Predicate;

public class FilteredTableModel extends AbstractTableModel implements TableModel, TableModelListener {
   ExecutorService exec = UtilityConstants.createSingleThreadExecutorService("RowFilter");
   List<Integer> rowTranslation = new ArrayList();
   TableModel child = null;
   List<FilteredTableModel.FilterRecord> filters = new ArrayList();
   private ReadWriteLock access = UtilityConstants.createReadWriteLock();
   private boolean bUpdating = false;
   private boolean bRunAgain = false;
   protected final Runnable Update = new Runnable() {
      @Override
      public void run() {
         boolean var22 = false;

         try {
            var22 = true;
            Integer[] var1 = null;
            int var2 = 0;
            FilteredTableModel.this.readLock();

            int var4;
            try {
               var1 = new Integer[FilteredTableModel.this.child.getRowCount()];
               int var3 = var1.length / 10;

               for(var4 = 0; var4 < FilteredTableModel.this.child.getRowCount(); ++var4) {
                  if (FilteredTableModel.this.show(var4)) {
                     var1[var2++] = var4;
                  }

                  try {
                     if (var4 % var3 == 0) {
                        Thread.sleep(1L);
                     }
                  } catch (Exception var43) {
                  }

                  Thread.yield();
               }
            } finally {
               FilteredTableModel.this.readUnlock();
            }

            ArrayList var47 = new ArrayList();
            var4 = 0;
            int var5 = 0;
            FilteredTableModel.this.writeLock();

            int var49;
            try {
               List var6 = Arrays.asList(var1).subList(0, var2);
               boolean var7 = false;
               if (var6.size() > FilteredTableModel.this.rowTranslation.size()) {
                  var49 = FilteredTableModel.this.rowTranslation.size();
                  var4 = var6.size() - FilteredTableModel.this.rowTranslation.size();
               } else if (var6.size() < FilteredTableModel.this.rowTranslation.size()) {
                  var49 = var6.size();
                  var5 = FilteredTableModel.this.rowTranslation.size() - var6.size();
               } else {
                  var49 = var6.size();
               }

               for(int var8 = 0; var8 < var49; ++var8) {
                  if (FilteredTableModel.this.rowTranslation.get(var8) != var6.get(var8)) {
                     var47.add(var8);
                  }
               }

               FilteredTableModel.this.rowTranslation = var6;
            } finally {
               FilteredTableModel.this.writeUnlock();
            }

            int var48 = Integer.MIN_VALUE;
            var49 = Integer.MIN_VALUE;
            Iterator var50 = var47.iterator();

            while(var50.hasNext()) {
               Integer var9 = (Integer)var50.next();
               if (var49 + 1 == var9) {
                  var49 = var9;
               } else if (var48 < 0) {
                  var48 = var49 = var9;
               } else {
                  EventQueue.invokeLater(FilteredTableModel.this.new FireTableRowsUpdated(var48, var49));
                  var49 = Integer.MIN_VALUE;
                  var48 = Integer.MIN_VALUE;
               }
            }

            if (var48 >= 0) {
               EventQueue.invokeLater(FilteredTableModel.this.new FireTableRowsUpdated(var48, var49));
            }

            if (var4 > 0) {
               EventQueue.invokeLater(FilteredTableModel.this.new FireTableRowsInserted(FilteredTableModel.this.rowTranslation.size() - var4, FilteredTableModel.this.rowTranslation.size() - 1));
            }

            if (var5 > 0) {
               EventQueue.invokeLater(FilteredTableModel.this.new FireTableRowsInserted(FilteredTableModel.this.rowTranslation.size(), FilteredTableModel.this.rowTranslation.size() + var5));
               var22 = false;
            } else {
               var22 = false;
            }
         } finally {
            if (var22) {
               try {
                  Thread.sleep(100L);
               } catch (Exception var40) {
               }

               synchronized(this) {
                  if (FilteredTableModel.this.bRunAgain) {
                     FilteredTableModel.this.bRunAgain = false;
                     FilteredTableModel.this.exec.submit(this);
                  } else {
                     FilteredTableModel.this.bUpdating = false;
                  }

               }
            }
         }

         try {
            Thread.sleep(100L);
         } catch (Exception var42) {
         }

         synchronized(this) {
            if (FilteredTableModel.this.bRunAgain) {
               FilteredTableModel.this.bRunAgain = false;
               FilteredTableModel.this.exec.submit(this);
            } else {
               FilteredTableModel.this.bUpdating = false;
            }

         }
      }
   };

   protected void readLock() {
      this.access.readLock().lock();
   }

   protected void writeLock() {
      this.access.writeLock().lock();
   }

   protected void readUnlock() {
      this.access.readLock().unlock();
   }

   protected void writeUnlock() {
      this.access.writeLock().unlock();
   }

   public FilteredTableModel(TableModel child) {
      this.child = child;
      if (child != null) {
         child.addTableModelListener(this);
      }

   }

   @Override
   public int getRowCount() {
      this.readLock();

      int var1;
      try {
         var1 = this.rowTranslation.size();
      } finally {
         this.readUnlock();
      }

      return var1;
   }

   public void setModel(TableModel child) {
      this.writeLock();

      try {
         if (this.child != null) {
            this.child.removeTableModelListener(this);
         }

         this.child = child;
         if (this.child != null) {
            this.child.addTableModelListener(this);
         }
      } finally {
         this.writeUnlock();
      }

      this.submitUpdate();
   }

   public void tableChanged(TableModelEvent tableModelEvent) {
      if (tableModelEvent.getFirstRow() == -1 && tableModelEvent.getLastRow() == -1 && tableModelEvent.getColumn() == -1) {
         EventQueue.invokeLater(new FilteredTableModel.FireTableStructureChanged());
      } else {
         try {
            if (tableModelEvent.getLastRow() > this.child.getRowCount() || tableModelEvent.getFirstRow() == -1) {
               this.submitUpdate();
               return;
            }

            switch(tableModelEvent.getType()) {
            case -1:
               this.submitUpdate();
               break;
            case 0:
               for(int var2 = tableModelEvent.getLastRow(); var2 <= tableModelEvent.getFirstRow(); ++var2) {
                  this.readLock();

                  try {
                     int var3 = this.translateModelRowToViewRow(var2);
                     if (var3 != -1) {
                        EventQueue.invokeLater(new FilteredTableModel.FireTableRowsUpdated(var3, var3));
                     } else if (this.show(var2)) {
                        this.submitUpdate();
                        return;
                     }
                  } finally {
                     this.readUnlock();
                  }
               }

               return;
            case 1:
               this.submitUpdate();
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }

   }

   protected int translateModelRowToViewRow(int rowIndex) {
      this.readLock();

      try {
         for(int var2 = 0; var2 < this.rowTranslation.size(); ++var2) {
            if ((Integer)this.rowTranslation.get(var2) == rowIndex) {
               int var3 = var2;
               return var3;
            }
         }

         byte var7 = -1;
         return var7;
      } finally {
         this.readUnlock();
      }
   }

   protected int lastRowBefore(int rowIndex) {
      this.readLock();

      int var2;
      try {
         for(var2 = 0; var2 < this.rowTranslation.size(); ++var2) {
            if ((Integer)this.rowTranslation.get(var2) >= rowIndex) {
               int var3 = var2;
               return var3;
            }
         }

         var2 = this.rowTranslation.size();
      } finally {
         this.readUnlock();
      }

      return var2;
   }

   public int translateViewLocationToModelRow(int rowIndex, int columnIndex) {
      if (rowIndex <= -1) {
         return -1;
      } else {
         this.readLock();

         int var3;
         try {
            if (this.rowTranslation.size() <= rowIndex) {
               byte var7 = -1;
               return var7;
            }

            var3 = (Integer)this.rowTranslation.get(rowIndex);
         } finally {
            this.readUnlock();
         }

         return var3;
      }
   }

   public void filterChanged() {
      this.submitUpdate();
   }

   private void submitUpdate() {
      synchronized(this.Update) {
         if (this.bUpdating) {
            this.bRunAgain = true;
         } else {
            this.bUpdating = true;
            this.exec.submit(this.Update);
         }
      }
   }

   protected boolean show(int var1) {
      try {
         Iterator var2 = this.filters.iterator();

         while(var2.hasNext()) {
            FilteredTableModel.FilterRecord var3 = (FilteredTableModel.FilterRecord)var2.next();
            if (!this.show(var1, var3)) {
               return false;
            }
         }
      } catch (ClassCastException var4) {
         var4.printStackTrace();
      }

      return true;
   }

   private boolean show(int var1, FilteredTableModel.FilterRecord var2) {
      int var3 = 0;
      int[] var4 = var2.columns;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4[var6];
         if (var2.filter.evaluate(this.child.getValueAt(var1, var7))) {
            ++var3;
         }
      }

      switch(var2.match) {
      case All:
         return var3 == var2.columns.length;
      case Single:
         return var3 > 0;
      default:
         return false;
      }
   }

   public void addFilter(Predicate predicate, int... var2) {
      this.addFilter(predicate, FilteredTableModel.MultipleColumnMatch.Single, var2);
   }

   public void addFilter(Predicate predicate, Enum<?>... var2) {
      this.addFilter(predicate, FilteredTableModel.MultipleColumnMatch.Single, var2);
   }

   public void addFilter(Predicate predicate, FilteredTableModel.MultipleColumnMatch var2, int... var3) {
      if (predicate != null && var3.length != 0) {
         FilteredTableModel.FilterRecord var4 = new FilteredTableModel.FilterRecord();
         var4.columns = var3;
         var4.filter = predicate;
         var4.match = var2;
         this.writeLock();

         try {
            this.filters.add(var4);
         } finally {
            this.writeUnlock();
         }

         this.submitUpdate();
      }
   }

   public void addFilter(Predicate predicate, FilteredTableModel.MultipleColumnMatch var2, Enum<?>... var3) {
      int[] var4 = new int[var3.length];

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4[var5] = var3[var5].ordinal();
      }

      this.addFilter(predicate, var2, var4);
   }

   @Override
   public int getColumnCount() {
      this.readLock();

      int var1;
      try {
         var1 = this.child.getColumnCount();
      } finally {
         this.readUnlock();
      }

      return var1;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      this.readLock();

      Object var3;
      try {
         var3 = this.child.getValueAt(this.translateViewLocationToModelRow(rowIndex, columnIndex), columnIndex);
      } finally {
         this.readUnlock();
      }

      return var3;
   }

   @Override
   public String toString() {
      return String.format("Filter (%s)", this.child.toString());
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.readLock();

      try {
         this.child.setValueAt(aValue, this.translateViewLocationToModelRow(rowIndex, columnIndex), columnIndex);
      } finally {
         this.readUnlock();
      }

   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      this.readLock();

      boolean var3;
      try {
         var3 = this.child.isCellEditable(this.translateViewLocationToModelRow(rowIndex, columnIndex), columnIndex);
      } finally {
         this.readUnlock();
      }

      return var3;
   }

   @Override
   public String getColumnName(int column) {
      return this.child.getColumnName(column);
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return this.child.getColumnClass(columnIndex);
   }

   protected class FireTableCellUpdated implements Runnable {
      int i;
      int j;

      public FireTableCellUpdated(int var2, int var3) {
         this.i = var2;
         this.j = var3;
      }

      public void run() {
         FilteredTableModel.this.fireTableCellUpdated(this.i, this.j);
      }
   }

   protected class FireTableStructureChanged implements Runnable {
      public FireTableStructureChanged() {
      }

      @Override
      public void run() {
         FilteredTableModel.this.fireTableStructureChanged();
      }
   }

   protected class FireTableDataChanged implements Runnable {
      public FireTableDataChanged() {
      }

      @Override
      public void run() {
         FilteredTableModel.this.fireTableDataChanged();
      }
   }

   protected class FireTableRowsDeleted implements Runnable {
      int i;
      int j;

      public FireTableRowsDeleted(int var2, int var3) {
         this.i = var2;
         this.j = var3;
      }

      @Override
      public void run() {
         FilteredTableModel.this.fireTableRowsDeleted(this.i, this.j);
      }
   }

   protected class FireTableRowsInserted implements Runnable {
      int i;
      int j;

      public FireTableRowsInserted(int var2, int var3) {
         this.i = var2;
         this.j = var3;
      }

      @Override
      public void run() {
         FilteredTableModel.this.fireTableRowsInserted(this.i, this.j);
      }
   }

   protected class FireTableRowsUpdated implements Runnable {
      int i;
      int j;

      public FireTableRowsUpdated(int var2, int var3) {
         this.i = var2;
         this.j = var3;
      }

      @Override
      public void run() {
         FilteredTableModel.this.fireTableRowsUpdated(this.i, this.j);
      }
   }

   private class FilterRecord {
      int[] columns;
      Predicate filter;
      FilteredTableModel.MultipleColumnMatch match;

      private FilterRecord() {
         this.match = FilteredTableModel.MultipleColumnMatch.All;
      }

      @Override
      public String toString() {
         String var1 = "";
         if (this.columns.length > 1) {
            StringBuilder var2 = new StringBuilder();
            int[] var3 = this.columns;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = var3[var5];
               if (var2.length() > 0) {
                  var2.append(", ");
               }

               var2.append(var6);
            }

            var1 = var2.toString();
         } else {
            var1 = String.format("%d", this.columns[0]);
         }

         return String.format("Filter (%s[%s])", this.filter.toString(), var1);
      }

      // $FF: synthetic method
      FilterRecord(Object var2) {
         this();
      }
   }

   public enum MultipleColumnMatch {
      Single,
      All;
   }
}
