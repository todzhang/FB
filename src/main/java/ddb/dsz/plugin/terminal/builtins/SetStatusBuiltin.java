package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class SetStatusBuiltin extends BuiltinHandlerAdapter {
   Terminal terminal;

   public SetStatusBuiltin(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      if (param != null && param.trim().length() > 0) {
         this.terminal.lockStatus(param.trim());
         return true;
      } else {
         return false;
      }
   }
}
