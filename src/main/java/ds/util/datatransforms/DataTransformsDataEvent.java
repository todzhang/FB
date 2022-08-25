package ds.util.datatransforms;

import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.data.TransformEvent.TransformEventType;
import ddb.dsz.core.task.TaskId;
import ddb.util.GeneralUtilities;
import ds.util.datatransforms.transformers.ObjectValueImpl;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class DataTransformsDataEvent implements DataEvent {
   private DataEventType type;
   private DataTransformer source;
   StringBuilder rawText;
   StringBuilder transformedText;
   ObjectValueImpl data;
   TaskId taskId;
   Calendar timestamp;
   String resourceDirectory;
   String display;
   String storage;
   Map<String, String> parameters;
   List<String> guiflags;

   public DataTransformsDataEvent(DataEventType var1, DataTransformer var2, TaskId var3) {
      this.type = DataEventType.START;
      this.source = null;
      this.rawText = new StringBuilder();
      this.transformedText = new StringBuilder();
      this.data = new ObjectValueImpl();
      this.taskId = TaskId.UNINITIALIZED_ID;
      this.guiflags = new Vector();
      this.type = var1;
      this.source = var2;
      this.taskId = var3;
   }

   public List<String> getGuiflags() {
      return this.guiflags;
   }

   public void addFlag(String var1) {
      this.guiflags.add(var1);
   }

   public DataTransformer getSource() {
      return this.source;
   }

   public void setSource(DataTransformer var1) {
      this.source = var1;
   }

   public void setParameters(String var1, String var2, String var3, Map<String, String> var4) {
      this.display = var1;
      this.storage = var2;
      this.resourceDirectory = var3;
      this.parameters = var4;
   }

   public TransformEventType getType() {
      return TransformEventType.DATA;
   }

   @Override
   public DataEventType getDataType() {
      return this.type;
   }

   void setType(DataEventType var1) {
      this.type = var1;
   }

   public void addTransformedText(String var1) {
      this.transformedText.append(var1);
   }

   public void addObject(String var1, ObjectValue var2) {
      this.data.addValue(DataType.OBJECT, var1, var2);
   }

   public String getTransformedText() {
      return this.transformedText.toString();
   }

   public String getRawText() {
      return this.rawText.toString();
   }

   @Override
   public ObjectValue getData() {
      return this.data;
   }

   @Override
   public TaskId getTaskId() {
      return this.taskId;
   }

   public void setTaskId(TaskId var1) {
      this.taskId = var1;
   }

   @Override
   public Calendar getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(String var1) {
      this.timestamp = GeneralUtilities.stringToCalendar(var1, this.timestamp);
   }

   public String getDisplay() {
      return this.display;
   }

   public Map<String, String> getParameters() {
      return Collections.unmodifiableMap(this.parameters);
   }

   public String getStorage() {
      return this.storage;
   }

   public String getResourceDirectory() {
      return this.resourceDirectory;
   }
}
