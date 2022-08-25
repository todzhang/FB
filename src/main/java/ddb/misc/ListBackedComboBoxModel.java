package ddb.misc;

import java.util.List;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class ListBackedComboBoxModel implements ComboBoxModel {
   Object selected = null;
   List<?> objects;

   public ListBackedComboBoxModel(List<?> var1) {
      if (var1 == null) {
         this.objects = new Vector();
      } else {
         this.objects = var1;
      }

   }

   public void setSelectedItem(Object var1) {
      this.selected = var1;
   }

   public Object getSelectedItem() {
      return this.selected;
   }

   public int getSize() {
      return this.objects.size();
   }

   public Object getElementAt(int var1) {
      return this.objects.get(var1);
   }

   public void addListDataListener(ListDataListener var1) {
   }

   public void removeListDataListener(ListDataListener var1) {
   }
}
