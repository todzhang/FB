package ddb.dsz.plugin;

import javax.swing.JComponent;

public abstract class NoHostAbstractPlugin extends AbstractPlugin {
   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }
}
