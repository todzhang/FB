package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class MergeAddresses extends UpdateNode {
   private final Collection<String> addresses = new HashSet();

   public MergeAddresses(Collection<String> var1, NetmapViewerHost var2) {
      super(var2);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (var4 != null && !var4.equals("0.0.0.0") && !var4.equals("::")) {
            this.addresses.add(var4);
         }
      }

   }

   public void run() {
      if (this.addresses.size() > 1) {
         HashSet var1 = new HashSet();
         DefaultMutableTreeNode var2 = null;
         Iterator var3 = this.addresses.iterator();

         DefaultMutableTreeNode var5;
         while(var3.hasNext()) {
            final String var4 = (String)var3.next();
            var5 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
               public boolean evaluate(Object var1) {
                  if (var1 instanceof Node) {
                     Node var2 = (Node)var1;
                     if (var2.doesAddressMatch(var4) || var2.doesNameMatch(var4)) {
                        return true;
                     }
                  }

                  return false;
               }
            });
            if (var5 != null) {
               var1.add(var5);
               if (var2 == null && var5.getParent() != this.networkHost.unknownNode) {
                  var2 = var5;
               }
            }
         }

         Node var8;
         Iterator var9;
         String var11;
         if (var2 != null) {
            var8 = (Node)var2.getUserObject();
            var9 = this.addresses.iterator();

            while(var9.hasNext()) {
               var11 = (String)var9.next();
               var8.addAddress(var11);
            }

            var1.remove(var2);
            var9 = var1.iterator();

            while(var9.hasNext()) {
               var5 = (DefaultMutableTreeNode)var9.next();
               Node var14 = (Node)var5.getUserObject();
               var8.CopyNode(var14);
               this.removeNode(var5);
            }

         } else {
            DefaultMutableTreeNode var10;
            if (var1.size() == 0) {
               var8 = new Node();
               var8.setName((String)this.addresses.iterator().next());
               var9 = this.addresses.iterator();

               while(var9.hasNext()) {
                  var11 = (String)var9.next();
                  var8.addAddress(var11);
               }

               var10 = new DefaultMutableTreeNode(var8);
               this.insertNode(this.networkHost.unknownNode, var10);
            } else {
               var3 = var1.iterator();
               var10 = (DefaultMutableTreeNode)var3.next();
               Node var12 = (Node)var10.getUserObject();
               Iterator var6 = this.addresses.iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  var12.addAddress(var7);
               }

               while(var3.hasNext()) {
                  DefaultMutableTreeNode var13 = (DefaultMutableTreeNode)var3.next();
                  Node var15 = (Node)var13.getUserObject();
                  var12.CopyNode(var15);
                  this.removeNode(var13);
               }
            }

         }
      }
   }
}
