package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class ResetStatus extends BuiltinHandlerAdapter {
   Terminal terminal;

   public ResetStatus(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.terminal.resetStatus();
      return true;
   }
}
