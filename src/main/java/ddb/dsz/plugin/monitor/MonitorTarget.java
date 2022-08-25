package ddb.dsz.plugin.monitor;

import java.util.List;
import java.util.Vector;

class MonitorTarget extends CheckNode {
   String id;
   List<MonitorTask> tasks = new Vector();

   public String toString() {
      return this.id;
   }
}
