package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.BuiltinHandler;
import ddb.dsz.library.console.Console;
import ddb.dsz.library.console.ConsoleOutputPane;

public class BuiltinHandlerAdapter implements BuiltinHandler {
   protected Console console;

   public BuiltinHandlerAdapter(Console console) {
      this.console = console;
   }

   public void init(String var1) {
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.console.appendOutputMessage("Builtin command '" + builtinCommand + "' registered but not yet implemented\n", ConsoleOutputPane.OutputLevel.WARNING);
      return false;
   }
}
