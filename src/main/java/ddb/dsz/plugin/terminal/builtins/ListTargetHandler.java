package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class ListTargetHandler extends BuiltinHandlerAdapter {
   Terminal terminal;

   public ListTargetHandler(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.terminal.listTargets();
      return true;
   }
}
