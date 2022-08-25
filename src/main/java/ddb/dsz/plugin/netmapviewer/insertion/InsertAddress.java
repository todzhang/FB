package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Closure;

public class InsertAddress extends UpdateNode {
   private final Collection<String> addresses;

   public InsertAddress(Collection<String> var1, NetmapViewerHost var2) {
      super(var2);
      this.addresses = var1;
   }

   public void run() {
      NetmapViewerHost.Traverse(this.networkHost.rootNode, new Closure() {
         public void execute(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               InsertAddress.this.addresses.removeAll(var2.getAddresses());
            }

         }
      });
      int var1 = this.networkHost.unknownNode.getChildCount();
      int[] var2 = new int[this.addresses.size()];
      int var3 = 0;

      for(Iterator var4 = this.addresses.iterator(); var4.hasNext(); var2[var3++] = var1++) {
         String var5 = (String)var4.next();
         Node var6 = new Node();
         var6.setName(var5);
         var6.addAddress(var5);
         DefaultMutableTreeNode var7 = new DefaultMutableTreeNode(var6);
         this.networkHost.unknownNode.add(var7);
      }

      this.networkHost.treeModel.nodesWereInserted(this.networkHost.unknownNode, var2);
   }
}
