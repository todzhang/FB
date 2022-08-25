package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.insertion.InsertAddress;
import ddb.dsz.plugin.netmapviewer.insertion.MergeAddresses;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.collections.Closure;

public class NetConnectionsClosure implements Closure {
   final NetmapViewerHost networkHost;
   final Collection<String> AllRemoteAddressesSeen;
   final Collection<String> AllLocalAddressesSeen;

   public NetConnectionsClosure(NetmapViewerHost var1) {
      this.networkHost = var1;
      this.AllRemoteAddressesSeen = new HashSet();
      this.AllLocalAddressesSeen = new HashSet();
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      TaskId var3 = var2.getTaskId();
      if (var3 != null) {
         if (DataEventType.DATA.equals(var2.getDataType())) {
            HashSet var4 = new HashSet();
            HashSet var5 = new HashSet();
            String[] var6 = new String[]{"InitialConnectionListItem", "StartConnectionListItem", "StopConnectionListItem"};
            int var7 = var6.length;

            label61:
            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               Iterator var10 = var2.getData().getObjects(String.format("%s::ConnectionItem", var9)).iterator();

               while(true) {
                  ObjectValue var13;
                  do {
                     if (!var10.hasNext()) {
                        continue label61;
                     }

                     ObjectValue var11 = (ObjectValue)var10.next();
                     ObjectValue var12 = var11.getObject("local");
                     if (var12 != null) {
                        var4.add(var12.getString("address"));
                        var4.add(var12.getString("ipv4"));
                        var4.add(var12.getString("ipv6"));
                     }

                     var13 = var11.getObject("remote");
                  } while(var13 == null);

                  Vector var14 = new Vector();
                  var14.add(var13.getString("address"));
                  var14.add(var13.getString("ipv4"));
                  var14.add(var13.getString("ipv6"));
                  Iterator var15 = var14.iterator();

                  while(var15.hasNext()) {
                     String var16 = (String)var15.next();
                     if (var16 != null && !var16.equals("::") && !var16.equals("0.0.0.0")) {
                        var5.add(var16);
                     }
                  }
               }
            }

            var5.removeAll(this.AllRemoteAddressesSeen);
            if (var5.size() > 0) {
               this.AllRemoteAddressesSeen.addAll(var5);
               EventQueue.invokeLater(new InsertAddress(var5, this.networkHost));
            }

            var4.removeAll(this.AllLocalAddressesSeen);
            if (var4.size() > 0) {
               this.AllLocalAddressesSeen.addAll(var4);
               EventQueue.invokeLater(new MergeAddresses(var4, this.networkHost));
            }

         }
      }
   }
}
