package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.data.Netmap;
import ddb.dsz.plugin.netmapviewer.insertion.InsertNetmapNode;
import java.awt.EventQueue;
import java.util.Iterator;
import org.apache.commons.collections.Closure;

public class NetmapClosure implements Closure {
   NetmapViewerHost networkHost;

   public NetmapClosure(NetmapViewerHost var1) {
      this.networkHost = var1;
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      TaskId var3 = var2.getTaskId();
      if (var3 != null) {
         if (DataEventType.DATA.equals(var2.getDataType())) {
            Iterator var4 = var2.getData().getObjects("netmapentryitem").iterator();

            while(var4.hasNext()) {
               ObjectValue var5 = (ObjectValue)var4.next();
               EventQueue.invokeLater(new InsertNetmapNode(new Netmap(var5), this.networkHost));
            }

         }
      }
   }
}
