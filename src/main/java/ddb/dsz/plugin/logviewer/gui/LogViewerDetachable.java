package ddb.dsz.plugin.logviewer.gui;

import ddb.detach.AbstractTabbable;
import ddb.detach.Tabbable;
import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class LogViewerDetachable extends AbstractTabbable implements Tabbable {
   protected JPanel display = new JPanel();

   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }

   protected LogViewerDetachable() {
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

   public void increaseFontSize() {
   }

   public void decreaseFontSize() {
   }
}
