package ddb.dsz.plugin.logviewer.gui.detail;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class VariableFilterTreeModel implements TreeModel, TreeModelListener {
   EventListenerList listenerList = new EventListenerList();
   TreeModel child;
   VariableFilterTreeModel.FilterTreeNode root;
   List<String> filter = new Vector();

   public VariableFilterTreeModel(TreeModel child) {
      this.child = child;
      this.child.addTreeModelListener(this);
      this.update();
   }

   public void addTreeModelListener(TreeModelListener l) {
      this.listenerList.add(TreeModelListener.class, l);
   }

   public Object getChild(Object parent, int index) {
      return parent instanceof TreeNode ? ((TreeNode)TreeNode.class.cast(parent)).getChildAt(index) : null;
   }

   public int getChildCount(Object parent) {
      return parent instanceof TreeNode ? ((TreeNode)TreeNode.class.cast(parent)).getChildCount() : 0;
   }

   public int getIndexOfChild(Object parent, Object child2) {
      return parent instanceof TreeNode ? ((TreeNode)TreeNode.class.cast(parent)).getIndex((TreeNode)TreeNode.class.cast(child2)) : -1;
   }

   public Object getRoot() {
      return this.root;
   }

   public boolean isLeaf(Object node) {
      return node instanceof TreeNode ? ((TreeNode)TreeNode.class.cast(node)).isLeaf() : true;
   }

   public void removeTreeModelListener(TreeModelListener l) {
      this.listenerList.remove(TreeModelListener.class, l);
   }

   public void valueForPathChanged(TreePath path, Object newValue) {
   }

   public void treeNodesChanged(TreeModelEvent e) {
      this.update();
   }

   public void treeNodesInserted(TreeModelEvent e) {
      this.update();
   }

   public void treeNodesRemoved(TreeModelEvent e) {
      this.update();
   }

   public void treeStructureChanged(TreeModelEvent e) {
      this.update();
   }

   public void update() {
      this.root = new VariableFilterTreeModel.FilterTreeNode((MutableTreeNode)MutableTreeNode.class.cast(this.child.getRoot()), (VariableFilterTreeModel.FilterTreeNode)null, this.filter);
      this.fireTreeStructureChanged();
   }

   protected void fireTreeStructureChanged() {
      TreeModelListener[] listeners = (TreeModelListener[])this.listenerList.getListeners(TreeModelListener.class);
      TreeModelEvent e = null;
      TreeModelListener[] arr$ = listeners;
      int len$ = listeners.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         TreeModelListener l = arr$[i$];
         if (e == null) {
            e = new TreeModelEvent(this, new Object[]{this.root});
         }

         l.treeStructureChanged(e);
      }

   }

   public void applyFilter(String path) {
      if (path != null && path.length() != 0) {
         this.applyFilter(path.split("::"));
      } else {
         this.filter.clear();
         this.update();
      }

   }

   public void applyFilter(String[] path) {
      List<String> list = new ArrayList(path.length);
      String[] arr$ = path;
      int len$ = path.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String s = arr$[i$];
         list.add(s);
      }

      this.applyFilter((List)list);
   }

   public void applyFilter(List<String> path) {
      this.filter.clear();
      this.filter.addAll(path);
      this.update();
   }

   class FilterTreeNode implements MutableTreeNode {
      MutableTreeNode childNode;
      List<VariableFilterTreeModel.FilterTreeNode> children;
      VariableFilterTreeModel.FilterTreeNode parent;

      public FilterTreeNode(MutableTreeNode childNode, VariableFilterTreeModel.FilterTreeNode parent, List<String> filter) {
         this.childNode = childNode;
         this.parent = parent;
         this.children = new Vector();
         Enumeration<?> c = childNode.children();
         String phrase = null;
         int index = -2;
         if (filter.size() > 0) {
            String filterPhrase = (String)filter.get(0);
            int start = filterPhrase.indexOf(91);
            int stop = filterPhrase.indexOf(93);
            if (start > 0) {
               phrase = filterPhrase.substring(0, start);
               if (stop > start) {
                  try {
                     index = Integer.parseInt(filterPhrase.substring(start + 1, stop));
                  } catch (Exception var12) {
                     JOptionPane.showMessageDialog((Component)null, String.format("The filter '%s' has an incorrect or illegible index", filterPhrase), "Invalid filter", 2);
                     filter.clear();
                     phrase = null;
                     index = -2;
                  }
               } else {
                  JOptionPane.showMessageDialog((Component)null, String.format("The filter '%s' has an incorrect or illegible index", filterPhrase), "Invalid filter", 2);
                  filter.clear();
                  phrase = null;
                  index = -2;
               }
            } else {
               phrase = filterPhrase;
            }
         }

         while(true) {
            while(c.hasMoreElements()) {
               MutableTreeNode newNode = (MutableTreeNode)MutableTreeNode.class.cast(c.nextElement());
               if (phrase != null) {
                  if (!phrase.equalsIgnoreCase(newNode.toString()) || index == -1) {
                     continue;
                  }

                  if (index > 0) {
                     --index;
                     continue;
                  }

                  if (index == 0) {
                     --index;
                  }
               }

               if (filter.size() == 0) {
                  this.children.add(VariableFilterTreeModel.this.new FilterTreeNode(newNode, this, filter));
               } else {
                  this.children.add(VariableFilterTreeModel.this.new FilterTreeNode(newNode, this, filter.subList(1, filter.size())));
               }
            }

            return;
         }
      }

      public Enumeration<?> children() {
         return new Enumeration<Object>() {
            Iterator<VariableFilterTreeModel.FilterTreeNode> iter;

            {
               this.iter = FilterTreeNode.this.children.iterator();
            }

            public boolean hasMoreElements() {
               return this.iter.hasNext();
            }

            public Object nextElement() {
               return this.iter.next();
            }
         };
      }

      public boolean getAllowsChildren() {
         return this.childNode.getAllowsChildren();
      }

      public TreeNode getChildAt(int childIndex) {
         return (TreeNode)this.children.get(childIndex);
      }

      public int getChildCount() {
         return this.children.size();
      }

      public int getIndex(TreeNode node) {
         return this.children.indexOf(node);
      }

      public TreeNode getParent() {
         return this.parent;
      }

      public boolean isLeaf() {
         return this.getChildCount() == 0;
      }

      public TreeNode getRawNode() {
         return this.childNode;
      }

      public void insert(MutableTreeNode childNode2, int index) {
         this.childNode.insert(childNode2, index);
         VariableFilterTreeModel.this.update();
      }

      public void remove(int index) {
         this.childNode.remove(index);
         VariableFilterTreeModel.this.update();
      }

      public void remove(MutableTreeNode childNode2) {
         this.childNode.remove(childNode2);
         VariableFilterTreeModel.this.update();
      }

      public void removeFromParent() {
         this.childNode.removeFromParent();
         VariableFilterTreeModel.this.update();
      }

      public void setParent(MutableTreeNode newParent) {
         this.childNode.setParent(newParent);
         VariableFilterTreeModel.this.update();
      }

      public void setUserObject(Object object) {
         this.childNode.setUserObject(object);
      }

      public String toString() {
         return this.childNode.toString();
      }
   }
}
