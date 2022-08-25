package ddb.dsz.plugin.monitor;

import ddb.console.OptionPane;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.library.console.ConsoleOutputPane;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.events.AutoScroll;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.util.tablefilter.FilteredTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/monitor.png")
@DszName("Monitor")
@DszDescription("Monitored Command Output")
@DszUserStartable(false)
public class MonitorHost extends SingleTargetImpl {
   OutputTableModel outputTableModel;
   JTable outputTable;
   JSplitPane splitPane;
   JPanel mainPanel;
   JFrame outputPopupWindow;
   ConsoleOutputPane detailedOutputArea;
   JScrollPane outputScrollPane;
   DataTransformer transformer = DataTransformer.newInstance();
   JCheckBox bAutoScroll = new JCheckBox("AutoScroll", true);
   Predicate isMonitoredTask = new Predicate() {
      public boolean evaluate(Object var1) {
         if (!(var1 instanceof Task)) {
            return false;
         } else {
            Task var2 = (Task)Task.class.cast(var1);
            return var2.getGuiFlagValue("monitor") != null;
         }
      }
   };
   Predicate shouldShowCommandOutput = new Predicate() {
      public boolean evaluate(Object var1) {
         if (var1 == null) {
            return false;
         } else if (!(var1 instanceof Task)) {
            return false;
         } else {
            Task var2 = (Task)var1;
            if (var2 == null) {
               return false;
            } else {
               CheckNode var3 = (CheckNode)MonitorHost.this.taskLookup.get(var2);
               return var3 == null ? false : var3.isSelected();
            }
         }
      }
   };
   FilteredTableModel filteredOutput;
   AutoScroll auto;
   JTree tree;
   CheckNode live = new CheckNode("Live Tasks");
   CheckNode complete = new CheckNode("Completed Tasks");
   final Map<Task, CheckNode> taskLookup = Collections.synchronizedMap(new HashMap());
   DefaultTreeModel model;
   MultipleTargetPlugin parent;
   public static final String MONITOR_ICON = "images/monitor.png";
   public static final int MAXIMUM_DEFAULT = 10000;

   public MonitorHost(HostInfo var1, CoreController var2, MultipleTargetPlugin var3) {
      super(var1, var2);
      this.parent = var3;
      int var4 = 10000;
      Object var5 = var2.getOption(MonitorWindow2.class, "-MonitorMaximumLines");
      if (var5 instanceof Integer) {
         var4 = (Integer)var5;
      } else if (var5 instanceof String) {
         try {
            var4 = Integer.parseInt((String)var5);
         } catch (Exception var9) {
         }
      }

      var2.logEvent(Level.FINER, "Monitor:  init");
      this.complete.setSelected(false);
      this.tree = new JTree(new CheckNode[0]);
      this.model = (DefaultTreeModel)this.tree.getModel();
      MutableTreeNode var6 = (MutableTreeNode)this.tree.getModel().getRoot();
      this.model.insertNodeInto(this.live, var6, 0);
      this.model.insertNodeInto(this.complete, var6, 1);
      this.tree.expandPath(new TreePath(this.model.getPathToRoot(var6)));
      this.tree.setCellRenderer(new CheckRenderer());
      this.tree.getSelectionModel().setSelectionMode(1);
      this.tree.putClientProperty("JTree.lineStyle", "Angled");
      this.tree.addMouseListener(new NodeSelectionListener(this.tree));
      this.tree.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            if (!var1.isPopupTrigger() && var1.getButton() == 1) {
               if (var1.isShiftDown()) {
                  int var2 = var1.getX();
                  int var3 = var1.getY();
                  int var4 = MonitorHost.this.tree.getRowForLocation(var2, var3);
                  TreePath var5 = MonitorHost.this.tree.getPathForRow(var4);
                  if (var5 != null) {
                     CheckNode var6 = (CheckNode)var5.getLastPathComponent();
                     if (var6.getUserObject() instanceof Task) {
                        MonitorHost.this.showCommand((Task)var6.getUserObject());
                     }
                  }

               }
            }
         }
      });
      this.tree.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               this.maybePopup(var1);
            }

         }

         public void mousePressed(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               this.maybePopup(var1);
            }

         }

         public void mouseReleased(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               this.maybePopup(var1);
            }

         }

         private void maybePopup(MouseEvent var1) {
            int var2 = var1.getX();
            int var3 = var1.getY();
            int var4 = MonitorHost.this.tree.getRowForLocation(var2, var3);
            JPopupMenu var5 = null;
            TreePath var6 = MonitorHost.this.tree.getPathForRow(var4);
            if (var6 != null) {
               CheckNode var7 = (CheckNode)var6.getLastPathComponent();
               if (var7.getUserObject() instanceof Task) {
                  var5 = MonitorHost.this.getPopupMenu((Task)var7.getUserObject(), var7.isSelected());
               } else if (var7.getUserObject() instanceof HostInfo) {
                  var5 = MonitorHost.this.getPopupMenu((HostInfo)var7.getUserObject(), var7.isSelected());
               }
            }

            if (var5 != null) {
               var5.show(MonitorHost.this.tree, var2, var3);
            }

         }
      });
      this.tree.getModel().addTreeModelListener(new TreeModelListener() {
         public void treeNodesChanged(TreeModelEvent var1) {
            MonitorHost.this.filteredOutput.filterChanged();
         }

         public void treeNodesInserted(TreeModelEvent var1) {
            MonitorHost.this.filteredOutput.filterChanged();
         }

         public void treeNodesRemoved(TreeModelEvent var1) {
            MonitorHost.this.filteredOutput.filterChanged();
         }

         public void treeStructureChanged(TreeModelEvent var1) {
            MonitorHost.this.filteredOutput.filterChanged();
         }
      });
      this.outputTableModel = new OutputTableModel(var4);
      this.filteredOutput = new FilteredTableModel(this.outputTableModel);
      this.outputTableModel.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent var1) {
            if (var1.getType() == 1) {
               MonitorHost.this.changeSupport.firePropertyChange("TABBABLE_CONTENT_CHANGED", false, true);
            }

         }
      });
      this.filteredOutput.addFilter(this.shouldShowCommandOutput, new int[]{0});
      this.outputTable = new JTable(this.filteredOutput);
      this.outputTable.setDefaultRenderer(Task.class, new DszTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Task) {
               JLabel var8 = (JLabel)var7;
               Task var9 = (Task) value;
               var8.setText(var9.getCommandName());
               return var7;
            } else {
               return var7;
            }
         }
      });
      this.outputTable.setDefaultRenderer(Long.class, new DszTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Long) {
               JLabel var8 = (JLabel)var7;
               var8.setHorizontalAlignment(4);
               Long var9 = (Long) value;
               String var10 = "";
               if (var9 < 1000L) {
                  var10 = String.format("0s.%03d", var9);
               } else {
                  var10 = String.format(".%03d", var9 % 1000L);
                  var9 = var9 / 1000L;
                  if (var9 < 60L) {
                     var10 = String.format("%ds%s", var9, var10);
                  } else {
                     var10 = String.format("%02ds%s", var9 % 60L, var10);
                     var9 = var9 / 60L;
                     if (var9 < 60L) {
                        var10 = String.format("%dm%s", var9, var10);
                     } else {
                        var10 = String.format("%02dm%s", var9 % 60L, var10);
                        var9 = var9 / 60L;
                        var10 = String.format("%dh%s", var9, var10);
                     }
                  }
               }

               var8.setText(var10);
               return var7;
            } else {
               return var7;
            }
         }
      });
      this.outputTable.setDefaultRenderer(String.class, new DszTableCellRenderer() {
         static final int MAX_OUTPUT_LENGTH = 100;
         static final String ELIPSE = "...";

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Task) {
               JLabel var8 = (JLabel)var7;
               String var9 = (String) value;
               if (var9.length() > 100) {
                  var9 = String.format("%s %s", var9.substring(0, 100 - ("...".length() + 1)), "...");
               }

               var8.setText(var9);
               return var7;
            } else {
               return var7;
            }
         }
      });
      this.setColumnWidth(OutputTableColumns.COMMAND, "wwwwwwwwwwwwww", true);
      this.setColumnWidth(OutputTableColumns.WHEN, "9h99m99s.999", true);
      this.outputScrollPane = new JScrollPane(this.outputTable);
      JPanel var7 = new JPanel(new BorderLayout());
      var7.add(this.outputScrollPane, "Center");
      JPanel var8 = new JPanel();
      var7.add(var8, "South");
      var8.setAlignmentY(0.0F);
      var8.add(this.bAutoScroll);
      this.splitPane = new JSplitPane(1, true, new JScrollPane(this.tree), var7);
      this.splitPane.setOneTouchExpandable(true);
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            MonitorHost.this.splitPane.removeComponentListener(this);
            MonitorHost.this.splitPane.setDividerLocation(0.2D);
         }
      });
      this.mainPanel = new JPanel(new BorderLayout());
      this.mainPanel.add(this.splitPane);
      super.setDisplay(this.mainPanel);
      this.setupOutputPopup();
      this.outputTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            MonitorHost.this.outputDoubleClickAction(var1);
         }
      });
      this.auto = new AutoScroll(this.outputScrollPane);
      this.bAutoScroll.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.auto.setScroll(MonitorHost.this.bAutoScroll.isSelected());
         }
      });
   }

   private JPopupMenu getPopupMenu(final Task var1, boolean var2) {
      JPopupMenu var3 = new JPopupMenu();
      JMenuItem var4 = new JMenuItem("Show All");
      JMenuItem var5 = new JMenuItem("Hide All");
      JMenuItem var6 = new JMenuItem("Show Only This Task");
      JMenuItem var7 = new JMenuItem("Hide This Task");
      JMenuItem var8 = new JMenuItem("View Task Output");
      var3.add(var4);
      var3.add(var5);
      var3.addSeparator();
      var3.add(var6);
      var3.add(var7);
      var3.addSeparator();
      var3.add(var8);
      var4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            MonitorHost.this.setHashSettings(MonitorHost.this.taskLookup, true);
            MonitorHost.this.redrawTree();
         }
      });
      var5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.setHashSettings(MonitorHost.this.taskLookup, false);
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            MonitorHost.this.redrawTree();
         }
      });
      var6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1x) {
            MonitorHost.this.setHashSettings(MonitorHost.this.taskLookup, false);
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            ((CheckNode)MonitorHost.this.taskLookup.get(var1)).setSelected(true);
            MonitorHost.this.redrawTree();
         }
      });
      var7.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1x) {
            ((CheckNode)MonitorHost.this.taskLookup.get(var1)).setSelected(false);
            MonitorHost.this.redrawTree();
         }
      });
      var8.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1x) {
            MonitorHost.this.showCommand(var1);
         }
      });
      return var3;
   }

   private void setHashSettings(Map<?, CheckNode> var1, boolean var2) {
      synchronized(var1) {
         Iterator var4 = var1.values().iterator();

         while(var4.hasNext()) {
            CheckNode var5 = (CheckNode)var4.next();
            var5.setSelected(var2);
         }

      }
   }

   private JPopupMenu getPopupMenu(HostInfo var1, boolean var2) {
      JPopupMenu var3 = new JPopupMenu();
      JMenuItem var4 = new JMenuItem("Show All");
      JMenuItem var5 = new JMenuItem("Hide All");
      JMenuItem var6 = new JMenuItem("Show Only This Host");
      JMenuItem var7 = new JMenuItem("Hide This Host");
      var3.add(var4);
      var3.add(var5);
      var3.addSeparator();
      var3.add(var6);
      var3.add(var7);
      var4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            MonitorHost.this.redrawTree();
         }
      });
      var5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            MonitorHost.this.redrawTree();
         }
      });
      var6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.complete.setSelected(true);
            MonitorHost.this.live.setSelected(true);
            MonitorHost.this.redrawTree();
         }
      });
      var7.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MonitorHost.this.redrawTree();
         }
      });
      return var3;
   }

   private void redrawTree() {
      this.tree.revalidate();
      this.tree.repaint();
      this.filteredOutput.filterChanged();
   }

   private void setupOutputPopup() {
      this.core.logEvent(Level.FINER, "Monitor:  SetupOutputPopup()");
      this.outputPopupWindow = new JFrame("Monitored Command Output");
      this.outputPopupWindow.setSize(800, 250);
      this.outputPopupWindow.setDefaultCloseOperation(1);
      this.detailedOutputArea = new ConsoleOutputPane(this.core, Integer.MAX_VALUE);
      this.detailedOutputArea.setTheme(OptionPane.getInstance().getSharedTheme());
      this.outputPopupWindow.getContentPane().add(this.detailedOutputArea);
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      Task var2 = this.core.getTaskById(commandEvent.getId());
      this.addTask(var2);
   }

   public void addTask(final Task var1) {
      if (this.isMonitoredTask.evaluate(var1) && var1 != null && var1.getHost() != null) {
         if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  MonitorHost.this.addTask(var1);
               }
            });
         } else {
            boolean var2 = false;
            CheckNode var3 = null;
            synchronized(this.taskLookup) {
               var3 = (CheckNode)this.taskLookup.get(var1);
               if (var3 == null) {
                  var2 = true;
                  var3 = new CheckNode(var1);
                  var3.setSelectionMode(CheckNode.SelectionMode.DigOutSelection);
                  var3.setSelectionType(CheckNode.SelectionType.OnTrue);
                  this.taskLookup.put(var1, var3);
                  this.addTask(this.live, var3);
               }
            }

            switch(var1.getState()) {
            case FAILED:
            case KILLED:
            case SUCCEEDED:
               this.removeTask(this.live, var3);
               this.addTask(this.complete, var3);
            default:
               if (!var2) {
                  return;
               }

               if (this.transformer != null) {
                  this.transformer.addClosure(ClosureFactory.newDisplayClosure(this.core, var1, new Closure() {
                     public void execute(Object var1x) {
                        MonitorHost.this.updateDisplay(var1, var1x.toString(), Calendar.getInstance());
                     }
                  }));
                  this.transformer.addTask(var1);
               }
            }
         }

      }
   }

   private void expand(CheckNode var1) {
      if (var1.getChildCount() == 1) {
         TreeNode[] var2 = var1.getPath();
         if (var2 != null && var2.length != 0) {
            TreePath var3 = new TreePath(var2);
            if (var3 != null) {
               this.tree.expandPath(var3);
            }
         }
      }
   }

   private void addTask(CheckNode var1, CheckNode var2) {
      this.model.insertNodeInto(var2, var1, var1.getChildCount());
      this.expand(var1);
   }

   private void removeTask(CheckNode var1, CheckNode var2) {
      this.model.removeNodeFromParent(var2);
   }

   void updateDisplay(Task var1, String var2, Calendar var3) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            MonitorHost.super.fireContentsChanged();
         }
      });
      if (var2 != null && var2.length() != 0) {
         EventQueue.invokeLater(new MonitorHost.MonitorUpdate(var1, var2, var3));
      }
   }

   void outputDoubleClickAction(MouseEvent var1) {
      if (var1.getClickCount() >= 2) {
         Task var2 = (Task)this.outputTable.getValueAt(this.outputTable.getSelectedRow(), OutputTableColumns.COMMAND.ordinal());
         this.showCommand(var2);
      }
   }

   void showCommand(Task var1) {
      if (var1 != null) {
         List var2 = this.outputTableModel.getOutputsFor(var1);
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            MonitoredCommandOutput var5 = (MonitoredCommandOutput)var4.next();
            var3.append(String.format("%s\n", var5.getCommandOutput()));
         }

         this.detailedOutputArea.clearAndReplace(var3.toString());
         if (!this.outputPopupWindow.isVisible()) {
            this.outputPopupWindow.setVisible(true);
         }

      }
   }

   private void setColumnWidth(Enum<?> var1, String var2, boolean var3) {
      TableColumn var4 = this.outputTable.getColumnModel().getColumn(var1.ordinal());
      JLabel var5 = new JLabel(var2);
      var4.setPreferredWidth(var5.getPreferredSize().width + 10);
      if (var3) {
         var4.setMaxWidth(var5.getPreferredSize().width + 15);
         var4.setMinWidth(var5.getPreferredSize().width + 5);
      }

   }

   private class MonitorUpdate implements Runnable {
      Task task;
      String[] lines;
      Calendar cal;

      public MonitorUpdate(Task var2, String var3, Calendar var4) {
         this.task = var2;
         this.lines = var3.split("\\n");
         this.cal = var4;
      }

      public void run() {
         long var1 = Calendar.getInstance().getTimeInMillis() - this.task.getCreationTime();
         String[] var3 = this.lines;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            MonitoredCommandOutput var7 = new MonitoredCommandOutput(this.task, var1, var6);
            MonitorHost.this.outputTableModel.addRecord(var7);
         }

      }
   }
}
