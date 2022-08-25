package ddb.dsz.plugin.netmapviewer;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.contextmenu.CommandCallbackListener;
import ddb.dsz.core.contextmenu.ContextMenuAction;
import ddb.dsz.core.contextmenu.ContextMenuFactory;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.dsz.plugin.netmapviewer.closures.ArpClosure;
import ddb.dsz.plugin.netmapviewer.closures.IfConfigClosure;
import ddb.dsz.plugin.netmapviewer.closures.NetConnectionsClosure;
import ddb.dsz.plugin.netmapviewer.closures.NetmapClosure;
import ddb.dsz.plugin.netmapviewer.closures.PingClosure;
import ddb.dsz.plugin.netmapviewer.closures.SharesClosure;
import ddb.dsz.plugin.netmapviewer.closures.TracerouteClosure;
import ddb.dsz.plugin.netmapviewer.data.Group;
import ddb.dsz.plugin.netmapviewer.data.Netmap;
import ddb.dsz.plugin.netmapviewer.data.Service;
import ddb.dsz.plugin.netmapviewer.data.User;
import ddb.dsz.plugin.netmapviewer.display.ArpDisplay;
import ddb.dsz.plugin.netmapviewer.display.GroupsDisplay;
import ddb.dsz.plugin.netmapviewer.display.IfConfigDisplay;
import ddb.dsz.plugin.netmapviewer.display.NetmapDisplay;
import ddb.dsz.plugin.netmapviewer.display.PingDisplay;
import ddb.dsz.plugin.netmapviewer.display.PrintableCommandsDisplay;
import ddb.dsz.plugin.netmapviewer.display.ServiceDisplay;
import ddb.dsz.plugin.netmapviewer.display.ShareDisplay;
import ddb.dsz.plugin.netmapviewer.display.TracerouteDisplay;
import ddb.dsz.plugin.netmapviewer.display.UserDisplay;
import ddb.dsz.plugin.netmapviewer.insertion.InsertGroupsNode;
import ddb.dsz.plugin.netmapviewer.insertion.InsertPrintCommandNode;
import ddb.dsz.plugin.netmapviewer.insertion.InsertServicesNode;
import ddb.dsz.plugin.netmapviewer.insertion.InsertUsersNode;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public class NetmapViewerHost extends SingleTargetImpl implements ContextMenuAction, CommandCallbackListener {
   static final String CONTEXT_MENU = "/NetmapViewer/ContextMenu.xml";
   static final Set<String> INTERESTING_COMMANDS;
   private static Set<String> TARGETED_COMMANDS;
   private static Set<String> PRINTABLE_COMMANDS;
   private DataTransformer dataTranslator;
   private DataTransformer targetTranslator;
   public JTree itemList;
   private JTabbedPane nodeDisplay;
   public DefaultMutableTreeNode rootNode;
   private Node root;
   public DefaultMutableTreeNode unknownNode;
   private Node unknown;
   public Node ArpNode = null;
   public DefaultTreeModel treeModel;
   public final Map<String, DefaultMutableTreeNode> nameToNode = new HashMap();

   private synchronized void initializeTransformers() {
      if (this.targetTranslator == null) {
         this.targetTranslator = DataTransformer.newInstance();
      }

      if (this.dataTranslator == null) {
         this.dataTranslator = DataTransformer.newInstance();
      }

   }

   public NetmapViewerHost(HostInfo var1, final CoreController var2) {
      super(var1, var2);
      this.initializeTransformers();
      if (this.dataTranslator != null && this.targetTranslator != null) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               NetmapViewerHost.this.rootNode = new DefaultMutableTreeNode("Network");
               NetmapViewerHost.this.root = new Node();
               NetmapViewerHost.this.nameToNode.put("", NetmapViewerHost.this.rootNode);
               NetmapViewerHost.this.unknownNode = new DefaultMutableTreeNode("Unknown");
               NetmapViewerHost.this.unknown = new Node();
               NetmapViewerHost.this.unknownNode.setUserObject(NetmapViewerHost.this.unknown);
               NetmapViewerHost.this.unknown.setMatchable(false);
               NetmapViewerHost.this.treeModel = new DefaultTreeModel(NetmapViewerHost.this.rootNode);
               NetmapViewerHost.this.treeModel.insertNodeInto(NetmapViewerHost.this.unknownNode, NetmapViewerHost.this.rootNode, 0);
               NetmapViewerHost.this.itemList = new JTree(NetmapViewerHost.this.treeModel);
               NetmapViewerHost.this.nodeDisplay = new JTabbedPane();
               NetmapViewerHost.this.nodeDisplay.setTabLayoutPolicy(1);
               final JSplitPane var1 = new JSplitPane(1, new JScrollPane(NetmapViewerHost.this.itemList), NetmapViewerHost.this.nodeDisplay);
               NetmapViewerHost.this.setDisplay(var1);
               var1.addComponentListener(new ComponentAdapter() {
                  public void componentShown(ComponentEvent var1x) {
                     var1.setDividerLocation(0.33D);
                     var1.removeComponentListener(this);
                  }
               });
               NetmapViewerHost.this.itemList.addTreeSelectionListener(new TreeSelectionListener() {
                  public void valueChanged(TreeSelectionEvent var1) {
                     Component[] var2x = NetmapViewerHost.this.nodeDisplay.getComponents();
                     if (var2x != null) {
                        Component[] var3 = var2x;
                        int var4 = var2x.length;

                        for(int var5 = 0; var5 < var4; ++var5) {
                           Component var6 = var3[var5];
                           if (var6 instanceof NetmapDisplay) {
                              ((NetmapDisplay)NetmapDisplay.class.cast(var6)).setNode((Netmap)null);
                           }
                        }
                     }

                     NetmapViewerHost.this.nodeDisplay.removeAll();
                     Node var8 = null;
                     TreePath var9 = var1.getNewLeadSelectionPath();
                     if (var9 != null) {
                        Object var10 = var9.getLastPathComponent();
                        if (var10 instanceof DefaultMutableTreeNode) {
                           var10 = ((DefaultMutableTreeNode)DefaultMutableTreeNode.class.cast(var10)).getUserObject();
                        }

                        if (var10 instanceof Node) {
                           var8 = (Node)var10;
                        }
                     }

                     if (var8 != null) {
                        if (var8.isUpdated()) {
                           var8.viewed();
                           NetmapViewerHost.this.treeModel.nodeChanged((TreeNode)var9.getLastPathComponent());
                        }

                        if (var8.getNetmapData() != null) {
                           NetmapDisplay var12 = new NetmapDisplay(var2, var8.getNetmapData());
                           NetmapViewerHost.this.nodeDisplay.add("Netmap", var12);
                        }

                        if (var8.getIfConfig() != null) {
                           IfConfigDisplay var13 = new IfConfigDisplay(var8.getIfConfig());
                           NetmapViewerHost.this.nodeDisplay.add("IfConfig", new JScrollPane(var13));
                        }

                        if (var8.getArps().size() > 0) {
                           ArpDisplay var14 = new ArpDisplay(var8.getArps());
                           NetmapViewerHost.this.nodeDisplay.add("Arp", var14);
                        }

                        if (var8.getGroups().size() > 0) {
                           GroupsDisplay var15 = new GroupsDisplay(var2, var8.getGroups());
                           NetmapViewerHost.this.nodeDisplay.add("Groups", var15);
                        }

                        if (var8.getResources().size() > 0) {
                           ShareDisplay var16 = new ShareDisplay(var2, var8.getResources());
                           NetmapViewerHost.this.nodeDisplay.add("Shares", var16);
                        }

                        if (var8.getPings().size() > 0) {
                           PingDisplay var17 = new PingDisplay(var2, var8.getPings());
                           NetmapViewerHost.this.nodeDisplay.add("Ping", var17);
                        }

                        if (var8.getServices().size() > 0) {
                           ServiceDisplay var18 = new ServiceDisplay(var2, var8.getServices());
                           NetmapViewerHost.this.nodeDisplay.add("Services", var18);
                        }

                        if (var8.getTraceroutes().size() > 0) {
                           TracerouteDisplay var19 = new TracerouteDisplay(var2, var8);
                           NetmapViewerHost.this.nodeDisplay.add("Traceroute", var19);
                        }

                        if (var8.getUsers().size() > 0) {
                           UserDisplay var20 = new UserDisplay(var2, var8.getUsers());
                           NetmapViewerHost.this.nodeDisplay.add("Users", var20);
                        }

                        Iterator var21 = var8.getPrintedCommands().iterator();

                        while(var21.hasNext()) {
                           Task var11 = (Task)var21.next();
                           PrintableCommandsDisplay var7 = new PrintableCommandsDisplay(var2, var11, var8.getPrintedCommand(var11));
                           NetmapViewerHost.this.nodeDisplay.add(String.format("%d: %s", var11.getId().getId(), var11.getCommandName()), var7);
                        }

                     }
                  }
               });
               NetmapViewerHost.this.itemList.setCellRenderer(new DefaultTreeCellRenderer() {
                  Font BOLD = null;
                  Font PLAIN = null;
                  Font ITALICS = null;

                  public Component getTreeCellRendererComponent(JTree var1, Object var2x, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
                     if (this.BOLD == null) {
                        this.BOLD = super.getFont().deriveFont(1);
                     }

                     if (this.PLAIN == null) {
                        this.PLAIN = super.getFont().deriveFont(0);
                     }

                     if (this.ITALICS == null) {
                        this.ITALICS = super.getFont().deriveFont(2);
                     }

                     Component var8 = super.getTreeCellRendererComponent(var1, var2x, var3, var4, var5, var6, var7);
                     var8.setFont(this.PLAIN);
                     if (var2x instanceof DefaultMutableTreeNode) {
                        var2x = ((DefaultMutableTreeNode)DefaultMutableTreeNode.class.cast(var2x)).getUserObject();
                     }

                     if (var2x == NetmapViewerHost.this.ArpNode) {
                        var8.setFont(this.BOLD);
                     }

                     if (var2x instanceof Node) {
                        Node var9 = (Node)var2x;
                        if (var9.getNetmapData() != null && var9.getNetmapData().getLevel() == 1L) {
                           var8.setFont(this.BOLD);
                        }

                        if (var9.isUpdated()) {
                           var8.setFont(this.ITALICS);
                        }
                     }

                     return var8;
                  }
               });
               NetmapViewerHost.this.itemList.addMouseListener(new MouseAdapter() {
                  public void mouseClicked(MouseEvent var1) {
                     this.maybeShowPopup(var1);
                  }

                  public void mousePressed(MouseEvent var1) {
                     this.maybeShowPopup(var1);
                  }

                  public void mouseReleased(MouseEvent var1) {
                     this.maybeShowPopup(var1);
                  }

                  private void maybeShowPopup(MouseEvent var1) {
                     if (var1.isPopupTrigger()) {
                        int var2x = NetmapViewerHost.this.itemList.getRowForLocation(var1.getX(), var1.getY());
                        if (var2x >= 0) {
                           TreePath var3 = NetmapViewerHost.this.itemList.getPathForRow(var2x);
                           if (var3 != null) {
                              Object var4 = var3.getLastPathComponent();
                              if (var4 instanceof DefaultMutableTreeNode) {
                                 var4 = ((DefaultMutableTreeNode)DefaultMutableTreeNode.class.cast(var4)).getUserObject();
                              }

                              if (var4 instanceof Node) {
                                 Map var5 = NetmapViewerHost.this.generateMap((Node)var4);
                                 JPopupMenu var6 = ContextMenuFactory.createContextMenuString("/NetmapViewer/ContextMenu.xml", NetmapViewerHost.this.core, NetmapViewerHost.this, NetmapViewerHost.this.target, Collections.singleton(var5), (Object)null, NetmapViewerHost.this);
                                 var6.show(var1.getComponent(), var1.getX(), var1.getY());
                              }

                           }
                        }
                     }
                  }
               });
            }
         });
         Closure var3 = new Closure() {
            public void execute(Object var1) {
               DataEvent var2x = (DataEvent)var1;
               TaskId var3 = var2x.getTaskId();
               if (var3 != null) {
                  Boolean var4 = var2x.getData().getBoolean("target::local");
                  String var5 = var2x.getData().getString("target::location");
                  if (var4 != null && var4 != Boolean.TRUE) {
                     if (var5 != null) {
                        NetmapViewerHost.this.handleTargetedTask(var5, var2.getTaskById(var3));
                     }

                  }
               }
            }
         };
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "groups", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "ldap", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "netbios", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "processes", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "scheduler", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "services", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "uptime", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "users", "Dsz", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "shares", "ZBng", var3));
         this.targetTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "route", "Dsz", new Closure() {
            Set<Task> handledTasks;

            {
               this.handledTasks = new TreeSet(Task.TaskComparator);
            }

            public void execute(Object var1) {
               DataEvent var2x = (DataEvent)var1;
               TaskId var3 = var2x.getTaskId();
               if (var3 != null) {
                  try {
                     if (var2x.getData().getObject("route") != null) {
                        Task var4 = var2.getTaskById(var3);
                        if (this.handledTasks.add(var4)) {
                           NetmapViewerHost.this.handleTargetedTask(NetmapViewerHost.this.unknown, var4);
                        }
                     }
                  } catch (Throwable var5) {
                     var5.printStackTrace();
                  }

               }
            }
         }));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "arp", "Dsz", new ArpClosure(this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "ifconfig", "Dsz", new IfConfigClosure(var2, this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "netconnections", "Dsz", new NetConnectionsClosure(this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "netmap", "Dsz", new NetmapClosure(this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "ping", "Dsz", new PingClosure(this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "traceroute", "Dsz", new TracerouteClosure(this)));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "shares", "ZBng", new SharesClosure(var2, this)));
      } else {
         throw new IllegalStateException("Unable to instansiate NetmapViewerHost because 'dataTranslator' is null");
      }
   }

   public CoreController getCore() {
      return this.core;
   }

   private void handleTargetedTask(final Node var1, final Task var2) {
      if (var1 != null && var2 != null) {
         this.dataTranslator.addClosure(ClosureFactory.newDisplayClosure(this.core, var2, new Closure() {
            public void execute(Object var1x) {
               EventQueue.invokeLater(new InsertPrintCommandNode(var1x.toString(), var2, var1, NetmapViewerHost.this));
            }
         }));
         this.dataTranslator.addTask(var2);
      }
   }

   private void handleTargetedTask(final String var1, final Task var2) {
      if (var1 != null && var2 != null) {
//         if (var1.startsWith("\\\\")) {
//            var1 = var1.substring(2);
//         }

         if (var2.getCommandName().equalsIgnoreCase("groups")) {
            this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, var2, new Closure() {
               public void execute(Object var1x) {
                  DataEvent var2 = (DataEvent)var1x;
                  Iterator var3 = var2.getData().getObjects("group").iterator();

                  while(var3.hasNext()) {
                     ObjectValue var4 = (ObjectValue)var3.next();
                     EventQueue.invokeLater(new InsertGroupsNode(new Group(var4, var1, var2.getTaskId()), NetmapViewerHost.this));
                  }

               }
            }));
         }

         if (var2.getCommandName().equalsIgnoreCase("services")) {
            this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, var2, new Closure() {
               public void execute(Object var1x) {
                  DataEvent var2 = (DataEvent)var1x;
                  Iterator var3 = var2.getData().getObjects("service").iterator();

                  while(var3.hasNext()) {
                     ObjectValue var4 = (ObjectValue)var3.next();
                     EventQueue.invokeLater(new InsertServicesNode(new Service(var4, var1, var2.getTaskId()), NetmapViewerHost.this));
                  }

               }
            }));
         }

         if (var2.getCommandName().equalsIgnoreCase("users")) {
            this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, var2, new Closure() {
               public void execute(Object var1x) {
                  DataEvent var2 = (DataEvent)var1x;
                  Iterator var3 = var2.getData().getObjects("user").iterator();

                  while(var3.hasNext()) {
                     ObjectValue var4 = (ObjectValue)var3.next();
                     EventQueue.invokeLater(new InsertUsersNode(new User(var4, var1, var2.getTaskId()), NetmapViewerHost.this));
                  }

               }
            }));
         }

         if (PRINTABLE_COMMANDS.contains(var2.getCommandName().toLowerCase())) {
            this.dataTranslator.addClosure(ClosureFactory.newDisplayClosure(this.core, var2, new Closure() {
               public void execute(Object var1x) {
                  EventQueue.invokeLater(new InsertPrintCommandNode(var1x.toString(), var2, var1, NetmapViewerHost.this));
               }
            }));
         }

         this.dataTranslator.addTask(var2);
      }
   }

   public static long stringToMillisecondDuration(String var0) {
      if (var0 == null) {
         return 0L;
      } else {
         try {
            DatatypeFactory var1 = DatatypeFactory.newInstance();
            Duration var2 = var1.newDurationDayTime(var0);
            if (var2 != null) {
               Calendar var3 = Calendar.getInstance();
               return var2.getTimeInMillis(var3);
            } else {
               return 0L;
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
            return 0L;
         }
      }
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      if (commandEvent != null && this.dataTranslator != null) {
         if (commandEvent.getId() != null) {
            if (!commandEvent.getId().equals(TaskId.UNINITIALIZED_ID)) {
               Task var2 = this.core.getTaskById(commandEvent.getId());
               if (var2 != null) {
                  if (var2.getCommandName() != null) {
                     if (TARGETED_COMMANDS.contains(var2.getCommandName().toLowerCase())) {
                        this.targetTranslator.addTask(var2);
                     }

                     if (INTERESTING_COMMANDS.contains(var2.getCommandName().toLowerCase())) {
                        this.dataTranslator.addTask(var2);
                     }

                  }
               }
            }
         }
      }
   }

   @Override
   public void action(List<String> var1) {
      this.action(var1, (Object)null);
   }

   @Override
   public void action(List<String> var1, Object var2) {
      if (var1.size() != 0) {
         var1.add(String.format("host=%s", this.target.getId()));
         if (this.core.internalCommand((InternalCommandCallback)null, var1)) {
         }

      }
   }

   @Override
   public void registerCommand(String var1, TaskId taskId) {
      System.out.println(var1);
   }

   public static void Traverse(TreeNode var0, Closure var1) {
      if (var0 != null) {
         var1.execute(var0);

         for(int var2 = 0; var2 < var0.getChildCount(); ++var2) {
            Traverse(var0.getChildAt(var2), var1);
         }
      }

   }

   public static DefaultMutableTreeNode DepthFirstSearch(TreeNode var0, Predicate var1) {
      if (var0 == null) {
         return null;
      } else {
         if (var0 instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)var0;
            if (var1.evaluate(var2.getUserObject())) {
               return var2;
            }
         }

         for(int var5 = 0; var5 < var0.getChildCount(); ++var5) {
            TreeNode var3 = var0.getChildAt(var5);
            DefaultMutableTreeNode var4 = DepthFirstSearch(var3, var1);
            if (var4 != null) {
               return var4;
            }
         }

         return null;
      }
   }

   public void NewNodeCleanup(TreeNode var1, Node var2) {
      Collection var3 = var2.getAddresses();

      label34:
      for(int var4 = 0; var4 < this.unknownNode.getChildCount(); ++var4) {
         DefaultMutableTreeNode var5 = (DefaultMutableTreeNode)this.unknownNode.getChildAt(var4);
         Node var6 = (Node)var5.getUserObject();
         if (var6 != this.ArpNode) {
            Iterator var7 = var3.iterator();

            while(true) {
               String var8;
               do {
                  if (!var7.hasNext()) {
                     continue label34;
                  }

                  var8 = (String)var7.next();
               } while(!var6.doesAddressMatch(var8) && !var6.doesNameMatch(var8));

               CopyNode(var6, var2);
               if (var5.getParent() != null) {
                  this.treeModel.removeNodeFromParent(var5);
               }
            }
         }
      }

   }

   public static void CopyNode(Node var0, Node var1) {
      var1.CopyNode(var0);
   }

   public Map<String, String> generateMap(Node var1) {
      HashMap var2 = new HashMap();
      Vector var3 = new Vector();
      var3.addAll(var1.getAddresses());
      Collections.sort(var3, new Comparator<String>() {
         public int compare(String var1, String var2) {
            if (var1 == var2) {
               return 0;
            } else if (var1 == null) {
               return -1;
            } else if (var2 == null) {
               return 1;
            } else if (var1.contains(".") && var2.contains(".") || var1.contains(":") && var2.contains(":")) {
               return var1.compareToIgnoreCase(var2);
            } else {
               return var1.contains(".") ? -1 : 1;
            }
         }
      });
      int var4 = 0;
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         Object[] var10002 = new Object[1];
         ++var4;
         var10002[0] = var4;
         var2.put(String.format("Address_%d", var10002), var6);
      }

      return var2;
   }

   public void expand(TreeNode var1) {
      try {
         TreePath var2 = new TreePath(this.treeModel.getPathToRoot(var1));
         this.itemList.expandPath(var2);
      } catch (Throwable var3) {
         this.core.logEvent(Level.SEVERE, var3.getMessage(), var3);
         var3.printStackTrace();
      }

   }

   public void insertNode(DefaultMutableTreeNode var1, DefaultMutableTreeNode var2) {
      boolean var3 = false;
      if (var2.getUserObject() instanceof Node) {
         Node var4 = (Node)var2.getUserObject();

         for(int var5 = 0; var5 < var1.getChildCount(); ++var5) {
            DefaultMutableTreeNode var6 = (DefaultMutableTreeNode)var1.getChildAt(var5);
            if (var6.getUserObject() instanceof Node) {
               Node var7 = (Node)var6.getUserObject();
               if (var4.getOrder().equals(var7.getOrder()) && var4.toString().compareTo(var7.toString()) < 0) {
                  this.treeModel.insertNodeInto(var2, var1, var5);
                  var3 = true;
                  break;
               }

               if (var4.getOrder().ordinal() < var7.getOrder().ordinal()) {
                  this.treeModel.insertNodeInto(var2, var1, var5);
                  var3 = true;
                  break;
               }
            }
         }
      }

      if (!var3) {
         this.treeModel.insertNodeInto(var2, var1, var1.getChildCount());
      }

      this.expand(var1);
      this.expand(var2);
   }

   public void removeNode(DefaultMutableTreeNode var1) {
      this.treeModel.removeNodeFromParent(var1);
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("arp");
      var0.add("ifconfig");
      var0.add("netconnections");
      var0.add("netmap");
      var0.add("ping");
      var0.add("traceroute");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(var0);
      TARGETED_COMMANDS = new HashSet();
      TARGETED_COMMANDS.add("groups");
      TARGETED_COMMANDS.add("ldap");
      TARGETED_COMMANDS.add("netbios");
      TARGETED_COMMANDS.add("processes");
      TARGETED_COMMANDS.add("route");
      TARGETED_COMMANDS.add("scheduler");
      TARGETED_COMMANDS.add("services");
      TARGETED_COMMANDS.add("uptime");
      TARGETED_COMMANDS.add("users");
      PRINTABLE_COMMANDS = new HashSet();
      PRINTABLE_COMMANDS.add("ldap");
      PRINTABLE_COMMANDS.add("netbios");
      PRINTABLE_COMMANDS.add("processes");
      PRINTABLE_COMMANDS.add("route");
      PRINTABLE_COMMANDS.add("scheduler");
      PRINTABLE_COMMANDS.add("uptime");
   }
}
