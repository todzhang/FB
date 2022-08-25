package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Ping;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertPing extends UpdateNode {
   Ping ping;

   public InsertPing(Ping var1, NetmapViewerHost var2) {
      super(var2);
      this.ping = var1;
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               if (var2.doesAddressMatch(InsertPing.this.ping.getDestination()) || var2.doesNameMatch(InsertPing.this.ping.getDestination())) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2;
      if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         var2.addPing(this.ping);
         this.networkHost.treeModel.nodeChanged(var1);
      } else {
         var2 = new Node();
         DefaultMutableTreeNode var3 = new DefaultMutableTreeNode(var2);
         var2.getPings().add(this.ping);
         this.insertNode(this.networkHost.unknownNode, var3);
      }

   }
}
