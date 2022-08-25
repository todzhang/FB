package ds.plugin.pluginmanagement;

import ddb.detach.Alignment;
import ddb.detach.Workbench;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszDetachable;
import ddb.dsz.annotations.DszHideable;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.plugin.AbstractPlugin;
import ddb.dsz.plugin.Plugin;
import ddb.imagemanager.ImageManager;
import ddb.listeners.DoubleClickListener;
import ds.core.DSConstants;
import ds.gui.PluginWorkbench;
import ds.plugin.PluginContainer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

@DszLive(
   live = false,
   replay = false
)
@DszLogo("images/plugin_manager.png")
@DszName("Running Plugins")
@DszDescription("List of currently running plugins")
@DszUserStartable(false)
@DszHideable(
   hide = false,
   unhide = false
)
@DszDetachable(false)
public class PluginManager extends AbstractPlugin {
   PluginWorkbench pluginWorkbench;
   private JPanel mainFrame;
   DefaultTreeModel runningModel;
   JTree runningPlugins;
   PluginManager.PluginTreeNode root;
   private JLabel messageLabel;
   private ImageIcon errorIcon;
   Dimension imageToUse;

   public PluginManager() {
      super.setVerifyClose(false);
   }

   public void setWorkbench(PluginWorkbench var1) {
      super.setWorkbench(var1);
      this.pluginWorkbench = var1;
   }

   @Override
   protected int init2() {
      this.mainFrame = new JPanel();
      super.setDisplay(this.mainFrame);
      this.imageToUse = ImageManager.SIZE32;
      this.mainFrame.setLayout(new BorderLayout());
      this.root = new PluginManager.PluginTreeNode(this.pluginWorkbench, (PluginManager.PluginTreeNode)null);
      this.runningModel = new DefaultTreeModel(this.root);
      this.runningModel.addTreeModelListener(new TreeModelListener() {
         public void treeNodesChanged(TreeModelEvent var1) {
         }

         public void treeNodesInserted(TreeModelEvent var1) {
            TreePath var2 = var1.getTreePath();
            PluginManager.PluginTreeNode var3 = (PluginManager.PluginTreeNode)var2.getLastPathComponent();
            TreePath var4 = var2.pathByAddingChild(var3.getChildAt(var1.getChildIndices()[0]));
            PluginManager.this.runningPlugins.expandPath(var4);
         }

         public void treeNodesRemoved(TreeModelEvent var1) {
         }

         public void treeStructureChanged(TreeModelEvent var1) {
         }
      });
      this.runningPlugins = new JTree(this.runningModel);
      this.runningPlugins.setRootVisible(false);
      this.runningPlugins.setCellRenderer(new PluginManager.PluginTreeCellRenderer());
      this.runningPlugins.setRowHeight(this.imageToUse.height);
      this.runningPlugins.addTreeWillExpandListener(new TreeWillExpandListener() {
         public void treeWillExpand(TreeExpansionEvent var1) throws ExpandVetoException {
         }

         public void treeWillCollapse(TreeExpansionEvent var1) throws ExpandVetoException {
            throw new ExpandVetoException(var1);
         }
      });
      this.expandPath(this.root);
      this.mainFrame.add(new JScrollPane(this.runningPlugins));
      this.messageLabel = new JLabel();
      this.mainFrame.add(this.messageLabel, "South");
      this.errorIcon = ImageManager.getIcon(DSConstants.Icon.ERROR.getPath(), ImageManager.SIZE16);
      final JPopupMenu var1 = new JPopupMenu();
      final JMenuItem var2 = new JMenuItem("Tabbify plugin");
      var1.add(var2);
      final JMenuItem var3 = new JMenuItem("Hide plugin");
      var1.add(var3);
      final JMenuItem var4 = new JMenuItem("Unhide plugin");
      var1.add(var4);
      final JMenuItem var5 = new JMenuItem("Detach plugin");
      var1.add(var5);
      final JMenuItem var6 = new JMenuItem("Shutdown plugin");
      var1.add(var6);
      JMenuItem var7 = new JMenuItem("Rename plugin");
      var1.add(var7);
      final JMenuItem var8 = new JMenuItem("Duplicate plugin");
      var1.add(var8);
      var1.addPopupMenuListener(new PopupMenuListener() {
         public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
            TreePath[] var2x = PluginManager.this.runningPlugins.getSelectionPaths();
            if (var2x != null) {
               for(int var3x = 0; var3x < var2x.length; ++var3x) {
                  TreePath var4x = var2x[var3x];
                  PluginManager.PluginTreeNode var5x = (PluginManager.PluginTreeNode)var4x.getLastPathComponent();
                  Plugin var6x = var5x.getPlugin();
                  var2.setEnabled(var5x.getWorkbench().pluginIsDetached(var6x));
                  var5.setEnabled(!var5x.getWorkbench().pluginIsDetached(var6x));
                  if (!var6x.isDetachable()) {
                     var5.setEnabled(false);
                  }

                  var3.setEnabled(var6x.isHideable() && var5x.getWorkbench().pluginIsVisible(var6x));
                  var4.setEnabled(var6x.isUnhideable() && var5x.getWorkbench().pluginIsHidden(var6x));
                  var6.setEnabled(var6x.isClosable());
                  var8.setEnabled(PluginManager.this.pluginWorkbench.allowNewInstance(var6x.getClass()));
               }

            }
         }

         public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
         }

         public void popupMenuCanceled(PopupMenuEvent var1) {
         }
      });
      var2.addActionListener(new PluginManager.MenuAction() {
         protected WorkbenchAction getWorkbenchAction() {
            return WorkbenchAction.TABBIFYTAB;
         }

         protected Object[] getParams() {
            return new Object[0];
         }
      });
      var3.addActionListener(new PluginManager.MenuAction() {
         protected WorkbenchAction getWorkbenchAction() {
            return WorkbenchAction.HIDETAB;
         }

         protected Object[] getParams() {
            return new Object[0];
         }
      });
      var4.addActionListener(new PluginManager.MenuAction() {
         protected WorkbenchAction getWorkbenchAction() {
            return WorkbenchAction.UNHIDETAB;
         }

         protected Object[] getParams() {
            return new Object[0];
         }
      });
      var5.addActionListener(new PluginManager.MenuAction() {
         protected WorkbenchAction getWorkbenchAction() {
            return WorkbenchAction.DETACHTAB;
         }

         protected Object[] getParams() {
            return new Object[]{null, null};
         }
      });
      var6.addActionListener(new PluginManager.MenuAction() {
         protected boolean veto(Plugin var1) {
            return !var1.isClosable();
         }

         protected PluginWorkbench.PluginWorkbenchAction getPluginWorkbenchAction() {
            return PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN;
         }

         protected Object[] getParams() {
            return new Object[0];
         }
      });
      var7.addActionListener(new PluginManager.MenuAction() {
         protected WorkbenchAction getWorkbenchAction() {
            return WorkbenchAction.RENAMETAB;
         }

         protected Object[] getParams() {
            return new Object[]{JOptionPane.showInputDialog(PluginManager.access$201(PluginManager.this), "Enter new plugin name", "Rename", 3)};
         }
      });
      var8.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TreePath[] var2 = PluginManager.this.runningPlugins.getSelectionPaths();
            if (var2 != null) {
               for(int var3 = 0; var3 < var2.length; ++var3) {
                  TreePath var4 = var2[var3];
                  PluginManager.PluginTreeNode var5 = (PluginManager.PluginTreeNode)var4.getLastPathComponent();
                  Plugin var6 = var5.getPlugin();
                  PluginWorkbench var7 = var5.getWorkbench();
                  Class var9 = var6.getClass();
                  Alignment var10 = var6.getAlignment();

                  Plugin var8;
                  try {
                     var8 = var7.startPlugin(var9, (String)null, (List)null, (Alignment)var10, (String)null);
                  } catch (Exception var12) {
                     PluginManager.this.showErrorMessage(var12.getMessage());
                     continue;
                  }

                  if (var8 == null) {
                     PluginManager.this.showErrorMessage("Failed to load plugin of class " + var9);
                  } else {
                     PluginManager.this.clearMessageLabel();
                  }
               }

            }
         }
      });
      this.runningPlugins.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1x) {
            this.maybeShowPopup(var1x);
         }

         public void mouseReleased(MouseEvent var1x) {
            this.maybeShowPopup(var1x);
         }

         public void maybeShowPopup(MouseEvent var1x) {
            if (var1x.isPopupTrigger()) {
               int var2 = PluginManager.this.runningPlugins.getRowForLocation(var1x.getX(), var1x.getY());
               if (var2 >= 0) {
                  PluginManager.this.runningPlugins.setSelectionRow(var2);
               }

               var1.show(PluginManager.this.runningPlugins, var1x.getX(), var1x.getY());
            }

         }
      });
      this.runningPlugins.addMouseListener(new DoubleClickListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            PluginManager.this.focusOnPath(PluginManager.this.runningPlugins.getSelectionPath());
            PluginManager.this.clearMessageLabel();
         }
      }));
      return 0;
   }

   void focusOnPath(TreePath var1) {
      if (var1 != null) {
         if (var1.getPathCount() != 0) {
            PluginManager.PluginTreeNode var2 = (PluginManager.PluginTreeNode)var1.getLastPathComponent();
            if (var2 != null) {
               Plugin var3 = var2.getPlugin();
               PluginWorkbench var4 = var2.getWorkbench();
               if (var3 != null && var4 != null) {
                  var4.enqueAction(WorkbenchAction.UNHIDETAB, new Object[]{var3});
                  var4.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var3});
                  this.focusOnPath(var1.getParentPath());
               }
            }
         }
      }
   }

   private void expandPath(TreeNode var1) {
      this.runningPlugins.expandPath(new TreePath(this.runningModel.getPathToRoot(var1)));

      for(int var2 = 0; var2 < var1.getChildCount(); ++var2) {
         this.expandPath(var1.getChildAt(var2));
      }

   }

   public void showErrorMessage(final String var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.messageLabel.setText(var1);
         this.messageLabel.setIcon(this.errorIcon);
      } else {
         Runnable var2 = new Runnable() {
            public void run() {
               PluginManager.this.showErrorMessage(var1);
            }
         };
         SwingUtilities.invokeLater(var2);
      }

   }

   public void clearMessageLabel() {
      if (SwingUtilities.isEventDispatchThread()) {
         this.messageLabel.setText("");
         this.messageLabel.setIcon((Icon)null);
      } else {
         Runnable var1 = new Runnable() {
            public void run() {
               PluginManager.this.clearMessageLabel();
            }
         };
         SwingUtilities.invokeLater(var1);
      }

   }

   @Override
   public void setWorkbench(Workbench workbench) {
      super.setWorkbench(workbench);
      if (workbench instanceof PluginWorkbench) {
         this.pluginWorkbench = ((PluginWorkbench)PluginWorkbench.class.cast(workbench)).getHighestWorkbench();
         this.root.setModel(this.pluginWorkbench);
      }

   }

   // $FF: synthetic method
   static JComponent access$201(PluginManager var0) {
      return var0.parentDisplay;
   }

   private abstract class MenuAction implements ActionListener {
      private MenuAction() {
      }

      public final void actionPerformed(ActionEvent var1) {
         TreePath[] var2 = PluginManager.this.runningPlugins.getSelectionPaths();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               TreePath var4 = var2[var3];
               PluginManager.PluginTreeNode var5 = (PluginManager.PluginTreeNode)var4.getLastPathComponent();
               Plugin var6 = var5.getPlugin();
               PluginWorkbench var7 = var5.getWorkbench();
               if (!this.veto(var6)) {
                  if (this.getPluginWorkbenchAction() != null) {
                     var7.enqueAction(this.getPluginWorkbenchAction(), this.addParams(var6, this.getParams()));
                  }

                  if (this.getWorkbenchAction() != null) {
                     var7.enqueAction(this.getWorkbenchAction(), this.addParams(var6, this.getParams()));
                  }

                  PluginManager.this.clearMessageLabel();
               }
            }

         }
      }

      private final Object[] addParams(Object var1, Object[] var2) {
         Object[] var3 = new Object[var2.length + 1];
         var3[0] = var1;

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4 + 1] = var2[var4];
         }

         return var3;
      }

      protected boolean veto(Plugin var1) {
         return false;
      }

      protected PluginWorkbench.PluginWorkbenchAction getPluginWorkbenchAction() {
         return null;
      }

      protected WorkbenchAction getWorkbenchAction() {
         return null;
      }

      protected abstract Object[] getParams();

      // $FF: synthetic method
      MenuAction(Object var2) {
         this();
      }
   }

   private class PluginTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
      private PluginTreeCellRenderer() {
      }

      public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
         Component var8 = super.getTreeCellRendererComponent(var1, var2, var3, var4, var5, var6, var7);
         if (var8 instanceof JLabel && var2 instanceof PluginManager.PluginTreeNode) {
            JLabel var9 = (JLabel)var8;
            Plugin var10 = ((PluginManager.PluginTreeNode)var2).getPlugin();
            if (var10 != null) {
               var9.setText(var10.getName());
               var9.setIcon(ImageManager.getIcon(var10.getLogo(), PluginManager.this.imageToUse));
            }
         }

         return var8;
      }

      // $FF: synthetic method
      PluginTreeCellRenderer(Object var2) {
         this();
      }
   }

   private class PluginTreeNode implements TreeNode, ListDataListener {
      PluginManager.PluginTreeNode parent1;
      PluginWorkbench bench;
      ListModel model = null;
      Plugin plugin = null;
      Vector<PluginManager.PluginTreeNode> children = null;

      public Plugin getPlugin() {
         return this.plugin;
      }

      public PluginWorkbench getWorkbench() {
         return this.bench;
      }

      PluginTreeNode(PluginWorkbench var2, PluginManager.PluginTreeNode var3) {
         this.children = new Vector();
         this.parent1 = var3;
         this.bench = var2;
         this.setModel(var2);
      }

      PluginTreeNode(Plugin var2, PluginWorkbench var3, PluginManager.PluginTreeNode var4) {
         this.plugin = var2;
         this.parent1 = var4;
         this.children = new Vector();
         if (var2 instanceof PluginContainer) {
            PluginContainer var5 = (PluginContainer)var2;
            this.setModel(var5.getChildWorkbench());
         }

         this.bench = var3;
      }

      void setModel(PluginWorkbench var1) {
         if (var1 != null) {
            this.bench = var1;
            if (this.model != null) {
               this.model.removeListDataListener(this);
            }

            this.model = var1.getChildPlugins();
            if (this.model != null) {
               this.model.addListDataListener(this);
               this.addAll();
            }

         }
      }

      private void addAll() {
         for(int var1 = 0; var1 < this.model.getSize(); ++var1) {
            PluginContainer var2 = (PluginContainer)this.plugin;
            PluginWorkbench var3 = this.bench;
            if (var2 != null) {
               var3 = var2.getChildWorkbench();
            }

            this.children.add(PluginManager.this.new PluginTreeNode((Plugin)this.model.getElementAt(var1), var3, this));
            if (PluginManager.this.runningModel != null) {
               PluginManager.this.runningModel.nodesWereInserted(this, new int[]{var1});
            }
         }

      }

      public TreeNode getChildAt(int var1) {
         return (TreeNode)this.children.get(var1);
      }

      public int getChildCount() {
         return this.children.size();
      }

      public TreeNode getParent() {
         return this.parent1;
      }

      public int getIndex(TreeNode var1) {
         return this.children.indexOf(var1);
      }

      public boolean getAllowsChildren() {
         return true;
      }

      public boolean isLeaf() {
         return this.children.size() == 0;
      }

      public Enumeration<?> children() {
         return this.children.elements();
      }

      public void intervalAdded(ListDataEvent var1) {
         for(int var2 = var1.getIndex0(); var2 <= var1.getIndex1(); ++var2) {
            PluginContainer var3 = (PluginContainer)this.plugin;
            PluginWorkbench var4 = PluginManager.this.pluginWorkbench;
            if (var3 != null) {
               var4 = var3.getChildWorkbench();
            }

            this.children.add(var2, PluginManager.this.new PluginTreeNode((Plugin)this.model.getElementAt(var2), var4, this));
            if (PluginManager.this.runningModel != null) {
               PluginManager.this.runningModel.nodesWereInserted(this, new int[]{var2});
            }
         }

      }

      public void intervalRemoved(ListDataEvent var1) {
         for(int var2 = var1.getIndex1(); var2 >= var1.getIndex0(); --var2) {
            Object var3 = this.children.remove(var2);
            if (PluginManager.this.runningModel != null) {
               PluginManager.this.runningModel.nodesWereRemoved(this, new int[]{var2}, new Object[]{var3});
            }
         }

      }

      public void contentsChanged(ListDataEvent var1) {
         this.intervalRemoved(var1);
         this.intervalAdded(var1);
      }
   }
}
