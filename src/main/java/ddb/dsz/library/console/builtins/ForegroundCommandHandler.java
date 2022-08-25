package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;
import ddb.dsz.library.console.ConsoleOutputPane;

public class ForegroundCommandHandler extends BuiltinHandlerAdapter {
   public ForegroundCommandHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      try {
         this.console.foregroundCommand(Integer.parseInt(param));
         return true;
      } catch (NumberFormatException var4) {
         this.console.appendOutputMessage("Internal command '" + builtinCommand + "' takes an integer argument\n", ConsoleOutputPane.OutputLevel.ERROR);
         return false;
      }
   }
}
