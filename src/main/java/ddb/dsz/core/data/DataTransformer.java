package ddb.dsz.core.data;

import ddb.Factory;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.collections.Closure;

public abstract class DataTransformer extends Factory {
   public static final String DEFAULT_IMPL = "DataTranformer.impl";
   private static final String DEFAULT_NAME = "DataTransformer default";
   public static final String LP_TIMESTAMP = "gui_LpTimestamp";
   public static final String DATA_TIMESTAMP = "gui_DataTimestamp";
   public static final String GUI_TASKING = "gui_Tasking";
   public static final String ORDINAL = "Ordinal";
   public static final String SIZE = "Size";

   public static final DataTransformer newInstance() {
      return newInstance("DataTransformer default");
   }

   public static final DataTransformer newInstance(String var0) {
      return newInstance(var0, false);
   }

   public static final DataTransformer newInstance(boolean var0) {
      return newInstance("DataTransformer default", var0);
   }

   public static final DataTransformer newInstance(ThreadFactory var0) {
      return newInstance(var0, false);
   }

   public static final DataTransformer newInstance(String var0, boolean var1) {
      return (DataTransformer)Factory.newObject(System.getProperty("DataTranformer.impl"), new Class[]{String.class, Boolean.TYPE}, var0, var1);
   }

   public static final DataTransformer newInstance(ThreadFactory var0, boolean var1) {
      return (DataTransformer)Factory.newObject(System.getProperty("DataTranformer.impl"), new Class[]{ThreadFactory.class, Boolean.TYPE}, var0, var1);
   }

   public abstract void addTask(Task var1);

   public abstract void removeAllTasks();

   public abstract void removeTask(Task var1);

   public abstract void stop();

   public abstract void addClosure(Closure var1);

   public abstract void addClosure(Closure var1, ClosureOrder var2);

   public abstract void addClosure(Closure var1, ClosureOrder var2, TaskDataAccess.DataType var3);

   public abstract void removeClosure(Closure var1);

   public abstract void removeAllClosures();
}
