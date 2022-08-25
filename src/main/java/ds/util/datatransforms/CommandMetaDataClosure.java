package ds.util.datatransforms;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ds.util.datatransforms.transformers.ObjectValueImpl;
import java.util.Iterator;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Node;

public class CommandMetaDataClosure extends TransformedTaskClosure {
   protected final Closure handleData;
   protected final CoreController core;

   protected CommandMetaDataClosure(CoreController var1, Task var2, Closure var3, Closure var4) {
      super(var2, var4);
      this.core = var1;
      this.handleData = var3;
   }

   protected CommandMetaDataClosure(CoreController var1, String var2, String var3, Closure var4, Closure var5) {
      super(var2, var5);
      this.core = var1;
      this.handleData = var4;
   }

   protected void handleNode(Node var1, TaskDataAccess var2) {
   }

   protected void handleCommandData(TaskDataAccess var1) {
      super.handleCommandData(var1);
      Task var2 = var1.getTask();
      DataTransformsDataEvent var3 = new DataTransformsDataEvent(DataEventType.DATA, (DataTransformer)null, var2.getId());
      ObjectValueImpl var4 = this.createCommandMetaData(var1);
      ObjectValueImpl var5 = new ObjectValueImpl();
      var4.addValue(DataType.OBJECT, "gui_data", var5);
      var5.addValue(DataType.STRING, "LogType", var1.getLocationType());
      var5.addValue(DataType.STRING, "FullLocation", var1.getLocation());
      var3.addObject("CommandMetaData", var4);
      this.handleData.execute(var3);
   }

   protected void handleCommandTasking(TaskDataAccess var1) {
      super.handleCommandTasking(var1);
      Task var2 = var1.getTask();
      DataTransformsDataEvent var3 = new DataTransformsDataEvent(DataEventType.DATA, (DataTransformer)null, var2.getId());
      ObjectValueImpl var4 = this.createCommandMetaData(var1);
      ObjectValueImpl var5 = new ObjectValueImpl();
      var4.addValue(DataType.OBJECT, "gui_Tasking", var5);
      var5.addValue(DataType.STRING, "Operation", var2.getId().getOperation().toString());
      var5.addValue(DataType.STRING, "Target", var2.getTargetId());
      var5.addValue(DataType.STRING, "TypedCommand", var2.getTypedCommand());
      var5.addValue(DataType.STRING, "TaskId", var2.getTaskId().toString());
      var5.addValue(DataType.STRING, "DisplayTransform", var2.getDisplayTransform());
      var5.addValue(DataType.STRING, "StorageTransform", var2.getStorageTransform());
      var5.addValue(DataType.STRING, "ResourceDirectory", var2.getResourceDirectory());
      var5.addValue(DataType.STRING, "LogType", var1.getLocationType());
      var5.addValue(DataType.STRING, "FullLocation", var1.getLocation());
      Iterator var6 = var2.getArguments().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var5.addValue(DataType.STRING, "Argument", var7);
      }

      var5.addValue(DataType.INTEGER, "ParentTask", var2.getParentId().getId());
      var3.addObject("CommandMetaData", var4);
      this.handleData.execute(var3);
   }

   protected ObjectValueImpl createCommandMetaData(TaskDataAccess var1) {
      ObjectValueImpl var2 = new ObjectValueImpl();
      Task var3 = var1.getTask();
      var2.addValue(DataType.INTEGER, "Id", var3.getId().getId());
      var2.addValue(DataType.STRING, "Name", var3.getCommandName());
      var2.addValue(DataType.STRING, "XmlLog", var1.getRelativeLocation());
      return var2;
   }
}
