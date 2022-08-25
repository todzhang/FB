package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.dsz.core.data.ObjectValue;
import ddb.util.AbstractEnumeratedTableModel;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ValueTableModel extends AbstractEnumeratedTableModel<ValueTableColumns> {
   ObjectValue current;
   List<List<Object>> dataVector = new Vector();

   ValueTableModel() {
      super(ValueTableColumns.class);
   }

   @Override
   public String getColumnName(ValueTableColumns e) {
      return e.getName();
   }

   void setCurrent(ObjectValue newCurrent) {
      this.current = newCurrent;
      this.regenerate();
      this.fireTableDataChanged();
   }

   void regenerate() {
      this.dataVector.clear();
      if (this.current != null) {
         Iterator i$ = this.current.getBooleanNames().iterator();

         String s;
//         Iterator i$;
         while(i$.hasNext()) {
            s = (String)i$.next();
            i$ = this.current.getBooleans(s).iterator();

            while(i$.hasNext()) {
               Boolean b = (Boolean)i$.next();
               this.addRow(Boolean.class.getSimpleName(), s, b);
            }
         }

         i$ = this.current.getIntegerNames().iterator();

         while(i$.hasNext()) {
            s = (String)i$.next();
            i$ = this.current.getIntegers(s).iterator();

            while(i$.hasNext()) {
               Long b = (Long)i$.next();
               this.addRow(Long.class.getSimpleName(), s, b);
            }
         }

         i$ = this.current.getStringNames().iterator();

         while(i$.hasNext()) {
            s = (String)i$.next();
            i$ = this.current.getStrings(s).iterator();

            while(i$.hasNext()) {
               String b = (String)i$.next();
               this.addRow(String.class.getSimpleName(), s, b);
            }
         }

      }
   }

   private void addRow(String type, String key, Object value) {
      List<Object> newRow = new Vector();
      newRow.add(type);
      newRow.add(key);
      newRow.add(value);
      this.dataVector.add(newRow);
   }

   public int getRowCount() {
      return this.dataVector.size();
   }

   public Object getValueAt(int i, ValueTableColumns e) {
      List<Object> row = (List)this.dataVector.get(i);
      return row.get(e.ordinal());
   }
}
