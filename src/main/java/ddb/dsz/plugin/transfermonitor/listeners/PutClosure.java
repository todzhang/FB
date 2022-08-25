package ddb.dsz.plugin.transfermonitor.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.transfermonitor.TransferMonitorHost;
import ddb.dsz.plugin.transfermonitor.model.TransferDirection;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Closure;

public class PutClosure implements Closure {
   Map<TaskId, TransferRecord> putFile = new HashMap();
   TransferMonitorHost monitor;
   CoreController core;

   public PutClosure(TransferMonitorHost var1, CoreController var2) {
      this.monitor = var1;
      this.core = var2;
   }

   public void execute(Object var1) {
      if (var1 instanceof DataEvent) {
         DataEvent var2 = (DataEvent)var1;
         Task var3 = this.core.getTaskById(var2.getTaskId());
         if (var3 != null) {
            if (var3.getHost().equals(this.monitor.getTarget())) {
               TaskId var4 = var2.getTaskId();
               Iterator var5 = var2.getData().getObjects("localfile").iterator();

               ObjectValue var6;
               TransferRecord var7;
               while(var5.hasNext()) {
                  var6 = (ObjectValue)var5.next();
                  var7 = (TransferRecord)this.putFile.get(var4);
                  if (var7 == null) {
                     var7 = new TransferRecord(this.monitor.getNext(), "", "", var4);
                     this.putFile.put(var4, var7);
                  }

                  var7.setResumable(false);
                  var7.setLocal(var6.getString("name"));
                  var7.setDirection(TransferDirection.PUT);
                  this.monitor.addRecord(var7);
                  if (var7.getRemote() == null) {
                     var7.setRemote((new File(var7.getLocal())).getName());
                  }
               }

               for(var5 = var2.getData().getObjects("file").iterator(); var5.hasNext(); this.monitor.recordChanged(var7)) {
                  var6 = (ObjectValue)var5.next();
                  var7 = (TransferRecord)this.putFile.get(var4);
                  if (var7 == null) {
                     var7 = new TransferRecord(this.monitor.getNext(), "", "", var4);
                     var7.setDirection(TransferDirection.PUT);
                     this.putFile.put(var2.getTaskId(), var7);
                  }

                  Long var8 = var6.getInteger("byteswritten");
                  Long var9 = var6.getInteger("bytesremaining");
                  if (var8 != null && var9 != null) {
                     if (var7.getSize() == 0L) {
                        var7.setSize(var9 + var8);
                     }

                     var7.addTransfered(var8);
                     if (var9 == 0L) {
                        var7.setState(TransferState.DONE);
                     }
                  } else {
                     var7.setRemote(var6.getString("name"));
                  }
               }

            }
         }
      }
   }
}
