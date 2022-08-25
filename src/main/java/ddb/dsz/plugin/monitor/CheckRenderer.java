package ddb.dsz.plugin.monitor;

import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

public class CheckRenderer extends JPanel implements TreeCellRenderer {
   protected JCheckBox check;
   private CheckRenderer.TreeLabel label;

   public CheckRenderer() {
      this.setLayout((LayoutManager)null);
      this.add(this.check = new JCheckBox());
      this.add(this.label = new CheckRenderer.TreeLabel());
   }

   public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
      String var8;
      boolean var9;
      if (!(var2 instanceof CheckNode)) {
         var8 = "";
         var9 = false;
      } else {
         var9 = ((CheckNode)CheckNode.class.cast(var2)).isSelected();
         Object var10 = ((CheckNode)CheckNode.class.cast(var2)).getUserObject();
         if (var10 instanceof Task) {
            var8 = String.format("%d %s", ((Task)Task.class.cast(var10)).getId().getId(), ((Task)Task.class.cast(var10)).getCommandName());
         } else if (var10 instanceof HostInfo) {
            var8 = ((HostInfo)HostInfo.class.cast(var10)).getId();
         } else {
            var8 = var1.convertValueToText(var2, var3, var4, var5, var6, var7);
         }
      }

      this.setEnabled(var1.isEnabled());
      this.check.setSelected(var9);
      this.label.setFont(var1.getFont());
      this.label.setText(var8);
      this.label.setSelected(var3);
      this.label.setFocus(var7);
      if (var5) {
         this.label.setIcon(UIManager.getIcon("Tree.leafIcon"));
      } else if (var4) {
         this.label.setIcon(UIManager.getIcon("Tree.openIcon"));
      } else {
         this.label.setIcon(UIManager.getIcon("Tree.closedIcon"));
      }

      return this;
   }

   public Dimension getPreferredSize() {
      Dimension var1 = this.check.getPreferredSize();
      Dimension var2 = this.label.getPreferredSize();
      return new Dimension(var1.width + var2.width, var1.height < var2.height ? var2.height : var1.height);
   }

   public void doLayout() {
      Dimension var1 = this.check.getPreferredSize();
      Dimension var2 = this.label.getPreferredSize();
      int var3 = 0;
      int var4 = 0;
      if (var1.height < var2.height) {
         var3 = (var2.height - var1.height) / 2;
      } else {
         var4 = (var1.height - var2.height) / 2;
      }

      this.check.setLocation(0, var3);
      this.check.setBounds(0, var3, var1.width, var1.height);
      this.label.setLocation(var1.width, var4);
      this.label.setBounds(var1.width, var4, var2.width, var2.height);
   }

   public void setBackground(Color var1) {
      if (var1 instanceof ColorUIResource) {
         var1 = null;
      }

      super.setBackground(var1);
   }

   class TreeLabel extends JLabel {
      boolean isSelected;
      boolean hasFocus;

      public TreeLabel() {
      }

      public void setBackground(Color var1) {
         if (var1 instanceof ColorUIResource) {
            var1 = null;
         }

         super.setBackground(var1);
      }

      public void paint(Graphics var1) {
         if (this.getText() != null) {
            if (this.isSelected) {
               var1.setColor(UIManager.getColor("Tree.selectionBackground"));
            } else {
               var1.setColor(UIManager.getColor("Tree.textBackground"));
            }

            Dimension var3 = this.getPreferredSize();
            int var4 = 0;
            Icon var5 = this.getIcon();
            if (var5 != null) {
               var4 = var5.getIconWidth() + Math.max(0, this.getIconTextGap() - 1);
            }

            var1.fillRect(var4, 0, var3.width - 1 - var4, var3.height);
            if (this.hasFocus) {
               var1.setColor(UIManager.getColor("Tree.selectionBorderColor"));
               var1.drawRect(var4, 0, var3.width - 1 - var4, var3.height - 1);
            }
         }

         super.paint(var1);
      }

      public Dimension getPreferredSize() {
         Dimension var1 = super.getPreferredSize();
         if (var1 != null) {
            var1 = new Dimension(var1.width + 3, var1.height);
         }

         return var1;
      }

      void setSelected(boolean var1) {
         this.isSelected = var1;
      }

      void setFocus(boolean var1) {
         this.hasFocus = var1;
      }
   }
}
