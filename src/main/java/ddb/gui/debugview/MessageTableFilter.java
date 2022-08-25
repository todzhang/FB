package ddb.gui.debugview;

import ddb.util.tablefilter.DefaultTableFilter;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

public class MessageTableFilter extends DefaultTableFilter {
   private List<String> sections = new Vector();
   private List<Integer> threads = new Vector();
   int[] rowTranslation = new int[0];
   int size = 0;

   public void addString(String newString) {
      synchronized(this) {
         if (this.sections.contains(newString)) {
            return;
         }

         this.sections.add(newString);
      }

      this.update();
   }

   public void removeString(String oldString) {
      synchronized(this) {
         this.sections.remove(oldString);
      }

      this.update();
   }

   public int getRowCount() {
      synchronized(this) {
         return this.size;
      }
   }

   @Override
   public int translateViewLocationToModelRow(int rowIndex, int columnIndex) {
      synchronized(this) {
         return this.rowTranslation[rowIndex];
      }
   }

   private void update() {
      synchronized(this) {
         this.size = 0;
         this.rowTranslation = new int[this.model.getRowCount()];
         int i = 0;

         while(true) {
            if (i >= this.rowTranslation.length) {
               break;
            }

            String sec = this.model.getValueAt(i, OutputMessageColumns.SECTION.ordinal()).toString();
            if (this.sections.contains(sec)) {
               this.rowTranslation[this.size++] = i;
            } else {
               Object o = this.model.getValueAt(i, OutputMessageColumns.THREAD.ordinal());
               if (this.threads.contains(o)) {
                  this.rowTranslation[this.size++] = i;
               }
            }

            ++i;
         }
      }

      this.fireTableDataChanged();
   }

   @Override
   public void setModel(TableModel tableModel) {
      super.setModel(tableModel);
      this.update();
   }

   @Override
   public void tableChanged(TableModelEvent e) {
      if (e.getFirstRow() == -1 && e.getLastRow() == -1 && e.getColumn() == -1) {
         this.fireTableStructureChanged();
      } else {
         this.update();
      }

   }

   public void setThreads(List<Integer> newThreads) {
      this.threads = newThreads;
      this.update();
   }
}
