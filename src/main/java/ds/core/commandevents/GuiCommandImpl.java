package ds.core.commandevents;

import ddb.dsz.core.command.GuiCommand;
import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.task.TaskId;
import java.awt.Color;

public class GuiCommandImpl extends CommandEventImpl implements GuiCommand {
   String guiCommand;
   int reqId;
   boolean isHandled = false;

   public GuiCommandImpl(Object var1, TaskId var2, String var3, int var4) {
      super(var1, CommandEventType.GUICOMMAND, (String)null, (String)null, (Color)null, var2, (TaskId)null, (String)null);
      this.guiCommand = var3;
      this.reqId = var4;
   }

   public String getGuiCommand() {
      return this.guiCommand;
   }

   public boolean isHandled() {
      return this.isHandled;
   }

   public void handled() {
      this.isHandled = true;
   }

   @Override
   public int getReqId() {
      return this.reqId;
   }

   public void eraseTask() {
      super.eraseTaskSource();
   }
}
