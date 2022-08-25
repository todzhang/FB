package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Arp;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertArps extends UpdateNode {
   private Collection<Arp> arps;

   public InsertArps(Collection<Arp> var1, NetmapViewerHost var2) {
      super(var2);
      this.arps = var1;
   }

   public void run() {
      Node var1 = this.networkHost.ArpNode;
      if (var1 == null) {
         var1 = new Node();
         var1.setMatchable(false);
         this.networkHost.ArpNode = var1;
         var1.setName("Arp");
         var1.setOrder(Node.Order.Before);
         DefaultMutableTreeNode var2 = new DefaultMutableTreeNode(var1);
         this.insertNode(this.networkHost.unknownNode, var2);
      }

      Iterator var7 = this.arps.iterator();

      while(var7.hasNext()) {
         final Arp var3 = (Arp)var7.next();
         var1.addArp(var3);
         DefaultMutableTreeNode var4 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
            public boolean evaluate(Object var1) {
               if (var1 instanceof Node) {
                  Node var2 = (Node)var1;
                  if (var2.doesAddressMatch(var3.getInetAddress()) || var2.doesNameMatch(var3.getInetAddress())) {
                     return true;
                  }
               }

               return false;
            }
         });
         Node var5;
         if (var4 != null) {
            var5 = (Node)var4.getUserObject();
            var5.addArp(var3);
         } else {
            var5 = new Node();
            DefaultMutableTreeNode var6 = new DefaultMutableTreeNode(var5);
            var5.setName(var3.getInetAddress());
            var5.addArp(var3);
            this.insertNode(this.networkHost.unknownNode, var6);
         }
      }

   }
}
