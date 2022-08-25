package ddb.actions.history;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class HistoryAction extends AbstractAction {
   private HistoryDirection direction;
   private HistoryListener listener;

   public HistoryAction(HistoryListener var1, HistoryDirection var2) {
      this.listener = var1;
      this.direction = var2;
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      this.listener.historyActionPerformed(this.direction);
   }
}
