package ddb.dsz.library.console;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class EnterKeyPressAction extends AbstractAction {
   private Console terminal;

   public EnterKeyPressAction(Console console) {
      this.terminal = console;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      this.terminal.processCommandLine();
   }
}
