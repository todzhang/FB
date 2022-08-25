package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class TargetHandler extends BuiltinHandlerAdapter {
   Terminal terminal;

   public TargetHandler(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      return param != null && param.trim().length() > 0 ? this.terminal.setTarget(param) : false;
   }
}
