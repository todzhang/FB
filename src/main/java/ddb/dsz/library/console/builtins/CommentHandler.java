package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class CommentHandler extends BuiltinHandlerAdapter {
   public CommentHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      return false;
   }
}
