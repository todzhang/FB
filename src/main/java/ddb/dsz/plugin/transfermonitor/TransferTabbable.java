package ddb.dsz.plugin.transfermonitor;

import ddb.detach.AbstractTabbable;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class TransferTabbable extends AbstractTabbable {
   protected final JPanel display = new JPanel(new BorderLayout());

   public TransferTabbable(String var1) {
      super.setDetachable(true);
      super.setShowButtons(false);
      super.setName(var1);
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return true;
   }

   @Override
   public final JComponent getDisplay() {
      return this.display;
   }

   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }
}
