package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.Console;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class HelpHandler extends BuiltinHandlerAdapter {
   Map<String, BuiltinCommand> commands;

   public HelpHandler(Console console, Map<String, BuiltinCommand> var2) {
      super(console);
      this.commands = var2;
   }

   @Override
   public boolean executeBuiltinCommand(String builtinCommand, String param) {
      if (param == null) {
         this.printAllCommands(builtinCommand);
         return true;
      } else {
         StringTokenizer var3 = new StringTokenizer(param);

         while(var3.hasMoreTokens()) {
            this.console.printBuiltinCommandHelp(var3.nextToken());
         }

         return true;
      }
   }

   private void printAllCommands(String var1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Available commands:\n");
      TreeSet var3 = new TreeSet();
      int var4 = 0;
      Iterator var5 = this.commands.keySet().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var3.add(var6);
         if (var6.length() > var4) {
            var4 = var6.length();
         }
      }

      int var8 = 0;
      Iterator var9 = var3.iterator();

      while(var9.hasNext()) {
         String var7 = (String)var9.next();
         stringBuilder.append(String.format(String.format("%%-%ds   ", var4), var7));
         ++var8;
         if (var8 % 3 == 0) {
            stringBuilder.append("\n");
         }
      }

      this.console.printString(stringBuilder.toString());
   }
}
