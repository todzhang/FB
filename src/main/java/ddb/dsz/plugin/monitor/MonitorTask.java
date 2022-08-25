package ddb.dsz.plugin.monitor;

import ddb.dsz.core.task.Task;

class MonitorTask extends CheckNode {
   MonitorTarget parent;
   Task task;

   public String toString() {
      return this.task.getFullCommandLine();
   }

   public boolean isSelected() {
      return this.parent.isSelected() && super.isSelected();
   }
}
