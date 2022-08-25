package ds.core.commandevents;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.command.CommandEvent.XmlOutput;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import java.awt.Color;
import java.util.Calendar;
import java.util.EventObject;

public class CommandEventImpl extends EventObject implements CommandEvent {
   private CommandEventType type;
   private String command;
   private String text;
   private TaskId id;
   private TaskId pid;
   private int reqId;
   private String targetAddress;
   private Calendar timestamp;
   private XmlOutput xmlOutput;
   private boolean currentOperation;
   private Color color;

   public CommandEventImpl(Object var1, CommandEventType var2, String var3, String var4, Color var5, TaskId var6, TaskId var7, String var8) {
      super(var1);
      this.xmlOutput = XmlOutput.DEFAULT;
      this.type = var2;
      this.command = var3;
      this.text = var4;
      this.id = var6;
      this.pid = var7;
      this.timestamp = Calendar.getInstance();
      this.targetAddress = var8;
      this.color = var5;
      this.currentOperation = false;
   }

   public CommandEventImpl(Object var1, CommandEventType var2, String var3, Task var4) {
      this(var1, var2, var4.getCommandName(), var3, Color.BLACK, var4.getId(), var4.getParentId(), var4.getTargetId());
   }

   public CommandEventImpl(Object var1, CommandEventType var2, String var3, String var4, TaskId var5, TaskId var6, String var7) {
      this(var1, var2, var3, var4, Color.BLACK, var5, var6, var7);
   }

   public CommandEventImpl(Object var1, CommandEventType var2, Task var3) {
      this(var1, var2, var3.getCommandName(), var3.getTypedCommand(), Color.BLACK, var3.getId(), var3.getParentId(), var3.getTargetId());
   }

   public CommandEventImpl(Object var1, CommandEventType var2, String var3, String var4, TaskId var5, TaskId var6, String var7, int var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
      this.reqId = var8;
   }

   public CommandEventImpl(Object var1, CommandEventType var2, Task var3, int var4) {
      this(var1, var2, var3);
      this.reqId = var4;
   }

   @Override
   public TaskId getId() {
      return this.id;
   }

   @Override
   public TaskId getPid() {
      return this.pid;
   }

   @Override
   public int getReqId() {
      return this.reqId;
   }

   @Override
   public String getCommand() {
      return this.command;
   }

   @Override
   public String getText() {
      return this.text;
   }

   public CommandEventType getType() {
      return this.type;
   }

   @Override
   public XmlOutput getXmlOutput() {
      return this.xmlOutput;
   }

   public void setXmlOutput(XmlOutput var1) {
      this.xmlOutput = var1;
   }

   @Override
   public Calendar getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(Calendar var1) {
      this.timestamp = var1;
   }

   @Override
   public String getTargetAddress() {
      return this.targetAddress;
   }

   public void setTargetAddress(String var1) {
      this.targetAddress = var1;
   }

   @Override
   public boolean isCurrentOperation() {
      return this.currentOperation;
   }

   public void setCurrentOperation(boolean var1) {
      this.currentOperation = var1;
   }

   @Override
   public Color getColor() {
      return this.color;
   }

   protected void eraseTaskSource() {
      this.id = null;
      this.pid = null;
   }

   public String toString() {
      return String.format("%s:  %d", this.getType(), this.getId().getId());
   }
}
