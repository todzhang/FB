package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.User;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertUsersNode extends UpdateNode {
   private User user;

   public InsertUsersNode(User var1, NetmapViewerHost var2) {
      super(var2);
      this.user = var1;
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               List var3 = var2.getUsers();
               if (var3 != null && (var2.doesNameMatch(InsertUsersNode.this.user.getTarget()) || var2.doesAddressMatch(InsertUsersNode.this.user.getTarget()))) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2;
      if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         List var3 = var2.getUsers();
         if (var3.size() != 0 && (var3.size() <= 0 || !((User)var3.get(0)).getTaskId().equals(this.user.getTaskId()))) {
            var2.removeDataset(Node.DataTypes.UserType);
            var2.addUser(this.user);
         } else {
            var2.addUser(this.user);
            this.networkHost.treeModel.nodeChanged(var1);
         }
      } else {
         var2 = new Node();
         var2.setName(this.user.getTarget());
         DefaultMutableTreeNode var4 = new DefaultMutableTreeNode(var2);
         var2.getUsers().add(this.user);
         this.insertNode(this.networkHost.unknownNode, var4);
      }

   }
}
