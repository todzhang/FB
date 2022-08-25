package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.data.Traceroute;
import ddb.dsz.plugin.netmapviewer.insertion.InsertTraceroute;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Closure;

public class TracerouteClosure implements Closure {
   NetmapViewerHost networkHost;
   final Map<TaskId, Traceroute> traceRouteMap = new HashMap();
   final Set<DataEvent> pendingEvents = new HashSet();

   public TracerouteClosure(NetmapViewerHost var1) {
      this.networkHost = var1;
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      TaskId var3 = var2.getTaskId();
      if (var3 != null) {
         if (DataEventType.DATA.equals(var2.getDataType())) {
            HashSet var4 = new HashSet();
            synchronized(this.traceRouteMap) {
               Iterator var6 = var2.getData().getObjects("target").iterator();

               ObjectValue var7;
               Traceroute var8;
               while(var6.hasNext()) {
                  var7 = (ObjectValue)var6.next();
                  var8 = new Traceroute(var7);
                  synchronized(this.traceRouteMap) {
                     this.traceRouteMap.put(var2.getTaskId(), var8);
                  }

                  EventQueue.invokeLater(new InsertTraceroute(var8, -1, this.networkHost));
                  Iterator var9 = this.pendingEvents.iterator();

                  while(var9.hasNext()) {
                     DataEvent var10 = (DataEvent)var9.next();
                     if (var10.getTaskId().equals(var2.getTaskId())) {
                        var4.add(var10);
                     }
                  }

                  this.pendingEvents.removeAll(var4);
               }

               var6 = var2.getData().getObjects("hopinfo").iterator();

               while(var6.hasNext()) {
                  var7 = (ObjectValue)var6.next();
                  synchronized(this.traceRouteMap) {
                     var8 = (Traceroute)this.traceRouteMap.get(var2.getTaskId());
                  }

                  if (var8 == null) {
                     this.pendingEvents.add(var2);
                  } else {
                     int var17 = var8.addHop(var7);
                     EventQueue.invokeLater(new InsertTraceroute(var8, var17, this.networkHost));
                  }
               }
            }

            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               DataEvent var16 = (DataEvent)var5.next();
               this.execute(var16);
            }

         }
      }
   }
}
