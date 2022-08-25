package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.SetFlagsType;
import java.util.Iterator;
import java.util.logging.Level;

public class SetFlagsClosure extends MessageClosure {
   public SetFlagsClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getSetFlags() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      SetFlagsType var2 = message.getInfo().getSetFlags();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved set flags message");
      }

      TaskId var3 = this.createTaskId(message.getInfo().getCmdId());
      MutableTask var4 = this.getTaskById(var3);
      if (var4 == null) {
         this.live.getMainSystem().logEvent(Level.WARNING, String.format("SetFlags message received for unknown command: %d", message.getInfo().getCmdId()));
      } else {
         Iterator var5 = var2.getFlag().iterator();

         while(true) {
            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               if (var6.startsWith("guiflag")) {
                  int var7 = var6.indexOf(61);
                  String var8 = var6.substring(var7 + 1);
                  if (var8.length() == 0) {
                     this.live.getMainSystem().logEvent(Level.WARNING, "GUI flags received without any values.  Ignoring flag");
                     continue;
                  }

                  var4.addGuiFlags(var8);
               }

               CommandEventImpl var9 = new CommandEventImpl(this, CommandEventType.SET_FLAGS, var6, var4);
               var9.setCurrentOperation(true);
               this.live.publishEvent(var9);
            }

            return;
         }
      }
   }
}
