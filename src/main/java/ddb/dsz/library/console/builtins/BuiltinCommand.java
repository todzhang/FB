package ddb.dsz.library.console.builtins;

import ddb.dsz.library.console.BuiltinHandler;
import ddb.dsz.library.console.Console;
import ddb.dsz.library.console.jaxb.consolecommands.Argument;
import ddb.dsz.library.console.jaxb.consolecommands.Command;
import ddb.dsz.library.console.jaxb.consolecommands.Split;
import ddb.dsz.library.console.jaxb.consolecommands.Statement;
import java.util.Map;

public class BuiltinCommand {
   private String name;
   private BuiltinHandler handler;
   private String helpStatement;

   public BuiltinCommand(String name) {
      this.name = name;
   }

   public BuiltinHandler getHandler() {
      return this.handler;
   }

   public void setHandler(BuiltinHandler handler) {
      this.handler = handler;
   }

   public String getHelpStatement() {
      return this.helpStatement;
   }

   public void setHelpStatement(String helpStatement) {
      this.helpStatement = helpStatement;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static BuiltinCommand generate(Console console, final Command command, final Map<String, BuiltinCommand> paraMap) {
      BuiltinCommand builtinCommand = new BuiltinCommand(command.getName());
      builtinCommand.setHelpStatement(command.getHelp());
      builtinCommand.setHandler(new BuiltinHandlerAdapter(console) {
         @Override
         public boolean executeBuiltinCommand(String builtinCommand, String param) {
            String var3 = this.getValue(builtinCommand, param, command.getCommand());
            String var4 = this.getValue(builtinCommand, param, command.getArgs());
            return paraMap.get(var3) != null ? ((BuiltinCommand)paraMap.get(var3)).getHandler().executeBuiltinCommand(var3, var4) : false;
         }

         private String getValue(String var1x, String var2x, Statement var3) {
            if (var3.getValue() != null) {
               return var3.getValue();
            } else if (var3.getNull() != null) {
               return null;
            } else {
               Argument var4 = var3.getArgument();
               if (var4.getFormat() != null) {
                  return var4.getFormat().replaceAll("%COMMAND%", var1x).replaceAll("%ARGS%", var2x);
               } else if (var4.getSplit() != null) {
                  Split var5 = var4.getSplit();
                  String var6 = var5.getString().replaceAll("%COMMAND%", var1x).replaceAll("%ARGS%", var2x);
                  String[] var7 = var6.split(var5.getRegex(), var5.getLimit());
                  if (var7 == null) {
                     return var5.getDefault();
                  } else if (var5.getPart() < 0) {
                     return var5.getDefault();
                  } else {
                     return var7.length < var5.getPart() ? var5.getDefault() : var7[var5.getPart()];
                  }
               } else {
                  return null;
               }
            }
         }
      });
      return builtinCommand;
   }
}
