package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class NewPluginHandler extends BuiltinHandlerAdapter {
   public NewPluginHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.console.startNewPluginWithInitArgs(param);
      return true;
   }
}
