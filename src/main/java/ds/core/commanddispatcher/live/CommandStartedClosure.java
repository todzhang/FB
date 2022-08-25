package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.core.impl.task.TaskImpl;
import ds.jaxb.ipc.CommandStartedType;
import ds.jaxb.ipc.InfoType;
import ds.jaxb.ipc.Message;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class CommandStartedClosure extends MessageClosure {
   public CommandStartedClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getCommandStarted() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      InfoType var2 = message.getInfo();
      CommandStartedType var3 = message.getInfo().getCommandStarted();
      if (var3 != null) {
         if (this.live.getMainSystem().isDebugMode()) {
            this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command started message");
         }

         TaskId var4 = this.createTaskId(var2.getCmdId());
         String var5 = var3.getCommand();
         List var6 = var3.getPrefix();
         List var7 = var3.getArg();
         TaskId var8 = this.createTaskId(var3.getParent());
         Object var9 = this.getTaskById(var4);
         if (var9 == null) {
            StringBuilder var10 = new StringBuilder();
            Iterator var11 = var6.iterator();

            String var12;
            while(var11.hasNext()) {
               var12 = (String)var11.next();
               var10.append(var12);
               var10.append(" ");
            }

            var10.append(var5);
            var11 = var7.iterator();

            while(var11.hasNext()) {
               var12 = (String)var11.next();
               var10.append(" ");
               var10.append(var12);
            }

            var9 = new TaskImpl(var10.toString(), (HostInfo)null);
            ((MutableTask)var9).setId(var4);
            MutableTask var16 = this.getTaskById(var8);
            ((MutableTask)var9).setParent(var16);
            synchronized(this.live.getMainSystem()) {
               this.live.getMainSystem().addNewTask((MutableTask)var9);
            }
         }

         ((MutableTask)var9).setResourceDirectory("Dsz");
         ((MutableTask)var9).addArguments(var7);
         ((MutableTask)var9).addPrefixes(var6);
         ((MutableTask)var9).setCommandName(var5);
         ((MutableTask)var9).setState(TaskState.RUNNING);
         ((MutableTask)var9).notifyObservers();
         CommandEventImpl var15 = new CommandEventImpl(this, CommandEventType.STARTED, (Task)var9);
         var15.setCurrentOperation(true);
         var15.setTargetAddress(((MutableTask)var9).getTargetId());
         this.live.publishEvent(var15);
      }
   }
}
