package ddb.dsz.plugin.monitor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class NodeSelectionListener extends MouseAdapter {
   JTree tree;

   public NodeSelectionListener(JTree var1) {
      this.tree = var1;
   }

   public void mouseClicked(MouseEvent var1) {
      if (!var1.isPopupTrigger() && var1.getButton() == 1 && !var1.isShiftDown()) {
         int var2 = var1.getX();
         int var3 = var1.getY();
         int var4 = this.tree.getRowForLocation(var2, var3);
         TreePath var5 = this.tree.getPathForRow(var4);
         if (var5 != null) {
            CheckNode var6 = (CheckNode)var5.getLastPathComponent();
            boolean var7 = !var6.isSelected();
            Collection var8 = var6.setSelected(var7);
            var8.add(var6);
            if (CheckNode.SelectionMode.DigInSelection.equals(var6.getSelectionMode())) {
               if (var7) {
                  this.tree.expandPath(var5);
               } else {
                  this.tree.collapsePath(var5);
               }
            }

            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               CheckNode var10 = (CheckNode)var9.next();
               ((DefaultTreeModel)DefaultTreeModel.class.cast(this.tree.getModel())).nodeChanged(var10);
            }

            if (var4 == 0) {
               this.tree.revalidate();
               this.tree.repaint();
            }
         }

      }
   }

   public void mousePressed(MouseEvent var1) {
      if (!var1.isPopupTrigger()) {
         ;
      }
   }

   public void mouseReleased(MouseEvent var1) {
      if (!var1.isPopupTrigger()) {
         ;
      }
   }
}
