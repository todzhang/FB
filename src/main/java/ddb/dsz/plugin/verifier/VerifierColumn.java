package ddb.dsz.plugin.verifier;

import ddb.dsz.core.task.TaskState;
import ddb.util.Guid;

public enum VerifierColumn {
   TASK_STATUS("", TaskState.class),
   OP("Op", Integer.class),
   GUID("", Guid.class),
   ID("Id", Integer.class),
   COMMAND("Command", String.class),
   FULLCOMMAND("Full Command", String.class),
   VERIFY_STATUS("Valid?", VerifierState.class),
   OUTPUT("", String.class);

   String name;
   Class<?> clazz;

   private VerifierColumn(String var3, Class<?> var4) {
      this.name = var3;
      this.clazz = var4;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }
}
