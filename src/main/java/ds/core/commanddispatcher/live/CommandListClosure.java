package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.controller.CommandInfo;
import ddb.dsz.core.controller.CommandInfo.CommandInfoEnum;
import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.AliasDefinitionType;
import ds.jaxb.ipc.CommandDefinitionType;
import ds.jaxb.ipc.CommandListType;
import ds.jaxb.ipc.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class CommandListClosure extends MessageClosure {
   public CommandListClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getRes() == null) {
         return false;
      } else {
         CommandListType var2 = message.getRes().getCommandList();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      CommandListType var2 = message.getRes().getCommandList();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command list message");
      }

      List var3 = var2.getPrefix();
      List var4 = var2.getAlias();
      List var5 = var2.getCommand();
      ArrayList var6 = new ArrayList();
      HashMap var7 = new HashMap();
      Iterator var8 = var3.iterator();

      CommandInfo var10;
      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         var10 = new CommandInfo(var9, CommandInfoEnum.PREFIX);
         var6.add(var10);
         var7.put(var10.getName().toLowerCase(), var10);
      }

      var8 = var5.iterator();

      String var18;
      while(var8.hasNext()) {
         CommandDefinitionType var15 = (CommandDefinitionType)var8.next();
         var18 = var15.getOptionPrefix();
         List var11 = var15.getOption();
         CommandInfo var12 = new CommandInfo(var15.getName(), CommandInfoEnum.COMMAND);
         var12.setOptionPrefix(var18);
         Iterator var13 = var11.iterator();

         while(var13.hasNext()) {
            String var14 = (String)var13.next();
            var12.addOption(var14);
         }

         var6.add(var12);
         var7.put(var12.getName().toLowerCase(), var12);
      }

      var8 = var4.iterator();

      while(var8.hasNext()) {
         AliasDefinitionType var16 = (AliasDefinitionType)var8.next();
         var10 = new CommandInfo(var16.getOriginal(), var16.getReplace());
         var6.add(var10);
         var7.put(var10.getName().toLowerCase(), var10);
      }

      var8 = var6.iterator();

      while(true) {
         while(true) {
            CommandInfo var17;
            do {
               if (!var8.hasNext()) {
                  this.live.getMainSystem().getCommandSet().replaceAllCommands(var6, var7);
                  this.live.publishEvent(new CommandEventImpl(this, CommandEventType.COMMANDLISTUPDATED, "", "", this.createTaskId(0), (TaskId)null, "127.0.0.1"));
                  return;
               }

               var17 = (CommandInfo)var8.next();
            } while(!var17.isAlias());

            var18 = var17.getExpansionCommand();
            int var19 = 100;

            while(var19 > 0) {
               --var19;
               StringTokenizer var20 = new StringTokenizer(var18);
               if (var20.hasMoreTokens()) {
                  String var21 = var20.nextToken();
                  if (var21.equals("%cmd_args%")) {
                     var17.setCommandType(CommandInfoEnum.ALIASPREFIX);
                     break;
                  }

                  CommandInfo var22 = (CommandInfo)var7.get(var21.toLowerCase());
                  if (var22 == null && var21.indexOf(61) != -1) {
                     var22 = (CommandInfo)var7.get(var21.substring(0, var21.indexOf(61)).toLowerCase());
                  }

                  if (var22 == null) {
                     break;
                  }

                  switch(var22.getType()) {
                  case ALIAS:
                     var18 = var18.replaceFirst(var21, var22.getExpansionCommand());
                  case COMMAND:
                  default:
                     break;
                  case ALIASPREFIX:
                  case PREFIX:
                     var18 = var18.replaceFirst(var21, "");
                  }
               } else {
                  var17.setCommandType(CommandInfoEnum.ALIASPREFIX);
               }
            }
         }
      }
   }
}
