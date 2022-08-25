package ddb.dsz.library.console.actions;

import ddb.dsz.library.console.Console;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class DecreaseFontSize extends AbstractAction {
   private final Console console;

   public DecreaseFontSize(Console console, String name) {
      super(name);
      this.console = console;
   }

   @Override
   public void actionPerformed(ActionEvent actionEvent) {
      this.console.decreaseFontSize();
   }
}
