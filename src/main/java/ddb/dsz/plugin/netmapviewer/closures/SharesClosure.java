package ddb.dsz.plugin.netmapviewer.closures;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Resource;
import ddb.dsz.plugin.netmapviewer.insertion.InsertShare;
import java.awt.EventQueue;
import java.util.Iterator;
import org.apache.commons.collections.Closure;

public class SharesClosure implements Closure {
   private final CoreController core;
   NetmapViewerHost networkHost;

   public SharesClosure(CoreController var1, NetmapViewerHost var2) {
      this.networkHost = var2;
      this.core = var1;
   }

   public void execute(Object var1) {
      DataEvent var2 = (DataEvent)var1;
      Task var3 = this.core.getTaskById(var2.getTaskId());
      if (DataEventType.DATA.equals(var2.getDataType())) {
         boolean var4 = true;

         Iterator var5;
         ObjectValue var6;
         Resource var7;
         for(var5 = var2.getData().getObjects("resource").iterator(); var5.hasNext(); var4 = false) {
            var6 = (ObjectValue)var5.next();
            var7 = new Resource();
            var7.setCaption(var6.getString("caption"));
            var7.setDescription(var6.getString("Description"));
            String var8 = var6.getString("Name");
            var7.setName(var8);
            var7.setNodeType(var6.getString("type"));
            var7.setPath(var6.getString("path"));
            var7.setAdmin(var6.getBoolean("admin"));
            EventQueue.invokeLater(new InsertShare(var7, var4, this.networkHost));
         }

         for(var5 = var2.getData().getObjects("share").iterator(); var5.hasNext(); var4 = false) {
            var6 = (ObjectValue)var5.next();
            var7 = new Resource();
            var7.setName(var6.getString("remotename"));
            var7.setDescription(String.format("Mapped Resource on %s", var3.getHost().getId()));
            EventQueue.invokeLater(new InsertShare(var7, var4, this.networkHost));
         }

      }
   }
}
