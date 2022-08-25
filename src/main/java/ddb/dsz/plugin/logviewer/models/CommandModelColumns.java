package ddb.dsz.plugin.logviewer.models;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskState;
import ddb.util.Guid;
import java.util.Calendar;

public enum CommandModelColumns {
   STATUS("", TaskState.class),
   ID("Id", Integer.class),
   OP("Op", Integer.class),
   COMMAND("Command", String.class),
   FULLCOMMAND("Full Command", String.class),
   COMMENT("", String.class),
   CREATED("Time", Calendar.class),
   TARGET("Target", String.class),
   GUID("", Guid.class),
   DISPLAY("", String.class),
   STORAGE("", String.class),
   VALID("", Boolean.class),
   TASK("", Task.class);

   String name;
   Class<?> clazz;

   private CommandModelColumns(String name, Class<?> clazz) {
      this.name = name;
      this.clazz = clazz;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public String toString() {
      return this.name;
   }
}
