package ddb.dsz.library.console;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public abstract class CommandCompletionAction extends AbstractAction {
   public abstract void actionPerformed(ActionEvent actionEvent);
}
