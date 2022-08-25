package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Netmap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class InsertNetmapNode extends UpdateNode {
   Netmap child;

   public InsertNetmapNode(Netmap var1, NetmapViewerHost var2) {
      super(var2);
      this.child = var1;
   }

   public void run() {
      Node var1 = new Node();
      var1.setNetmapData(this.child);
      DefaultMutableTreeNode var2 = new DefaultMutableTreeNode(var1);
      DefaultMutableTreeNode var3 = (DefaultMutableTreeNode)this.networkHost.nameToNode.get(this.child.getName());
      if (var3 != null) {
         Node var4 = (Node)var3.getUserObject();
         Netmap var5 = var4.getNetmapData();
         if (var5 != null && var5.getLpTimestamp() - this.child.getLpTimestamp() >= 0L) {
            return;
         }

         var4.setNetmapData(this.child);
         this.networkHost.treeModel.nodeChanged(var3);
      } else {
         this.networkHost.nameToNode.put(this.child.getName(), var2);
         DefaultMutableTreeNode var11 = this.networkHost.unknownNode;
         if (this.child.getLevel() > 0L) {
            DefaultMutableTreeNode var12 = (DefaultMutableTreeNode)this.networkHost.nameToNode.get(this.child.getParent());
            if (var12 != null) {
               var11 = var12;
            }
         } else {
            var11 = this.networkHost.rootNode;
         }

         if (var11 == null) {
            this.networkHost.unknownNode = this.networkHost.rootNode;
         }

         boolean var13 = false;

         for(int var6 = 0; var6 < var11.getChildCount() && !var13; ++var6) {
            TreeNode var7 = var11.getChildAt(var6);
            if (var7 instanceof DefaultMutableTreeNode) {
               DefaultMutableTreeNode var8 = (DefaultMutableTreeNode)var7;
               Object var9 = var8.getUserObject();
               if (var9 instanceof Node) {
                  Node var10 = (Node)var9;
                  if (var10.toString().compareToIgnoreCase(this.child.getName()) >= 0) {
                     if (var10.toString().compareToIgnoreCase(this.child.getName()) == 0) {
                        NetmapViewerHost.CopyNode(var1, var10);
                        this.networkHost.treeModel.nodesChanged(var11, new int[]{var6});
                        this.networkHost.expand(var7);
                        var13 = true;
                     } else {
                        this.networkHost.treeModel.insertNodeInto(var2, var11, var6);
                        this.networkHost.expand(var2);
                        var13 = true;
                     }
                  }
               }
            }
         }

         if (!var13) {
            this.networkHost.treeModel.insertNodeInto(var2, var11, var11.getChildCount());
            this.networkHost.expand(var2);
         }
      }

      this.networkHost.NewNodeCleanup(var2, var1);
   }
}
