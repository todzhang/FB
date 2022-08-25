package ddb.dsz.plugin.shell;

import ddb.dsz.core.controller.CommandSet;
import ddb.dsz.library.console.CommandCompletionAction;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class ShellCompletionAction extends CommandCompletionAction {
   private JTextField commandLine;
   private CommandSet commands;
   private Shell terminal;

   public ShellCompletionAction(Shell var1, CommandSet var2) {
      this.commandLine = var1.getCommandLine();
      this.commands = var2;
      this.terminal = var1;
   }

   public void actionPerformed(ActionEvent actionEvent) {
   }
}
