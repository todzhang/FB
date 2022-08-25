package ddb.dsz.plugin.netmapviewer.insertion;

import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.dsz.plugin.netmapviewer.Node;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.collections.Predicate;

public class InsertPrintCommandNode extends UpdateNode {
   private final Task task;
   private final String data;
   private final String target;
   private final Node targetNode;

   public InsertPrintCommandNode(String var1, Task var2, String var3, NetmapViewerHost var4) {
      super(var4);
      this.data = var1;
      this.task = var2;
      this.target = var3;
      this.targetNode = null;
   }

   public InsertPrintCommandNode(String var1, Task var2, Node var3, NetmapViewerHost var4) {
      super(var4);
      this.data = var1;
      this.task = var2;
      this.targetNode = var3;
      this.target = null;
   }

   public void run() {
      DefaultMutableTreeNode var1 = NetmapViewerHost.DepthFirstSearch(this.networkHost.rootNode, new Predicate() {
         public boolean evaluate(Object var1) {
            if (var1 instanceof Node) {
               Node var2 = (Node)var1;
               if (var2 == InsertPrintCommandNode.this.targetNode || var2.doesNameMatch(InsertPrintCommandNode.this.target) || var2.doesAddressMatch(InsertPrintCommandNode.this.target)) {
                  return true;
               }
            }

            return false;
         }
      });
      Node var2 = null;
      Vector var3 = new Vector();
      var3.add(this.data);
      var3.add(this.task.getTaskId());
      if (var1 != null) {
         var2 = (Node)var1.getUserObject();
         var2.addPrintingCommandEntry(this.task, this.data);
      } else {
         var2 = new Node();
         var2.setName(this.target);
         DefaultMutableTreeNode var4 = new DefaultMutableTreeNode(var2);
         var2.addPrintingCommandEntry(this.task, this.data);
         this.insertNode(this.networkHost.unknownNode, var4);
      }

   }
}
