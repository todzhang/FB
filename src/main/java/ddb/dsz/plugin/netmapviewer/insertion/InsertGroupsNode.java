package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Group;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertGroupsNode extends UpdateNode {
   private Group group;

   public InsertGroupsNode(Group var1, NetmapViewerHost var2) {
      super(var2);
      this.group = var1;
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               List var3 = var2.getGroups();
               if (var3 != null && (var2.doesNameMatch(InsertGroupsNode.this.group.getTarget()) || var2.doesAddressMatch(InsertGroupsNode.this.group.getTarget()))) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2;
      if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         List var3 = var2.getGroups();
         if (var3.size() != 0 && (var3.size() <= 0 || !((Group)var3.get(0)).getTaskId().equals(this.group.getTaskId()))) {
            var2.removeDataset(Node.DataTypes.GroupType);
            var2.addGroup(this.group);
         } else {
            var2.addGroup(this.group);
            this.networkHost.treeModel.nodeChanged(var1);
         }
      } else {
         var2 = new Node();
         var2.setName(this.group.getTarget());
         DefaultMutableTreeNode var4 = new DefaultMutableTreeNode(var2);
         var2.getGroups().add(this.group);
         this.insertNode(this.networkHost.unknownNode, var4);
      }

   }
}
