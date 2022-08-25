package ddb.dsz.plugin.commandviewer;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class CommandViewerHost extends SingleTargetImpl {
   public static final String COMMAND_VIEWER = "images/konsole.png";
   public static final String PAUSED = "images/player_pause.png";
   public static final String TASKED = "images/player_end.png";
   public static final String RUNNING = "images/player_play.png";
   public static final String BANNER = "images/flag.png";
   JTree commandTree;
   DefaultTreeModel commandModel;
   DefaultMutableTreeNode commandRoot;
   Map<Task, DefaultMutableTreeNode> taskToNode;
   Icon running;
   Icon paused;
   Icon tasked;
   Icon banner;

   public CommandViewerHost(HostInfo var1, CoreController var2) {
      super(var1, var2);
      this.tasked = ImageManager.getIcon("images/player_end.png", var2.getLabelImageSize());
      this.paused = ImageManager.getIcon("images/player_pause.png", var2.getLabelImageSize());
      this.running = ImageManager.getIcon("images/player_play.png", var2.getLabelImageSize());
      this.banner = ImageManager.getIcon("images/flag.png", var2.getLabelImageSize());
      this.commandRoot = new DefaultMutableTreeNode("Currently running commands:");
      this.commandModel = new DefaultTreeModel(this.commandRoot);
      this.commandTree = new JTree(this.commandModel);
      this.taskToNode = new Hashtable();
      JScrollPane var3 = new JScrollPane(this.commandTree);
      super.setDisplay(var3);
      var2.setupKeyBindings(this.commandTree);
      var2.setupKeyBindings(var3);
      this.setupStandardActionMaps(this.commandTree);
      this.setupStandardActionMaps(var3);
      this.commandTree.addTreeWillExpandListener(new CommandViewerHost.CollapseRootVeto());
      this.commandTree.setCellRenderer(new CommandViewerHost.TaskRenderer());
   }

   private void evaluateTask(final Task var1) {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               CommandViewerHost.this.evaluateTask(var1);
            }
         });
      } else if (var1 != null) {
         DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.taskToNode.get(var1);
         if (var2 != null) {
            this.commandModel.nodeChanged(var2);
         }

         DefaultMutableTreeNode var3;
         switch(var1.getState()) {
         case FAILED:
         case KILLED:
         case SUCCEEDED:
            if (var2 != null) {
               if (var2.getChildCount() > 0) {
                  var3 = (DefaultMutableTreeNode)var2.getParent();
                  int var4 = -1;
                  if (var3 != null) {
                     var4 = var3.getIndex(var2);
                  }

                  for(int var5 = var2.getChildCount() - 1; var4 != -1 && var2.getChildCount() > 0; --var5) {
                     MutableTreeNode var6 = (MutableTreeNode)var2.getChildAt(var5);
                     this.commandModel.removeNodeFromParent(var6);
                     this.commandModel.insertNodeInto(var6, var3, var4++);
                  }
               }

               this.commandModel.removeNodeFromParent(var2);
               this.taskToNode.remove(var1);
            }

            return;
         default:
            if (var2 == null) {
               var2 = new DefaultMutableTreeNode(var1);
               this.taskToNode.put(var1, var2);
               if (var1.getParentTask() == null) {
                  var3 = this.commandRoot;
               } else {
                  var3 = (DefaultMutableTreeNode)this.taskToNode.get(var1.getParentTask());
                  if (var3 == null) {
                     var3 = this.commandRoot;
                  }
               }

               this.commandModel.insertNodeInto(var2, var3, var3.getChildCount());
               TreeNode[] var7 = this.commandModel.getPathToRoot(var2.getParent());
               this.commandTree.expandPath(new TreePath(var7));
            } else {
               this.commandModel.nodeChanged(var2);
            }

         }
      }
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      if (commandEvent != null) {
         if (!commandEvent.getId().equals(TaskId.UNINITIALIZED_ID)) {
            this.evaluateTask(this.core.getTaskById(commandEvent.getId()));
         }
      }
   }

   public void addTasks(Collection<Task> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Task var3 = (Task)var2.next();
         this.evaluateTask(var3);
      }

   }

   private void killSelected() {
      TreePath[] var1 = this.commandTree.getSelectionPaths();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TreePath var4 = var1[var3];
         if (var4.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode var5 = (DefaultMutableTreeNode)DefaultMutableTreeNode.class.cast(var4.getLastPathComponent());
            if (var5.getUserObject() instanceof Task) {
               Task var6 = (Task)Task.class.cast(var5.getUserObject());

               try {
                  this.core.killCommand(var6);
               } catch (DispatcherException var8) {
                  this.core.logEvent(Level.SEVERE, CommandViewerHost.class.getSimpleName(), var8.getMessage(), var8);
               }
            }
         }
      }

   }

   private void setupStandardActionMaps(JComponent var1) {
      ActionMap var2 = var1.getActionMap();
      var2.put("kill command", new AbstractAction("kill command") {
         public void actionPerformed(ActionEvent var1) {
            CommandViewerHost.this.killSelected();
         }
      });
   }

   private final class TaskRenderer implements TreeCellRenderer {
      DefaultTreeCellRenderer def;

      private TaskRenderer() {
         this.def = new DefaultTreeCellRenderer();
      }

      public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
         Component var8 = this.def.getTreeCellRendererComponent(var1, var2, var3, var4, var5, var6, var7);
         if (var8 instanceof JLabel && var2 instanceof DefaultMutableTreeNode) {
            JLabel var9 = (JLabel)var8;
            DefaultMutableTreeNode var10 = (DefaultMutableTreeNode)var2;
            if (var10.getUserObject() instanceof Task) {
               Task var11 = (Task)var10.getUserObject();
               var9.setText(var11.getId() + ": " + var11.getTypedCommand());
               switch(var11.getState()) {
               case PAUSED:
                  var9.setIcon(CommandViewerHost.this.paused);
                  break;
               case RUNNING:
                  var9.setIcon(CommandViewerHost.this.running);
                  break;
               case TASKED:
                  var9.setIcon(CommandViewerHost.this.tasked);
                  break;
               default:
                  var9.setIcon((Icon)null);
               }
            } else {
               var9.setIcon(CommandViewerHost.this.banner);
            }
         }

         return var8;
      }

      // $FF: synthetic method
      TaskRenderer(Object var2) {
         this();
      }
   }

   private final class CollapseRootVeto implements TreeWillExpandListener {
      private CollapseRootVeto() {
      }

      public void treeWillExpand(TreeExpansionEvent var1) throws ExpandVetoException {
      }

      public void treeWillCollapse(TreeExpansionEvent var1) throws ExpandVetoException {
         if (var1.getPath().getLastPathComponent().equals(CommandViewerHost.this.commandRoot)) {
            throw new ExpandVetoException(var1);
         }
      }

      // $FF: synthetic method
      CollapseRootVeto(Object var2) {
         this();
      }
   }
}
