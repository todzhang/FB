package ds.gui;

import ds.core.DSClient;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class QuitAction extends AbstractAction {
   DSClient parent;

   public QuitAction(DSClient parent) {
      this.parent = parent;
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      this.parent.requestShutdown();
   }
}
