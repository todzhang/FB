package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;

public class QuitDanderspritz extends BuiltinHandlerAdapter {
   Terminal terminal;

   public QuitDanderspritz(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      this.terminal.quitDanderspritz();
      return true;
   }
}
