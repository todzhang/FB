package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Service;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertServicesNode extends UpdateNode {
   private Service service;

   public InsertServicesNode(Service var1, NetmapViewerHost var2) {
      super(var2);
      this.service = var1;
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               List var3 = var2.getUsers();
               if (var3 != null && (var2.doesNameMatch(InsertServicesNode.this.service.getTarget()) || var2.doesAddressMatch(InsertServicesNode.this.service.getTarget()))) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2;
      if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         List var3 = var2.getServices();
         if (var3.size() != 0 && (var3.size() <= 0 || !((Service)var3.get(0)).getTaskId().equals(this.service.getTaskId()))) {
            var2.removeDataset(Node.DataTypes.ServiceType);
            var2.addService(this.service);
         } else {
            var2.addService(this.service);
            this.networkHost.treeModel.nodeChanged(var1);
         }
      } else {
         var2 = new Node();
         var2.setName(this.service.getTarget());
         DefaultMutableTreeNode var4 = new DefaultMutableTreeNode(var2);
         var2.getServices().add(this.service);
         this.insertNode(this.networkHost.unknownNode, var4);
      }

   }
}
