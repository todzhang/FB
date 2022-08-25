package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class ClearHandler extends BuiltinHandlerAdapter {
   public ClearHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      if (param == null) {
         param = "";
      }

      this.console.clearOutputScreen(param);
      return true;
   }
}
