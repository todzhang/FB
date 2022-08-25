package ddb.actions.tabnav;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class NavigationAction extends AbstractAction {
   private NavigationDirection direction;
   private NavigationListener listener;

   public NavigationAction(NavigationListener var1, NavigationDirection var2) {
      this.listener = var1;
      this.direction = var2;
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      this.listener.navigationActionPerformed(this.direction, var1);
   }
}
