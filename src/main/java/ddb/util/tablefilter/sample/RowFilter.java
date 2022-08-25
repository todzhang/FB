package ddb.util.tablefilter.sample;

import ddb.util.UtilityConstants;
import ddb.util.checkedtablemodel.FilterWatcher;
import ddb.util.tablefilter.DefaultTableFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import org.apache.commons.collections.Predicate;

public abstract class RowFilter extends DefaultTableFilter implements FilterWatcher {
   private static ExecutorService exec = UtilityConstants.createExecutorService("RowFilter");
   List<Integer> rowTranslation;
   Predicate filter;
   private ReadWriteLock access;
   protected Runnable Update;

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

   public RowFilter() {
      this.rowTranslation = new ArrayList();
      this.access = UtilityConstants.createReadWriteLock();
      this.Update = new Runnable() {
         boolean bUpdating = false;
         boolean bRunAgain = false;

         @Override
         public void run() {
            synchronized(this) {
               if (this.bUpdating) {
                  this.bRunAgain = true;
                  return;
               }

               this.bUpdating = true;
               this.bRunAgain = false;
            }

            boolean var16 = false;

            try {
               var16 = true;
               Integer[] var1 = null;
               int var2 = 0;
               RowFilter.this.readLock();

               try {
                  var1 = new Integer[RowFilter.this.model.getRowCount()];

                  for(int var3 = 0; var3 < RowFilter.this.model.getRowCount(); ++var3) {
                     if (RowFilter.this.filter == null || RowFilter.this.show(var3)) {
                        var1[var2++] = var3;
                     }
                  }
               } finally {
                  RowFilter.this.readUnlock();
               }

               RowFilter.this.writeLock();

               try {
                  RowFilter.this.rowTranslation = Arrays.asList(var1).subList(0, var2);
               } finally {
                  RowFilter.this.writeUnlock();
               }

               RowFilter.this.fireTableDataChanged();
               var16 = false;
            } finally {
               if (var16) {
                  synchronized(this) {
                     this.bUpdating = false;
                     if (this.bRunAgain) {
                        RowFilter.exec.submit(this);
                     }

                  }
               }
            }

            synchronized(this) {
               this.bUpdating = false;
               if (this.bRunAgain) {
                  RowFilter.exec.submit(this);
               }

            }
         }
      };
   }

   public RowFilter(Predicate filter) {
      this();
      this.filter = filter;
   }

   public void setFilter(Predicate filter) {
      this.writeLock();

      try {
         this.filter = filter;
      } finally {
         this.writeUnlock();
      }

      exec.submit(this.Update);
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

   @Override
   public void setModel(TableModel tableModel) {
      this.writeLock();

      try {
         super.setModel(tableModel);
      } finally {
         this.writeUnlock();
      }

      exec.submit(this.Update);
   }

   @Override
   public void tableChanged(TableModelEvent tableModelEvent) {
      if (tableModelEvent.getFirstRow() == -1 && tableModelEvent.getLastRow() == -1 && tableModelEvent.getColumn() == -1) {
         this.fireTableStructureChanged();
      } else {
         try {
            if (tableModelEvent.getLastRow() > this.model.getRowCount() || tableModelEvent.getFirstRow() == -1) {
               exec.submit(this.Update);
               return;
            }

            switch(tableModelEvent.getType()) {
            case -1:
               exec.submit(this.Update);
               break;
            case 0:
               for(int var2 = tableModelEvent.getLastRow(); var2 <= tableModelEvent.getFirstRow(); ++var2) {
                  this.readLock();

                  try {
                     int var3 = this.translateModelRowToViewRow(var2);
                     if (var3 != -1) {
                        this.fireTableRowsUpdated(var3, var3);
                     }
                  } finally {
                     this.readUnlock();
                  }
               }

               return;
            case 1:
               exec.submit(this.Update);
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }

   }

   protected int translateModelRowToViewRow(int row) {
      this.readLock();

      try {
         for(int var2 = 0; var2 < this.rowTranslation.size(); ++var2) {
            if (this.rowTranslation.get(var2) == row) {
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

   protected int lastRowBefore(int var1) {
      this.readLock();

      int var2;
      try {
         for(var2 = 0; var2 < this.rowTranslation.size(); ++var2) {
            if ((Integer)this.rowTranslation.get(var2) >= var1) {
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

   @Override
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

   @Override
   public void predicateChanged() {
      exec.submit(this.Update);
   }

   protected abstract boolean show(int var1);
}
