package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;

public class SetHandler extends BuiltinHandlerAdapter {
   public SetHandler(Console console) {
      super(console);
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      if (param == null) {
         this.printVariables();
         return true;
      } else {
         String[] var3 = param.split("=", 2);
         if (var3.length == 1) {
            this.printVariables();
         } else if (var3[1].length() > 0) {
            this.console.getVariables().put(var3[0].toUpperCase(), var3[1]);
         } else {
            this.console.getVariables().put(var3[0], (String) null);
         }

         return true;
      }
   }

   private void printVariables() {
      System.out.println(this.console.getVariables());
   }
}
