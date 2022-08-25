package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class UpdateNode implements Runnable {
   NetmapViewerHost networkHost;

   public UpdateNode(NetmapViewerHost var1) {
      this.networkHost = var1;
   }

   protected void insertNode(DefaultMutableTreeNode var1, DefaultMutableTreeNode var2) {
      this.networkHost.insertNode(var1, var2);
   }

   protected void removeNode(DefaultMutableTreeNode var1) {
      this.networkHost.removeNode(var1);
   }
}
