package ddb.dsz.library.console.actions;

import ddb.dsz.library.console.ConsoleOutputPane;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class CopyOutputPane extends AbstractAction {
   private final ConsoleOutputPane output;

   public CopyOutputPane(ConsoleOutputPane output) {
      this.output = output;
   }

   @Override
   public void actionPerformed(ActionEvent actionEvent) {
      this.output.copy();
   }
}
