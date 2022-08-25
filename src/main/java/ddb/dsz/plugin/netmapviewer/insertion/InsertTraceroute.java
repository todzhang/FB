package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Traceroute;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Closure;

public class InsertTraceroute extends UpdateNode {
   Traceroute trace;
   boolean found = false;
   int index;

   public InsertTraceroute(Traceroute var1, int var2, NetmapViewerHost var3) {
      super(var3);
      this.trace = var1;
      this.index = var2;
   }

   public void run() {
      try {
         NetmapViewerHost.Traverse(this.networkHost.rootNode, new Closure() {
            public void execute(Object var1) {
               if (var1 instanceof DefaultMutableTreeNode) {
                  var1 = ((DefaultMutableTreeNode)DefaultMutableTreeNode.class.cast(var1)).getUserObject();
                  if (var1 instanceof Node) {
                     Node var2 = (Node)var1;
                     if (InsertTraceroute.this.index != -1) {
                        Traceroute.Hop var3 = (Traceroute.Hop)InsertTraceroute.this.trace.getHops().get(InsertTraceroute.this.index);
                        if (var2.doesNameMatch(var3.getAddress()) || var2.doesAddressMatch(var3.getAddress())) {
                           InsertTraceroute.this.found = true;
                           var2.addTraceroute(InsertTraceroute.this.trace);
                        }
                     } else {
                        if (var2.doesNameMatch(InsertTraceroute.this.trace.getLocation()) || var2.doesAddressMatch(InsertTraceroute.this.trace.getLocation())) {
                           InsertTraceroute.this.found = true;
                           var2.addTraceroute(InsertTraceroute.this.trace);
                        }

                     }
                  }
               }
            }
         });
         if (!this.found) {
            Node var1 = new Node();
            if (this.index == -1) {
               var1.setName(this.trace.getLocation());
            } else {
               if (((Traceroute.Hop)this.trace.getHops().get(this.index)).getAddress().equals("*")) {
                  return;
               }

               var1.setName(((Traceroute.Hop)this.trace.getHops().get(this.index)).getAddress());
            }

            var1.addTraceroute(this.trace);
            DefaultMutableTreeNode var2 = new DefaultMutableTreeNode(var1);
            this.insertNode(this.networkHost.unknownNode, var2);
         }
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

   }
}
