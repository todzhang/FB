package ddb.dsz.plugin.monitor;

import ddb.dsz.core.task.Task;
import java.util.List;
import java.util.Vector;

public class MonitoredCommand {
   private Task task;
   private List<MonitoredCommandOutput> outputs;
   private Object outputLock;
   private boolean changedSinceLastViewing;

   public MonitoredCommand(Task var1) {
      this.task = var1;
      this.changedSinceLastViewing = false;
      this.outputs = new Vector();
      this.outputLock = new Object();
   }

   public boolean isChangedSinceLastViewing() {
      return this.changedSinceLastViewing;
   }

   public Task getTask() {
      return this.task;
   }

   public void setChangedSinceLastViewing(boolean var1) {
      this.changedSinceLastViewing = var1;
   }

   public void addOutput(MonitoredCommandOutput var1) {
      synchronized(this.outputLock) {
         this.outputs.add(var1);
         this.changedSinceLastViewing = true;
      }
   }

   public List<MonitoredCommandOutput> getOutputs() {
      return this.outputs;
   }
}
