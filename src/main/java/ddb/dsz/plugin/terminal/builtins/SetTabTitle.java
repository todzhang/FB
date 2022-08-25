package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class SetTabTitle extends BuiltinHandlerAdapter {
   Terminal terminal;

   public SetTabTitle(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      if (param != null && param.trim().length() > 0) {
         this.terminal.setName(param);
         return true;
      } else {
         return false;
      }
   }
}
