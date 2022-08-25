package ddb.dsz.plugin.transfermonitor.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.transfermonitor.TransferMonitorHost;
import ddb.dsz.plugin.transfermonitor.listeners.closure.FileStartClosure;
import ddb.dsz.plugin.transfermonitor.listeners.closure.FileStopClosure;
import ddb.dsz.plugin.transfermonitor.listeners.closure.FileWriteClosure;
import ddb.dsz.plugin.transfermonitor.listeners.closure.LocalNameClosure;
import ddb.dsz.plugin.transfermonitor.listeners.closure.ObjectValueClosure;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.map.MultiKeyMap;

public class PapercutClosure implements Closure, RetrieveClosureInterface {
   TransferMonitorHost monitor;
   final Map<String, ObjectValueClosure> closureMap = new HashMap();
   final Object pointerLock = new Object();
   MultiKeyMap pointerToRecords = new MultiKeyMap();
   Map<TaskId, String> storedPath = new HashMap();
   CoreController core;

   public PapercutClosure(TransferMonitorHost var1, CoreController var2) {
      this.monitor = var1;
      this.core = var2;
      this.closureMap.put("FileStart".toLowerCase(), new FileStartClosure(this));
      this.closureMap.put("FileLocalName".toLowerCase(), new LocalNameClosure(this));
      this.closureMap.put("FileWrite".toLowerCase(), new FileWriteClosure(this));
      this.closureMap.put("FileStop".toLowerCase(), new FileStopClosure(this));
   }

   public synchronized void execute(Object var1) {
      if (var1 != null && var1 instanceof DataEvent) {
         DataEvent var2 = (DataEvent)var1;
         if (var2.getDataType().equals(DataEventType.DATA)) {
            Task var3 = this.core.getTaskById(var2.getTaskId());
            if (var3 != null) {
               if (var3.getHost().equals(this.monitor.getTarget())) {
                  Iterator var4 = var2.getData().getObjectNames().iterator();

                  while(true) {
                     String var5;
                     ObjectValueClosure var6;
                     do {
                        if (!var4.hasNext()) {
                           return;
                        }

                        var5 = (String)var4.next();
                        var6 = (ObjectValueClosure)this.closureMap.get(var5.toLowerCase());
                     } while(var6 == null);

                     Iterator var7 = var2.getData().getObjects(var5).iterator();

                     while(var7.hasNext()) {
                        ObjectValue var8 = (ObjectValue)var7.next();
                        var6.execute(var8, this.core.getTaskById(var2.getTaskId()));
                     }
                  }
               }
            }
         }
      }
   }

   public TransferRecord getRecord(TaskId var1, String var2, boolean var3, boolean var4) {
      synchronized(this.pointerLock) {
         TransferRecord var5 = (TransferRecord)TransferRecord.class.cast(this.pointerToRecords.get(var1, var2));
         if (var5 == null && var3) {
            var5 = new TransferRecord(this.monitor.getNext(), "", (String)null, var1);
            this.pointerToRecords.put(var1, var2, var5);
            this.monitor.addRecord(var5);
         }

         return var5;
      }
   }

   public void recordChanged(TransferRecord var1) {
      this.monitor.recordChanged(var1);
   }

   public boolean isResumable() {
      return false;
   }
}
