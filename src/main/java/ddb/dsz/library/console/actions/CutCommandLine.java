package ddb.dsz.library.console.actions;

import ddb.dsz.library.console.Console;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class CutCommandLine extends AbstractAction {
   private final Console console;

   public CutCommandLine(Console console) {
      this.console = console;
   }

   @Override
   public void actionPerformed(ActionEvent actionEvent) {
      this.console.getCommandLine().cut();
   }
}
