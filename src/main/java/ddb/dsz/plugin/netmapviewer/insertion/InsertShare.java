package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.Resource;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertShare extends UpdateNode {
   private final Resource res;
   private final String target;
   private final boolean resetData;

   public InsertShare(Resource var1, boolean var2, NetmapViewerHost var3) {
      super(var3);
      this.res = var1;
      this.resetData = var2;
      this.target = var1.getName().substring(2, var1.getName().indexOf("\\", 2));
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               if (var2.doesAddressMatch(InsertShare.this.target)) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2 = null;
      if (var1 != null && !this.resetData) {
         var2 = (Node)var1.getUserObject();
         var2.addResource(this.res);
         this.networkHost.treeModel.nodeChanged(var1);
      } else if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         var2.removeDataset(Node.DataTypes.ResourceType);
         var2.addResource(this.res);
      } else {
         var2 = new Node();
         var2.setName(this.target);
         DefaultMutableTreeNode var3 = new DefaultMutableTreeNode(var2);
         var2.getResources().add(this.res);
         this.insertNode(this.networkHost.unknownNode, var3);
      }

   }
}
