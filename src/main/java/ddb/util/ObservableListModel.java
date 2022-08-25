package ddb.util;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;

public class ObservableListModel extends DefaultListModel implements Observer {
   @Override
   public void update(Observable o, Object arg) {
      int index = this.indexOf(o);
      if (index != -1) {
         this.fireContentsChanged(this, index, index);
      }
   }

   @Override
   public void add(int index, Object element) {
      if (element instanceof Observable) {
         ((Observable)element).addObserver(this);
         super.add(index, element);
      }
   }

   @Override
   public void addElement(Object obj) {
      if (obj instanceof Observable) {
         ((Observable)obj).addObserver(this);
         super.addElement(obj);
      }
   }

   @Override
   public void copyInto(Object[] anArray) {
      int size = anArray.length;

      int i;
      for(i = 0; i < size; ++i) {
         if (!(anArray[i] instanceof Observable)) {
            return;
         }
      }

      for(i = 0; i < size; ++i) {
         ((Observable)anArray[i]).addObserver(this);
         super.addElement(anArray[i]);
      }

   }

   @Override
   public Object remove(int index) {
      Observable o = (Observable)this.elementAt(index);
      o.deleteObserver(this);
      return super.remove(index);
   }

   @Override
   public void removeAllElements() {
      Enumeration elements = this.elements();

      while(elements.hasMoreElements()) {
         Observable element = (Observable)elements.nextElement();
         element.deleteObserver(this);
      }

      super.removeAllElements();
   }

   @Override
   public boolean removeElement(Object obj) {
      boolean included = super.removeElement(obj);
      if (included) {
         ((Observable)obj).deleteObserver(this);
      }

      return included;
   }

   @Override
   public void removeElementAt(int index) {
      ((Observable)this.elementAt(index)).deleteObserver(this);
      super.removeElementAt(index);
   }

   @Override
   public void removeRange(int fromIndex, int toIndex) {
      for(int i = fromIndex; i <= toIndex; ++i) {
         ((Observable)this.elementAt(i)).deleteObserver(this);
      }

      super.removeRange(fromIndex, toIndex);
   }

   @Override
   public Object set(int index, Object element) {
      Observable obj = (Observable)super.set(index, element);
      ((Observable)element).addObserver(this);
      obj.deleteObserver(this);
      return obj;
   }

   @Override
   public void setElementAt(Object obj, int index) {
      this.set(index, obj);
   }
}
