package ddb.dsz.core.task;

import ddb.dsz.core.operation.Operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskId implements Comparable<TaskId> {
   public static final Comparator<TaskId> TaskIdComparator = (one, two) -> {
      if (one == two) {
         return 0;
      } else if (one == null && two != null) {
         return -1;
      } else if (two == null && one != null) {
         return 1;
      } else if (one.getOperation() == null && two.getOperation() != null) {
         return -1;
      } else if (one.getOperation() != null && two.getOperation() == null) {
         return 1;
      } else {
         int var3 = one.getOperation().compareTo(two.getOperation());
         return var3 != 0 ? var3 : one.getId() - two.getId();
      }
   };
   private static final List<TaskId> CREATED_IDS = new ArrayList();
   public static final TaskId NULL = new TaskId(0, (Operation)null);
   public static final TaskId GLOBAL = new TaskId(-1, (Operation)null);
   public static final TaskId UNINITIALIZED_ID = new TaskId(-2, (Operation)null);
   private static final TaskId[] ListOfKnownIds;
   private final int id;
   private final Operation operation;
   private Task task = null;

   public static final synchronized TaskId GenerateTaskId(int var0, Operation var1) {
      TaskId[] var2 = ListOfKnownIds;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TaskId var5 = var2[var4];
         if (var0 == var5.id) {
            return var5;
         }
      }

      TaskId var6 = new TaskId(var0, var1);
      var3 = Collections.binarySearch(CREATED_IDS, var6, TaskIdComparator);
      if (var3 >= 0) {
         return (TaskId)CREATED_IDS.get(var3);
      } else {
         ++var3;
         var3 = -var3;
         if (var3 >= CREATED_IDS.size()) {
            CREATED_IDS.add(var6);
         } else {
            CREATED_IDS.add(var3, var6);
         }

         return var6;
      }
   }

   private TaskId(int id, Operation operation) {
      this.id = id;
      this.operation = operation;
   }

   public final void setTask(Task task) {
      this.task = task;
   }

   public final Task getTask() {
      return this.task;
   }

   public final int getId() {
      return this.id;
   }

   @Override
   public int compareTo(TaskId taskId) {
      return TaskIdComparator.compare(this, taskId);
   }

   @Override
   public String toString() {
      return String.format("%d", this.id);
   }

   public final boolean isValid() {
      return this.id > 0;
   }

   public final boolean idMatch(int id) {
      return this.id == id;
   }

   public final Operation getOperation() {
      return this.operation;
   }

   @Override
   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + this.id;
      var3 = 31 * var3 + (this.operation == null ? 0 : this.operation.hashCode());
      return var3;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (this.getClass() != other.getClass()) {
         return false;
      } else {
         TaskId var2 = (TaskId)other;
         if (this.id != var2.id) {
            return false;
         } else {
            if (this.operation == null) {
               if (var2.operation != null) {
                  return false;
               }
            } else if (!this.operation.equals(var2.operation)) {
               return false;
            }

            return true;
         }
      }
   }

   static {
      ListOfKnownIds = new TaskId[]{NULL, GLOBAL, UNINITIALIZED_ID};
   }
}
