package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class ForegroundLastCommandHandler extends BuiltinHandlerAdapter {
   public ForegroundLastCommandHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.console.foregroundLast();
      return true;
   }
}
