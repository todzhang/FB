package ds.core.impl.task;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;

public abstract class AbstractDataAccess implements TaskDataAccess {
   private Task task;
   private DataType type;
   private final int ordinal;

   protected AbstractDataAccess(Task var1, DataType var2, int var3) {
      this.task = var1;
      this.type = var2;
      this.ordinal = var3;
   }

   @Override
   public final Task getTask() {
      return this.task;
   }

   public final DataType getType() {
      return this.type;
   }

   @Override
   public int getOrdinal() {
      return this.ordinal;
   }

   @Override
   public boolean isGenerated() {
      return false;
   }
}
