package ddb.dsz.plugin.monitor;

import ddb.dsz.core.task.Task;

public class MonitoredCommandOutput implements Comparable<MonitoredCommandOutput> {
   private static long currentOrdinal = 0L;
   private final Task task;
   private final String commandOutput;
   private final long when;
   private final long ordinal;

   private static synchronized long getOrdinal() {
      return (long)(currentOrdinal++);
   }

   public MonitoredCommandOutput(Task var1, long var2, String var4) {
      this.task = var1;
      this.when = var2;
      this.ordinal = getOrdinal();
      this.commandOutput = var4;
   }

   public final Task getTask() {
      return this.task;
   }

   public final String getCommandOutput() {
      return this.commandOutput;
   }

   public final long getWhen() {
      return this.when;
   }

   public int compareTo(MonitoredCommandOutput var1) {
      return var1 == null ? -1 : (int)(this.ordinal - var1.ordinal);
   }
}
