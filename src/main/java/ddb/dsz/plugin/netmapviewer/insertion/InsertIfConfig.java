package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertIfConfig extends UpdateNode {
   private final IfConfig ifConfig;

   public InsertIfConfig(IfConfig var1, NetmapViewerHost var2) {
      super(var2);
      this.ifConfig = var1;
   }

   public void run() {
      final List var1 = this.ifConfig.getAddresses();
      DefaultMutableTreeNode var2 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1x) {
            if (var1x == InsertIfConfig.this.networkHost.ArpNode) {
               return false;
            } else {
               if (var1x instanceof Node) {
                  Node var2 = (Node)var1x;
                  Iterator var3 = var1.iterator();

                  while(var3.hasNext()) {
                     String var4 = (String)var3.next();
                     if (var2.doesAddressMatch(var4)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      });
      Node var3 = null;
      if (var2 == null) {
         var3 = new Node();
         DefaultMutableTreeNode var4 = new DefaultMutableTreeNode(var3);
         this.insertNode(this.networkHost.unknownNode, var4);
      } else {
         var3 = (Node)var2.getUserObject();
      }

      var3.setIfconfig(this.ifConfig);
   }
}
