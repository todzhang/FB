package ddb.dsz.core.task.impl;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;

public class StringAccess implements TaskDataAccess {
   private static final String MEMORY = "Memory";
   private final String xml;
   private final String relativeLocation;
   private final Task task;
   private final TaskDataAccess.DataType type;

   public StringAccess(TaskDataAccess taskDataAccess, String xml) {
      this.xml = xml;
      this.relativeLocation = taskDataAccess.getRelativeLocation();
      this.task = taskDataAccess.getTask();
      this.type = taskDataAccess.getType();
   }

   @Override
   public final Task getTask() {
      return this.task;
   }

   @Override
   public final TaskDataAccess.DataType getType() {
      return this.type;
   }

   @Override
   public int getOrdinal() {
      return -1;
   }

   @Override
   public boolean isGenerated() {
      return true;
   }

   @Override
   public StringReader getReader() {
      return new StringReader(this.xml);
   }

   /** @deprecated */
   @Override
   @Deprecated
   public InputStream getStream() {
      return new StringBufferInputStream(this.xml);
   }

   @Override
   public long getSize() {
      return (long)this.xml.length();
   }

   @Override
   public String getLocation() {
      return "Memory";
   }

   @Override
   public String getLocationType() {
      return "Memory";
   }

   @Override
   public String getRelativeLocation() {
      return this.relativeLocation;
   }

   @Override
   public String toString() {
      return String.format("String:  %s", this.xml.substring(0, Math.min(300, this.xml.length())));
   }
}
