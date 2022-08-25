package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.data.Arp;
import ddb.dsz.plugin.netmapviewer.insertion.InsertArps;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.collections.Closure;

public class ArpClosure implements Closure {
   NetmapViewerHost networkHost;

   public ArpClosure(NetmapViewerHost var1) {
      this.networkHost = var1;
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      TaskId var3 = var2.getTaskId();
      if (var3 != null) {
         if (DataEventType.DATA.equals(var2.getDataType())) {
            Vector var4 = new Vector();
            Iterator var5 = var2.getData().getObjects("entry").iterator();

            while(var5.hasNext()) {
               ObjectValue var6 = (ObjectValue)var5.next();
               Arp var7 = new Arp(var6);
               var7.setLpTimestamp(var2.getTimestamp());
               var4.add(var7);
            }

            EventQueue.invokeLater(new InsertArps(var4, this.networkHost));
         }
      }
   }
}
