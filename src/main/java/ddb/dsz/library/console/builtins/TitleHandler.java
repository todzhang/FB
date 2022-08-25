package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class TitleHandler extends BuiltinHandlerAdapter {
   public TitleHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.console.setName(param);
      return true;
   }
}
