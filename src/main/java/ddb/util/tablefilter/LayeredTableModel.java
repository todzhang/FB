package ddb.util.tablefilter;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/** @deprecated */
@Deprecated
public class LayeredTableModel extends AbstractTableModel implements TableFilterModel {
   List<TableFilterModel> filters;
   TableModel model;
   JTable table;
   ReentrantReadWriteLock lock;
   Lock read;
   Lock write;
   TableModelListener listener;

   public LayeredTableModel() {
      this.lock = new ReentrantReadWriteLock();
      this.read = this.lock.readLock();
      this.write = this.lock.writeLock();
      this.listener = new TableModelListener() {
         @Override
         public void tableChanged(TableModelEvent var1) {
            LayeredTableModel.this.fireTableChanged(new TableModelEvent(LayeredTableModel.this, var1.getFirstRow(), var1.getLastRow(), var1.getColumn(), var1.getType()));
         }
      };
      this.filters = new Vector();
   }

   public LayeredTableModel(TableModel var1) {
      this();
      this.setModel(var1);
   }

   public LayeredTableModel(TableModel var1, Collection<TableFilterModel> var2) {
      this(var1);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         TableFilterModel var4 = (TableFilterModel)var3.next();
         this.addFilter(var4);
      }

   }

   public LayeredTableModel(TableModel var1, TableFilterModel[] var2) {
      this(var1);
      TableFilterModel[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TableFilterModel var6 = var3[var5];
         this.addFilter(var6);
      }

   }

   public LayeredTableModel(Collection<TableFilterModel> var1) {
      this();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         TableFilterModel var3 = (TableFilterModel)var2.next();
         this.addFilter(var3);
      }

   }

   public LayeredTableModel(TableFilterModel[] var1) {
      this();
      TableFilterModel[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TableFilterModel var5 = var2[var4];
         this.addFilter(var5);
      }

   }

   @Override
   public void setModel(TableModel tableModel) {
      this.write.lock();

      try {
         if (this.model != null) {
            if (this.filters.size() == 0) {
               this.model.removeTableModelListener(this.listener);
            } else {
               this.model.removeTableModelListener((TableModelListener)this.filters.get(this.filters.size() - 1));
            }
         }

         this.model = tableModel;
         if (this.filters.size() == 0) {
            this.model.addTableModelListener(this.listener);
         } else {
            ((TableFilterModel)this.filters.get(this.filters.size() - 1)).setModel(this.model);
         }
      } finally {
         this.write.unlock();
      }

      this.fireTableStructureChanged();
   }

   private void addFilterHelper(int var1, TableFilterModel var2) {
      this.write.lock();

      try {
         if (var1 == 0) {
            if (this.filters.size() == 0) {
               if (this.model != null) {
                  this.model.removeTableModelListener(this.listener);
               }
            } else {
               ((TableFilterModel)this.filters.get(0)).removeTableModelListener(this.listener);
            }

            var2.addTableModelListener(this.listener);
         }

         this.filters.add(var1, var2);
         if (var1 != 0) {
            ((TableFilterModel)this.filters.get(var1 - 1)).setModel(var2);
         }

         if (var1 + 1 != this.filters.size()) {
            var2.setModel((TableModel)this.filters.get(var1 + 1));
         } else {
            var2.setModel(this.model);
         }
      } finally {
         this.write.unlock();
      }

   }

   public void addFilter(TableFilterModel var1) {
      this.addFilterHelper(this.filters.size(), var1);
   }

   public void addFilter(int var1, TableFilterModel var2) {
      this.addFilterHelper(var1, var2);
   }

   public void addAllFilters(Collection<TableFilterModel> var1) {
      this.write.lock();

      try {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            TableFilterModel var3 = (TableFilterModel)var2.next();
            this.addFilterHelper(var1.size(), var3);
         }
      } finally {
         this.write.unlock();
      }

   }

   public void addAllFilters(int var1, Collection<TableFilterModel> var2) {
      this.write.lock();

      try {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            TableFilterModel var4 = (TableFilterModel)var3.next();
            this.addFilterHelper(var1++, var4);
         }
      } finally {
         this.write.unlock();
      }

   }

   public void removeFilter(TableFilterModel var1) {
      this.write.lock();

      try {
         if (this.unregisterFilter(this.filters.indexOf(var1)) == null) {
            return;
         }
      } finally {
         this.write.unlock();
      }

      this.fireTableStructureChanged();
   }

   public TableFilterModel removeFilter(int var1) {
      TableFilterModel var2 = this.unregisterFilter(var1);
      if (var2 != null) {
         this.fireTableStructureChanged();
      }

      return var2;
   }

   private TableFilterModel unregisterFilter(int var1) {
      this.write.lock();

      TableFilterModel var3;
      try {
         TableFilterModel var2;
         if (var1 == -1) {
            var2 = null;
            return var2;
         }

         if (var1 >= this.filters.size()) {
            var2 = null;
            return var2;
         }

         var2 = (TableFilterModel)this.filters.remove(var1);
         var2.removeTableConfiguration(this.table);
         if (var1 == 0) {
            var2.removeTableModelListener(this.listener);
            if (var1 == this.filters.size()) {
               this.model.addTableModelListener(this.listener);
            } else {
               ((TableFilterModel)this.filters.get(0)).addTableModelListener(this.listener);
            }
         }

         if (var1 == this.filters.size()) {
            ((TableFilterModel)this.filters.get(var1 - 1)).setModel(this.model);
         } else {
            ((TableFilterModel)this.filters.get(var1 - 1)).setModel((TableModel)this.filters.get(var1));
         }

         var3 = var2;
      } finally {
         this.write.unlock();
      }

      return var3;
   }

   @Override
   public Class<?> getColumnClass(int var1) {
      this.read.lock();

      Class var2;
      try {
         if (this.filters.size() != 0) {
            var2 = ((TableFilterModel)this.filters.get(0)).getColumnClass(var1);
            return var2;
         }

         var2 = this.model.getColumnClass(var1);
      } finally {
         this.read.unlock();
      }

      return var2;
   }

   @Override
   public int getColumnCount() {
      this.read.lock();

      int var1;
      try {
         if (this.filters.size() == 0) {
            var1 = this.model.getColumnCount();
            return var1;
         }

         var1 = ((TableFilterModel)this.filters.get(0)).getColumnCount();
      } finally {
         this.read.unlock();
      }

      return var1;
   }

   @Override
   public int getRowCount() {
      this.read.lock();

      int var1;
      try {
         if (this.filters.size() != 0) {
            var1 = ((TableFilterModel)this.filters.get(0)).getRowCount();
            return var1;
         }

         var1 = this.model.getRowCount();
      } finally {
         this.read.unlock();
      }

      return var1;
   }

   @Override
   public Object getValueAt(int var1, int var2) {
      this.read.lock();

      Object var3;
      try {
         if (this.filters.size() == 0) {
            var3 = this.model.getValueAt(var1, var2);
            return var3;
         }

         var3 = ((TableFilterModel)this.filters.get(0)).getValueAt(var1, var2);
      } finally {
         this.read.unlock();
      }

      return var3;
   }

   @Override
   public String getColumnName(int var1) {
      this.read.lock();

      String var2;
      try {
         if (this.filters.size() != 0) {
            var2 = ((TableFilterModel)this.filters.get(0)).getColumnName(var1);
            return var2;
         }

         var2 = this.model.getColumnName(var1);
      } finally {
         this.read.unlock();
      }

      return var2;
   }

   @Override
   public boolean isCellEditable(int var1, int var2) {
      this.read.lock();

      boolean var3;
      try {
         if (this.filters.size() != 0) {
            var3 = ((TableFilterModel)this.filters.get(0)).isCellEditable(var1, var2);
            return var3;
         }

         var3 = this.model.isCellEditable(var1, var2);
      } finally {
         this.read.unlock();
      }

      return var3;
   }

   @Override
   public void setValueAt(Object var1, int var2, int var3) {
      this.write.lock();

      try {
         if (this.filters.size() == 0) {
            this.model.setValueAt(var1, var2, var3);
         } else {
            ((TableFilterModel)this.filters.get(0)).setValueAt(var1, var2, var3);
         }
      } finally {
         this.write.unlock();
      }

   }

   @Override
   public int translateViewLocationToModelColumn(int rowIndex, int var2) {
      return this.translateViewLocationToModelLocation(rowIndex, var2).y;
   }

   @Override
   public int translateViewLocationToModelRow(int rowIndex, int columnIndex) {
      return this.translateViewLocationToModelLocation(rowIndex, columnIndex).x;
   }

   private Point translateViewLocationToModelLocation(int var1, int var2) {
      this.read.lock();

      try {
         Point var3 = new Point(var1, var2);

         TableFilterModel var5;
         for(Iterator var4 = this.filters.iterator(); var4.hasNext(); var3 = new Point(var5.translateViewLocationToModelRow(var3.x, var3.y), var5.translateViewLocationToModelColumn(var3.x, var3.y))) {
            var5 = (TableFilterModel)var4.next();
         }

         Point var9 = var3;
         return var9;
      } finally {
         this.read.unlock();
      }
   }

   @Override
   public void tableChanged(TableModelEvent var1) {
   }

   @Override
   public void applyTableConfiguration(JTable jTable) {
      this.write.lock();

      try {
         if (this.table != jTable) {
            this.removeTableConfiguration(this.table);
         }

         this.table = jTable;
         Iterator var2 = this.filters.iterator();

         while(var2.hasNext()) {
            TableFilterModel var3 = (TableFilterModel)var2.next();
            var3.applyTableConfiguration(this.table);
         }
      } finally {
         this.write.unlock();
      }

   }

   @Override
   public void removeTableConfiguration(JTable jTable) {
      this.write.lock();

      try {
         if (this.table == jTable) {
            this.table = null;
         }

         Iterator var2 = this.filters.iterator();

         while(var2.hasNext()) {
            TableFilterModel var3 = (TableFilterModel)var2.next();
            var3.removeTableConfiguration(jTable);
         }
      } finally {
         this.write.unlock();
      }

   }
}
