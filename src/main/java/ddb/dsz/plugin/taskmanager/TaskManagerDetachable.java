package ddb.dsz.plugin.taskmanager;

import ddb.detach.AbstractTabbable;
import ddb.detach.Tabbable;
import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class TaskManagerDetachable extends AbstractTabbable implements Tabbable {
   boolean detached;
   int alignment;
   protected JPanel display = new JPanel();

   protected TaskManagerDetachable() {
      super.registerDisplay(this.display);
      super.setShowButtons(true);
   }

   @Override
   public String getShortDescription() {
      return null;
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }

   @Override
   public boolean isHideable() {
      return false;
   }

   @Override
   public boolean isUnhideable() {
      return true;
   }

   @Override
   public boolean isDetachable() {
      return true;
   }

   @Override
   public JComponent getDefaultElement() {
      return null;
   }
}
