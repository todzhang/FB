package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import ddb.dsz.plugin.netmapviewer.insertion.InsertIfConfig;
import java.awt.EventQueue;
import org.apache.commons.collections.Closure;

public class IfConfigClosure implements Closure {
   private final CoreController core;
   NetmapViewerHost networkHost;

   public IfConfigClosure(CoreController var1, NetmapViewerHost var2) {
      this.networkHost = var2;
      this.core = var1;
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      Task var3 = this.core.getTaskById(var2.getTaskId());
      if (DataEventType.DATA.equals(var2.getDataType())) {
         if (var2.getData().getObject("FixedDataItem") != null) {
            IfConfig var4 = new IfConfig(var2.getData(), var2.getTaskId());
            EventQueue.invokeLater(new InsertIfConfig(var4, this.networkHost));
         }

      }
   }
}
