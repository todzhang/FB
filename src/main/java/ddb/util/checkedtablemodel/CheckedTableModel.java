package ddb.util.checkedtablemodel;

import ddb.util.AbstractEnumeratedTableModel;
import java.awt.Font;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class CheckedTableModel<T> extends AbstractEnumeratedTableModel<CheckedTableColumns> {
   List<DataEntry<T>> data = new Vector();
   final Comparator<T> comp;
   CheckedTableSelection<T> selection = null;
   Font defaultFont = null;
   private final Comparator<DataEntry<T>> DataObjectComp = new Comparator<DataEntry<T>>() {
      @Override
      public int compare(DataEntry<T> var1, DataEntry<T> var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 == null) {
            return 1;
         } else {
            return var2 == null ? -1 : CheckedTableModel.this.comp.compare(var1.getTag(), var2.getTag());
         }
      }
   };

   public CheckedTableModel(CheckedTableSelection<T> var1, Comparator<T> var2) {
      super(CheckedTableColumns.class);
      if (var2 == null) {
         throw new NullPointerException("Comparator<Object> cannot be null");
      } else {
         this.selection = var1;
         this.comp = var2;
      }
   }

   public int getRowCount() {
      return this.data.size();
   }

   public Object getValueAt(int i, CheckedTableColumns e) {
      switch(e) {
      case SELECTION:
         return ((DataEntry)this.data.get(i)).isSelected();
      case CAPTION:
         return ((DataEntry)this.data.get(i)).getCaption();
      case TAG:
         return ((DataEntry)this.data.get(i)).getTag();
      case OBJECT:
         return this.data.get(i);
      default:
         return null;
      }
   }

   @Override
   public boolean isCellEditable(int i, CheckedTableColumns e) {
      return e.equals(CheckedTableColumns.SELECTION);
   }

   @Override
   public void setValueAt(Object o, int i, CheckedTableColumns e) {
      if (CheckedTableColumns.SELECTION.equals(e)) {
         Object var4;
         synchronized(this) {
            if (((DataEntry)this.data.get(i)).isSelected().equals(o)) {
               return;
            }

            var4 = ((DataEntry)this.data.get(i)).getTag();
            ((DataEntry)this.data.get(i)).setSelected((Boolean)Boolean.class.cast(o));
         }

         this.fireTableCellUpdated(i, e);
         this.selection.selected((T) var4, (Boolean)Boolean.class.cast(o));
      }
   }

   @Override
   public Class<?> getColumnClass(CheckedTableColumns e) {
      switch(e) {
      case SELECTION:
         return Boolean.class;
      case CAPTION:
         return String.class;
      case TAG:
         return Object.class;
      default:
         return Object.class;
      }
   }

   @Override
   public int getColumnCount() {
      return 1;
   }

   @Override
   public String getColumnName(CheckedTableColumns e) {
      switch(e) {
      case SELECTION:
         return e.name();
      default:
         return "";
      }
   }

   private int getIndex(T var1) {
      return Collections.binarySearch(data, new DataEntry<T>(false, "", "", var1), DataObjectComp);
   }

   public boolean contains(T var1) {
      synchronized(this) {
         return this.getIndex(var1) >= 0;
      }
   }

   public void deleteElement(T var1) {
      if (var1 != null) {
         boolean var2 = true;
         int var6;
         synchronized(this) {
            var6 = this.getIndex(var1);
            if (var6 < 0) {
               return;
            }

            this.data.remove(var6);
         }

         if (var6 != -1) {
            this.fireTableRowsDeleted(var6, var6);
         }

      }
   }

   public boolean addElement(String var1, String var2, T var3, boolean var4) {
      if (var3 == null) {
         return false;
      } else {
         boolean var5 = true;
         int var10;
         synchronized(this) {
            var10 = this.getIndex(var3);
            if (var10 >= 0) {
               return false;
            }

            ++var10;
            var10 = -var10;
            DataEntry var7 = new DataEntry(var4, var1, var2, var3);
            this.data.add(var10, var7);
         }

         this.fireTableRowsInserted(var10, var10);
         return true;
      }
   }

   public void hide(Object var1) {
      for(int var2 = 0; var2 < this.getRowCount(); ++var2) {
         if (this.getValueAt(var2, CheckedTableColumns.TAG) == var1) {
            this.setValueAt(Boolean.FALSE, var2, CheckedTableColumns.SELECTION.ordinal());
         }
      }

   }

   public void showOnly(Object var1) {
      for(int var2 = 0; var2 < this.getRowCount(); ++var2) {
         if (this.getValueAt(var2, CheckedTableColumns.TAG) == var1) {
            this.setValueAt(Boolean.TRUE, var2, CheckedTableColumns.SELECTION.ordinal());
         } else {
            this.setValueAt(Boolean.FALSE, var2, CheckedTableColumns.SELECTION.ordinal());
         }
      }

   }

   public void showAll() {
      for(int var1 = 0; var1 < this.getRowCount(); ++var1) {
         this.setValueAt(Boolean.TRUE, var1, CheckedTableColumns.SELECTION.ordinal());
      }

   }

   public void hideAll() {
      for(int var1 = 0; var1 < this.getRowCount(); ++var1) {
         this.setValueAt(Boolean.FALSE, var1, CheckedTableColumns.SELECTION.ordinal());
      }

   }
}
