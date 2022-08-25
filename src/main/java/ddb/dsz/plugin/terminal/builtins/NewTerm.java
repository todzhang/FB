package ddb.dsz.plugin.terminal.builtins;

import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.terminal.Terminal;
import ddb.util.QuotedStringTokenizer;
import java.util.Vector;

public class NewTerm extends BuiltinHandlerAdapter {
   Terminal terminal;

   public NewTerm(Terminal var1) {
      super(var1);
      this.terminal = var1;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      Vector var3 = new Vector();
      if (param == null) {
         param = "";
      }

      QuotedStringTokenizer var4 = new QuotedStringTokenizer(param, " \t", true);

      while(var4.hasMoreTokens()) {
         String var5 = var4.nextToken();
         if (var5.trim().length() != 0) {
            var3.add(var5);
         }
      }

      return this.terminal.newTerminal(var3);
   }
}
