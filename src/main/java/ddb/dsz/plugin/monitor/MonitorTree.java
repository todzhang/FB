package ddb.dsz.plugin.monitor;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class MonitorTree implements TreeModel {
   EventListenerList listeners = new EventListenerList();
   MonitorHost monitor;
   CoreController core;
   List<MonitorTarget> root = new Vector();
   Method inserted;
   Method changed;
   Method removed;
   Method structureChanged;

   public MonitorTree(MonitorHost var1, CoreController var2) {
      this.monitor = var1;
      this.core = var2;
      Class var3 = TreeModelListener.class;

      try {
         this.inserted = var3.getMethod("treeNodesInserted", TreeModelEvent.class);
         this.changed = var3.getMethod("treeNodesChanged", TreeModelEvent.class);
         this.removed = var3.getMethod("treeNodesRemoved", TreeModelEvent.class);
         this.structureChanged = var3.getMethod("treeStructureChanged", TreeModelEvent.class);
      } catch (NoSuchMethodException var5) {
      }

   }

   public Object getRoot() {
      return this.root;
   }

   public Object getChild(Object var1, int var2) {
      if (var1 == this.root) {
         return this.root.get(var2);
      } else {
         return var1 instanceof MonitorTarget ? ((MonitorTarget)MonitorTarget.class.cast(var1)).tasks.get(var2) : null;
      }
   }

   public int getChildCount(Object var1) {
      if (var1 == this.root) {
         return this.root.size();
      } else {
         return var1 instanceof MonitorTarget ? ((MonitorTarget)MonitorTarget.class.cast(var1)).tasks.size() : 0;
      }
   }

   public boolean isLeaf(Object var1) {
      return this.getChildCount(var1) == 0;
   }

   public void valueForPathChanged(TreePath var1, Object var2) {
      System.out.println("Value changed");
   }

   public int getIndexOfChild(Object var1, Object var2) {
      if (var1 == this.root) {
         return this.root.indexOf(var2);
      } else {
         return var1 instanceof MonitorTarget ? ((MonitorTarget)MonitorTarget.class.cast(var1)).tasks.indexOf(var2) : -1;
      }
   }

   public void addTreeModelListener(TreeModelListener var1) {
      this.listeners.add(TreeModelListener.class, var1);
   }

   public void removeTreeModelListener(TreeModelListener var1) {
      this.listeners.remove(TreeModelListener.class, var1);
   }

   public void addTarget(String var1) {
      if (var1 != null) {
         MonitorTarget var2 = new MonitorTarget();
         var2.id = var1;
         synchronized(this) {
            int var3 = this.root.size();
            this.root.add(var2);
         }

         this.fireNodeInserted(new TreeModelEvent(this, new Object[]{this.root, var2}));
      }
   }

   public void addMonitoredTask(Task var1) {
      if (var1 != null) {
         MonitorTask var2 = new MonitorTask();
         MonitorTarget var3 = null;
         var2.task = var1;
         synchronized(this) {
            Iterator var5 = this.root.iterator();

            while(var5.hasNext()) {
               MonitorTarget var6 = (MonitorTarget)var5.next();
               if (var6.id.equalsIgnoreCase(var1.getHost().getId())) {
                  var3 = var6;
                  break;
               }
            }
         }

         if (var3 == null) {
            this.addTarget(var1.getHost().getId());
            this.addMonitoredTask(var1);
         } else {
            int var4 = var3.tasks.size();
            var2.parent = var3;
            var3.tasks.add(var2);
            this.fireNodeInserted(new TreeModelEvent(this, new Object[]{this.root, var3, var2}));
         }
      }
   }

   protected void fireNodeInserted(TreeModelEvent var1) {
      this.fire(this.inserted, var1);
   }

   protected void fireNodeChanged(TreeModelEvent var1) {
      this.fire(this.changed, var1);
   }

   protected void fireNodeRemoved(TreeModelEvent var1) {
      this.fire(this.removed, var1);
   }

   protected void fireStructureChanged(TreeModelEvent var1) {
      this.fire(this.structureChanged, var1);
   }

   protected final void fire(final Method var1, final TreeModelEvent var2) {
      if (var1 != null) {
         if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  MonitorTree.this.fire(var1, var2);
               }
            });
         }

         TreeModelListener[] var3 = (TreeModelListener[])this.listeners.getListeners(TreeModelListener.class);
         TreeModelListener[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            TreeModelListener var7 = var4[var6];

            try {
               var1.invoke(var7, var2);
            } catch (Exception var9) {
               Logger.getLogger("dsz").log(Level.SEVERE, (String)null, var9);
               var9.printStackTrace();
            }
         }

      }
   }
}
