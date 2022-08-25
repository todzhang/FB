package ds.core.commanddispatcher.live;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.command.CommandEvent.XmlOutput;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.TaskId;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.core.commandevents.CommandEventImpl;
import ds.jaxb.ipc.InfoType;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.OutputType;
import ds.jaxb.ipc.XmlOutputType;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

public class CommandOutputClosure extends MessageClosure {
   public CommandOutputClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getInfo() == null) {
         return false;
      } else {
         return message.getInfo().getOutput() != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      InfoType var2 = message.getInfo();
      OutputType var3 = var2.getOutput();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Recieved command output message");
      }

      TaskId var4 = this.createTaskId(var2.getCmdId());
      MutableTask var5 = this.getTaskById(var4);
      if (var5 == null && var4.isValid()) {
         try {
            this.live.getMainSystem().logEvent(Level.WARNING, "Output received for unknown command\n" + this.live.getFormatter().formatMessageAsString(message));
         } catch (JAXBException var14) {
            this.live.getMainSystem().logEvent(Level.WARNING, "Output received for unknown commandUnable to format IPC message as string", var14);
         }

      } else {
         String var8 = null;
         String var6;
         TaskId var7;
         if (!var4.isValid()) {
            var6 = "";
            var7 = TaskId.NULL;
         } else {
            var6 = var5.getCommandName();
            var7 = var5.getParentId();
            var8 = var5.getTargetId();
         }

         String var9 = var3.getText();
         String var10 = var3.getColor();
         CommandEventImpl var11 = new CommandEventImpl(this, CommandEventType.OUTPUT, var6, var9, this.lookup(var10), var4, var7, var5 != null ? var5.getTargetId() : "127.0.0.1");
         var11.setCurrentOperation(true);
         var11.setTargetAddress(var8);
         if (var9 == null) {
            for(Iterator var12 = var3.getXml().getNode().iterator(); var12.hasNext(); this.live.publishEvent(var11)) {
               XmlOutputType.Node var13 = (XmlOutputType.Node)var12.next();
               var11 = new CommandEventImpl(this, CommandEventType.OUTPUT, var6, var13.getValue(), var4, var7, var5 != null ? var5.getTargetId() : "127.0.0.1");
               var11.setCurrentOperation(true);
               var11.setTargetAddress(var8);
               if (var13.getType() == null) {
                  var11.setXmlOutput(XmlOutput.DEFAULT);
               } else {
                  switch(var13.getType()) {
                  case ERROR:
                     var11.setXmlOutput(XmlOutput.ERROR);
                     break;
                  case GOOD:
                     var11.setXmlOutput(XmlOutput.GOOD);
                     break;
                  case WARNING:
                     var11.setXmlOutput(XmlOutput.WARNING);
                     break;
                  case DEFAULT:
                  default:
                     var11.setXmlOutput(XmlOutput.DEFAULT);
                  }
               }
            }

         } else {
            this.live.publishEvent(var11);
         }
      }
   }
}
