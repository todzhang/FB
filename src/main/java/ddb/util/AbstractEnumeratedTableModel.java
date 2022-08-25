package ddb.util;

import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public abstract class AbstractEnumeratedTableModel<E extends Enum<?>> extends AbstractTableModel implements TableModel {
   Class<E> columnsType;
   E[] EnumConstants;
   protected AbstractEnumeratedTableModel<E>.PublicObservable updateObservable = new AbstractEnumeratedTableModel.PublicObservable();
   protected AbstractEnumeratedTableModel<E>.PublicObservable insertObservable = new AbstractEnumeratedTableModel.PublicObservable();
   private boolean pendingDataChanged = false;

   public void addUpdateObserver(Observer observer) {
      this.updateObservable.addObserver(observer);
   }

   public void deleteUpdateObserver(Observer observer) {
      this.updateObservable.deleteObserver(observer);
   }

   public void addInsertObserver(Observer observer) {
      this.insertObservable.addObserver(observer);
   }

   public void deleteInsertObserver(Observer observer) {
      this.insertObservable.deleteObserver(observer);
   }

   public AbstractEnumeratedTableModel(Class<E> columnsType) {
      this.columnsType = columnsType;
      this.EnumConstants =columnsType.getEnumConstants();
   }

   @Override
   public int getColumnCount() {
      return this.EnumConstants.length;
   }

   @Override
   public final Object getValueAt(int rowIndex, int columnIndex) {
      return this.getValueAt(rowIndex, this.EnumConstants[columnIndex]);
   }

   public void fireTableCellUpdated(int i, E e) {
      this.fireTableCellUpdated(i, e.ordinal());
   }

   @Override
   public final Class<?> getColumnClass(int columnIndex) {
      return this.getColumnClass(this.EnumConstants[columnIndex]);
   }

   @Override
   public final String getColumnName(int column) {
      return this.getColumnName(this.EnumConstants[column]);
   }

   public String getColumnName(E e) {
      return e.toString();
   }

   public Class<?> getColumnClass(E e) {
      return Object.class;
   }

   public abstract Object getValueAt(int i, E e);

   @Override
   public final boolean isCellEditable(int rowIndex, int columnIndex) {
      return this.isCellEditable(rowIndex, this.EnumConstants[columnIndex]);
   }

   public boolean isCellEditable(int i, E e) {
      return false;
   }

   @Override
   public final void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.setValueAt(aValue, rowIndex, this.EnumConstants[columnIndex]);
   }

   public void setValueAt(Object o, int i, E e) {
   }

   private synchronized boolean isPendingDataChange() {
      return this.pendingDataChanged;
   }

   private synchronized void setPendingDataChange(boolean pendingDataChange) {
      this.pendingDataChanged = pendingDataChange;
   }

   public class FireTableCellUpdated extends AbstractEnumeratedTableModel.FireTableModification {
      int row;
      int column;

      public FireTableCellUpdated(int row, int column) {
         super(AbstractEnumeratedTableModel.TableModificationType.CellUpdated, true);
         this.row = row;
         this.column = column;
      }

      public FireTableCellUpdated(int row, E e) {
         super(AbstractEnumeratedTableModel.TableModificationType.CellUpdated, true);
         this.row = row;
         this.column = e.ordinal();
      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.fireTableCellUpdated(this.row, this.column);
      }
   }

   protected class FireTableStructureChanged extends AbstractEnumeratedTableModel.FireTableModification {
      public FireTableStructureChanged() {
         super(AbstractEnumeratedTableModel.TableModificationType.StructureChanged, true);
      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.fireTableStructureChanged();
      }
   }

   public class FireTableDataChanged extends AbstractEnumeratedTableModel.FireTableModification {
      public FireTableDataChanged() {
         super(AbstractEnumeratedTableModel.TableModificationType.DataChanged, false);
         if (AbstractEnumeratedTableModel.this.isPendingDataChange()) {
            super.voided = true;
         } else {
            AbstractEnumeratedTableModel.this.setPendingDataChange(true);
         }

      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.setPendingDataChange(false);
         AbstractEnumeratedTableModel.this.fireTableDataChanged();
      }
   }

   public class FireTableRowsDeleted extends AbstractEnumeratedTableModel.FireTableModification {
      int i;
      int j;

      public FireTableRowsDeleted(int i, int j) {
         super(AbstractEnumeratedTableModel.TableModificationType.Deleted, true);
         this.i = i;
         this.j = j;
      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.fireTableRowsDeleted(this.i, this.j);
      }
   }

   public class FireTableRowsInserted extends AbstractEnumeratedTableModel.FireTableModification {
      int firstRow;
      int lastRow;

      public FireTableRowsInserted(int firstRow, int lastRow) {
         super(AbstractEnumeratedTableModel.TableModificationType.Inserted, true);
         this.firstRow = firstRow;
         this.lastRow = lastRow;
      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.fireTableRowsInserted(this.firstRow, this.lastRow);
      }
   }

   public class FireTableRowsUpdated extends AbstractEnumeratedTableModel.FireTableModification {
      int firstRow;
      int lastRow;

      public FireTableRowsUpdated(int firstRow, int lastRow) {
         super(AbstractEnumeratedTableModel.TableModificationType.Updated, true);
         this.firstRow = firstRow;
         this.lastRow = lastRow;
      }

      @Override
      protected final void runModification() {
         AbstractEnumeratedTableModel.this.fireTableRowsUpdated(this.firstRow, this.lastRow);
      }

//      public int compareTo(AbstractEnumeratedTableModel<E>.FireTableModification var1) {
//         if (this.getType() != var1.getType()) {
//            return 0;
//         } else {
//            AbstractEnumeratedTableModel.FireTableRowsUpdated var2 = (AbstractEnumeratedTableModel.FireTableRowsUpdated)var1;
//            if (this.firstRow < var2.firstRow) {
//               return -1;
//            } else if (this.firstRow > var2.firstRow) {
//               return 1;
//            } else if (this.lastRow < var2.lastRow) {
//               return -1;
//            } else {
//               return this.lastRow > var2.lastRow ? 1 : 0;
//            }
//         }
//      }
   }

   protected abstract class FireTableModification implements Runnable, Comparable<AbstractEnumeratedTableModel<E>.FireTableModification> {
      private AbstractEnumeratedTableModel.TableModificationType type;
      private final boolean voidable;
      protected boolean voided;

      protected FireTableModification(AbstractEnumeratedTableModel.TableModificationType type, boolean voidable) {
         this.type = type;
         this.voidable = voidable;
         this.voided = voidable && AbstractEnumeratedTableModel.this.isPendingDataChange();
      }

      public AbstractEnumeratedTableModel.TableModificationType getType() {
         return this.type;
      }

      @Override
      public int compareTo(AbstractEnumeratedTableModel<E>.FireTableModification var1) {
         return 0;
      }

      @Override
      public final void run() {
         if ((!this.voidable || !AbstractEnumeratedTableModel.this.isPendingDataChange()) && !this.voided) {
            this.runModification();
         }
      }

      protected abstract void runModification();
   }

   public enum TableModificationType {
      Updated,
      Inserted,
      Deleted,
      CellUpdated,
      DataChanged,
      StructureChanged;
   }

   protected class PublicObservable extends Observable {
      @Override
      public void setChanged() {
         super.setChanged();
      }
   }
}
