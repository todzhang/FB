package ddb.dsz.plugin.taskmanager;

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
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.dsz.plugin.taskmanager.details.ProcessDetails;
import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import ddb.dsz.plugin.taskmanager.enumerated.HandlesStatus;
import ddb.dsz.plugin.taskmanager.enumerated.ProcessInfoStatus;
import ddb.dsz.plugin.taskmanager.models.ProcessTableColumns;
import ddb.dsz.plugin.taskmanager.models.ProcessTableModel;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessDatabase;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.dsz.plugin.taskmanager.processinformation.generator.Generator;
import ddb.dsz.plugin.taskmanager.processinformation.generator.WindowsGenerator;
import ddb.dsz.plugin.taskmanager.renderers.FileStatusRenderer;
import ddb.dsz.plugin.taskmanager.renderers.HandleRenderer;
import ddb.dsz.plugin.taskmanager.renderers.HasProcessInformationRenderer;
import ddb.dsz.plugin.taskmanager.renderers.ProcessShadingRenderer;
import ddb.imagemanager.ImageManager;
import ddb.util.TableSorter;
import ddb.util.UtilityConstants;
import ddb.util.predicate.CheckBoxMatchPredicate;
import ddb.util.predicate.CheckBoxPredicate;
import ddb.util.predicate.MustMatchOnePredicate;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public class TargetProcessSpace extends SingleTargetImpl implements Observer {
   private static final Collection<String> INTERESTING_COMMANDS;
   public static final String HOST_NAME = "-host";
   public static final String INITIAL = "initial";
   public static final String STARTED = "started";
   public static final String STOPPED = "stopped";
   public static final String TASKMANAGER_ICON = "images/taskmanager.png";
   public static final String MENUENTRIES = "/TaskManager/ProcessContextMenu.xml";
   static final ScheduledExecutorService analyzer;
   static final ProcessAnalyzer procAnalyze;
   static final DataTransformer dataTranslator;
   private boolean stop = false;
   final MultipleTargetPlugin parent;
   JTable jtable;
   ProcessTableModel tableModel;
   ProcessDetails details;
   TableSorter sorter = new TableSorter();
   FilteredTableModel filteredModel;
   ColumnHidingModel columnHider = new ColumnHidingModel(ProcessTableColumns.class);
   private final ProcessDatabase database;
   private CheckBoxMatchPredicate highlightedPredicate = new CheckBoxMatchPredicate(false);
   private CheckBoxPredicate showSafePredicate = new CheckBoxPredicate(true);
   private CheckBoxPredicate showUnknownPredicate = new CheckBoxPredicate(true);
   private CheckBoxPredicate showSecurityPredicate = new CheckBoxPredicate(true);
   private CheckBoxPredicate showMaliciousPredicate = new CheckBoxPredicate(true);
   private CheckBoxPredicate showCorePredicate = new CheckBoxPredicate(true);
   Predicate typePredicate;
   JComponent localDisplay;
   HostInfo host;

   public TargetProcessSpace(HostInfo target, CoreController cc, MultipleTargetPlugin parent) {
      super(target, cc);
      this.typePredicate = PredicateUtils.anyPredicate(new Predicate[]{PredicateUtils.andPredicate(this.showSafePredicate, new MustMatchOnePredicate(new Object[]{FileStatus.SAFE})), PredicateUtils.andPredicate(this.showSecurityPredicate, new MustMatchOnePredicate(new Object[]{FileStatus.SECURITY_PRODUCT})), PredicateUtils.andPredicate(this.showMaliciousPredicate, new MustMatchOnePredicate(new Object[]{FileStatus.MALICIOUS_SOFTWARE})), PredicateUtils.andPredicate(this.showCorePredicate, new MustMatchOnePredicate(new Object[]{FileStatus.CORE_OS})), PredicateUtils.andPredicate(this.showUnknownPredicate, new MustMatchOnePredicate(new Object[]{FileStatus.NONE}))});
      this.localDisplay = null;
      this.host = null;
      this.database = ProcessDatabase.GetInstance(cc);
      this.host = target;
      super.setName(target.getId());
      this.target = target;
      this.parent = parent;
      this.details = new ProcessDetails(cc);
      procAnalyze.setResourceDirectory(cc.getResourceDirectory());
      this.core.logEvent(Level.FINE, "Initializing TaskMonitor");
      JPanel mainFrame = new JPanel();
      JPanel filterAtTop = new JPanel();
      filterAtTop.add(this.makeCheckBox("New Only", false, new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            TargetProcessSpace.this.filteredModel.filterChanged();
         }
      }, this.highlightedPredicate.getModel()));
      ActionListener typeFilterHasChanged = new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            TargetProcessSpace.this.filteredModel.filterChanged();
         }
      };
      JSeparator sep = new JSeparator(1);
      sep.setPreferredSize(new Dimension(10, 1));
      filterAtTop.add(sep);
      filterAtTop.add(this.makeCheckBox("Safe", true, typeFilterHasChanged, this.showSafePredicate.getModel()));
      filterAtTop.add(this.makeCheckBox("Security Products", true, typeFilterHasChanged, this.showSecurityPredicate.getModel()));
      filterAtTop.add(this.makeCheckBox("Core OS", true, typeFilterHasChanged, this.showCorePredicate.getModel()));
      filterAtTop.add(this.makeCheckBox("Malicious", true, typeFilterHasChanged, this.showMaliciousPredicate.getModel()));
      filterAtTop.add(this.makeCheckBox("Unknown", true, typeFilterHasChanged, this.showUnknownPredicate.getModel()));
      final JSplitPane splitter = new JSplitPane(0);
      splitter.setOneTouchExpandable(true);
      splitter.setResizeWeight(0.9D);
      splitter.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            splitter.setDividerLocation(splitter.getMaximumDividerLocation() * 2);
            splitter.setLastDividerLocation(splitter.getMaximumDividerLocation() * 5 / 10);
            splitter.removeComponentListener(this);
         }
      });
      mainFrame.setLayout(new BorderLayout());
      this.tableModel = new ProcessTableModel(cc);
      this.filteredModel = new FilteredTableModel(this.tableModel);
      this.filteredModel.addFilter(this.highlightedPredicate, new Enum[]{ProcessTableColumns.HIGHLIGHT});
      this.filteredModel.addFilter(this.typePredicate, new Enum[]{ProcessTableColumns.TYPE});
      this.sorter.setModel(this.filteredModel);
      this.jtable = new JTable() {
         public String getToolTipText(MouseEvent event) {
            int row = event.getY() / TargetProcessSpace.this.jtable.getRowHeight();
            Object obj = TargetProcessSpace.this.jtable.getModel().getValueAt(row, ProcessTableColumns.EXPLANATION.ordinal());
            return obj == null ? "" : obj.toString();
         }
      };
      this.jtable.setColumnModel(this.columnHider);
      this.jtable.setModel(this.sorter);
      this.sorter.addMouseListenerToHeaderInTable(this.jtable);
      this.columnHider.applyToTable(this.jtable);
      this.jtable.setDefaultRenderer(String.class, new ProcessShadingRenderer(this.core, this.sorter, TaskManager2.optionPane));
      this.jtable.setDefaultRenderer(Long.class, new ProcessShadingRenderer(this.core, this.sorter, TaskManager2.optionPane));
      this.jtable.setDefaultRenderer(FileStatus.class, new FileStatusRenderer(this.core, this.sorter, TaskManager2.optionPane));
      this.jtable.setDefaultRenderer(HandlesStatus.class, new HandleRenderer(this.core, this.sorter, TaskManager2.optionPane));
      this.jtable.setDefaultRenderer(ProcessInfoStatus.class, new HasProcessInformationRenderer(this.core, this.sorter, TaskManager2.optionPane));
      this.jtable.getSelectionModel().setSelectionMode(0);
      this.jtable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            int row = TargetProcessSpace.this.jtable.getSelectedRow();
            if (row != -1) {
               row = TargetProcessSpace.this.sorter.convertViewRowToModel(row);
               row = TargetProcessSpace.this.filteredModel.translateViewLocationToModelRow(row, 0);
               if (row != -1) {
                  ProcessInformation p = TargetProcessSpace.this.tableModel.getProcessAtRow(row);
                  if (p != null) {
                     if (TargetProcessSpace.this.details.setProcessInfo(p) && (p.hasProcessInfo() || p.hasHandleInfo())) {
                        if (splitter.getDividerLocation() >= splitter.getMaximumDividerLocation()) {
                           splitter.setDividerLocation(splitter.getLastDividerLocation());
                        }
                     } else if (splitter.getDividerLocation() < splitter.getMaximumDividerLocation()) {
                        splitter.setLastDividerLocation(splitter.getDividerLocation());
                        splitter.setDividerLocation(splitter.getMaximumDividerLocation() * 2);
                     }

                  }
               }
            }
         }
      });
      mainFrame.add(new JScrollPane(this.jtable), "Center");
      mainFrame.add(filterAtTop, "North");
      this.tableModel.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent e) {
            switch(e.getType()) {
            case -1:
            case 1:
               TargetProcessSpace.this.fireContentsChanged();
            default:
            }
         }
      });
      this.jtable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            this.popUp(e);
         }

         public void mousePressed(MouseEvent e) {
            this.popUp(e);
         }

         public void mouseReleased(MouseEvent e) {
            this.popUp(e);
         }

         private void popUp(MouseEvent e) {
            if (e.isPopupTrigger()) {
               int row = e.getY() / TargetProcessSpace.this.jtable.getRowHeight();
               TargetProcessSpace.this.jtable.addRowSelectionInterval(row, row);
               row = TargetProcessSpace.this.sorter.convertViewRowToModel(row);
               row = TargetProcessSpace.this.filteredModel.translateViewLocationToModelRow(row, 0);
               final ProcessInformation p = TargetProcessSpace.this.tableModel.getProcessAtRow(row);
               Map<String, String> replacements = new Hashtable();
               replacements.put("id", p.getId().toString());
               replacements.put("parent", p.getParent().toString());
               replacements.put("user", p.getUserName());
               replacements.put("name", p.getProcName());
               JPopupMenu popup = ContextMenuFactory.createContextMenuString("/TaskManager/ProcessContextMenu.xml", TargetProcessSpace.this.core, new CommandCallbackListener() {
                  @Override
                  public void registerCommand(String string, TaskId taskId) {
                  }
               }, TargetProcessSpace.this.host, Collections.singleton(replacements), (Object)null, new ContextMenuAction() {
                  @Override
                  public void action(List<String> parameters) {
                     if (parameters != null && parameters.size() != 0) {
                        if (((String)parameters.get(0)).equalsIgnoreCase("ProcessDetail")) {
                           (new ProcessDetailsPopup(TargetProcessSpace.this.core, p)).setVisible(true);
                        } else if (((String)parameters.get(0)).equalsIgnoreCase("GetComment")) {
                           String comment = JOptionPane.showInputDialog("Please enter a comment for this process:", "(None)");
                           if (comment != null) {
                              List<String> dup = new Vector(parameters.subList(1, parameters.size()));
                              dup.add(String.format("comment=%s", comment));
                              TargetProcessSpace.this.core.internalCommand((InternalCommandCallback)null, dup);
                           }
                        } else {
                           parameters.add(String.format("host=%s", TargetProcessSpace.this.target.getId()));
                           TargetProcessSpace.this.core.internalCommand((InternalCommandCallback)null, parameters);
                        }

                     }
                  }

                  @Override
                  public void action(List<String> parameters, Object extraContext) {
                     this.action(parameters);
                  }
               });
               if (popup.getSubElements().length > 0) {
                  popup.show(TargetProcessSpace.this.jtable, e.getX(), e.getY());
               }
            }

         }
      });
      this.localDisplay = splitter;
      super.setDisplay(this.localDisplay);
      splitter.setTopComponent(mainFrame);
      splitter.setBottomComponent(this.details);
      this.setColumnWidth(ProcessTableColumns.TYPE, "", true, true);
      this.setColumnWidth(ProcessTableColumns.PROCESSINFO, "", true, true);
      this.setColumnWidth(ProcessTableColumns.HANDLEINFO, "", true, true);
      this.setColumnWidth(ProcessTableColumns.PROCESSID, "12345678", false, true);
      this.setColumnWidth(ProcessTableColumns.PARENTID, "12345678", false, true);
      this.setColumnWidth(ProcessTableColumns.CPUTIME, "10 days 00:00:00", false, true);
      this.setColumnWidth(ProcessTableColumns.DISPLAY, "1234", false, true);
      this.setColumnWidth(ProcessTableColumns.ARCH_TYPE, "64-Bit", true, true);
      ProcessTableColumns[] arr$ = ProcessTableColumns.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ProcessTableColumns c = arr$[i$];
         if (c.isHidden()) {
            this.columnHider.hide(c);
         }
      }

      if (dataTranslator != null) {
         final Predicate examineData = this.getDataPredicate();
         dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, "processinfo", "Dsz", new Closure() {
            public void execute(Object arg0) {
               if (examineData.evaluate(arg0)) {
                  DataEvent de = (DataEvent)arg0;
                  Iterator i$ = de.getData().getObjects("processinfo").iterator();

                  while(i$.hasNext()) {
                     ObjectValue procInfo = (ObjectValue)i$.next();
                     ProcessInformation proc = TargetProcessSpace.this.tableModel.getProcessById(procInfo.getInteger("id"));
                     if (proc != null) {
                        proc.setProcessInformation(TargetProcessSpace.this.core.getTaskById(de.getTaskId()), procInfo);
                        TargetProcessSpace.procAnalyze.enqueProcess(proc);
                        TargetProcessSpace.this.tableModel.updateProcess(proc);
                     }
                  }

               }
            }
         }));
         dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, "processes", "Dsz", new Closure() {
            Set<TaskId> ignoreTask = new HashSet();

            public void execute(Object arg0) {
               if (examineData.evaluate(arg0)) {
                  DataEvent dtde = (DataEvent)DataEvent.class.cast(arg0);
                  if (!this.ignoreTask.contains(dtde.getTaskId())) {
                     ObjectValue data = dtde.getData();
                     Boolean local = data.getBoolean("target::local");
                     if (local != null && !local) {
                        this.ignoreTask.add(dtde.getTaskId());
                     } else {
                        List<ProcessInformation> newFullList = new Vector();
                        TargetProcessSpace.DszProcessGroup[] arr$ = TargetProcessSpace.DszProcessGroup.values();
                        int len$ = arr$.length;

                        ProcessInformation existing;
                        for(int i$xx = 0; i$xx < len$; ++i$xx) {
                           TargetProcessSpace.DszProcessGroup group = arr$[i$xx];
                           Iterator i$xxx = data.getObjects(group.data + "::processitem").iterator();

                           while(i$xxx.hasNext()) {
                              ObjectValue processItem = (ObjectValue)i$xxx.next();
                              existing = new ProcessInformation(processItem, TargetProcessSpace.this.core, TargetProcessSpace.this.host);
                              if ("initial".equalsIgnoreCase(group.status)) {
                                 newFullList.add(existing);
                              } else if ("started".equalsIgnoreCase(group.status)) {
                                 existing.SetHighlight();
                                 TargetProcessSpace.this.tableModel.addProcess(existing);
                                 TargetProcessSpace.procAnalyze.enqueProcess(existing);
                              } else if ("stopped".equalsIgnoreCase(group.status)) {
                                 TargetProcessSpace.this.tableModel.removeProcess(existing);
                              }
                           }
                        }

                        if (newFullList.size() > 0) {
                           List<ProcessInformation> existingProcesses = TargetProcessSpace.this.tableModel.getAllProcesses();
                           boolean highlight = existingProcesses.size() > 0;
                           Iterator i$ = newFullList.iterator();

                           ProcessInformation pi;
                           while(i$.hasNext()) {
                              pi = (ProcessInformation)i$.next();
                              boolean found = false;
                              Iterator i$x = existingProcesses.iterator();

                              while(i$x.hasNext()) {
                                 existing = (ProcessInformation)i$x.next();
                                 if (existing.getId().equals(pi.getId())) {
                                    existing.setAs(pi);
                                    existingProcesses.remove(existing);
                                    found = true;
                                    break;
                                 }
                              }

                              if (!found) {
                                 if (highlight) {
                                    pi.SetHighlight();
                                 }

                                 TargetProcessSpace.procAnalyze.enqueProcess(pi);
                                 TargetProcessSpace.this.tableModel.addProcess(pi);
                              }
                           }

                           i$ = existingProcesses.iterator();

                           while(i$.hasNext()) {
                              pi = (ProcessInformation)i$.next();
                              TargetProcessSpace.this.tableModel.removeProcess(pi);
                           }
                        }

                     }
                  }
               }
            }
         }));
         dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, "handles", "Dsz", new Closure() {
            public void execute(Object arg0) {
               if (examineData.evaluate(arg0)) {
                  DataEvent de = (DataEvent)arg0;
                  Iterator i$x = de.getData().getObjects("process").iterator();

                  while(true) {
                     ObjectValue procInfo;
                     ProcessInformation proc;
                     do {
                        if (!i$x.hasNext()) {
                           return;
                        }

                        procInfo = (ObjectValue)i$x.next();
                        proc = TargetProcessSpace.this.tableModel.getProcessById(procInfo.getInteger("id"));
                     } while(proc == null);

                     Generator g = new WindowsGenerator(TargetProcessSpace.this.core);
                     Iterator i$ = procInfo.getObjects("handle").iterator();

                     while(i$.hasNext()) {
                        ObjectValue handle = (ObjectValue)i$.next();
                        proc.addHandle(g.newHandle(handle));
                     }

                     TargetProcessSpace.this.tableModel.updateProcess(proc);
                  }
               }
            }
         }));
      }

      analyzer.schedule(new Runnable() {
         public void run() {
            List<ProcessInformation> existingProcesses = TargetProcessSpace.this.tableModel.getAllProcesses();
            if (existingProcesses != null) {
               Iterator i$ = existingProcesses.iterator();

               while(i$.hasNext()) {
                  ProcessInformation pi = (ProcessInformation)i$.next();
                  TargetProcessSpace.procAnalyze.enqueProcess(pi);
               }
            }

            if (!TargetProcessSpace.this.stop) {
               TargetProcessSpace.analyzer.schedule(this, 15L, TimeUnit.SECONDS);
            }

         }
      }, 15L, TimeUnit.SECONDS);
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      Task t = this.core.getTaskById(commandEvent.getId());
      if (t != null) {
         if (t.getCommandName() != null) {
            if (INTERESTING_COMMANDS.contains(t.getCommandName().toLowerCase()) && dataTranslator != null) {
               dataTranslator.addTask(t);
            }

         }
      }
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   JCheckBox makeCheckBox(String name, boolean checked, ActionListener listener, ToggleButtonModel model) {
      JCheckBox check = new JCheckBox();
      check.setText(name);
      check.setSelected(checked);
      check.addActionListener(listener);
      check.setModel(model);
      return check;
   }

   public void update(Observable o, Object arg) {
      if (o == TaskManager2.optionPane.getObservable()) {
         this.jtable.repaint();
      }

   }

   private void setColumnWidth(Enum<?> col, String string, boolean icon, boolean binding) {
      TableColumn column = this.jtable.getColumnModel().getColumn(col.ordinal());
      JLabel label = new JLabel(string);
      if (icon) {
         label.setIcon(ImageManager.getIcon("images/taskmanager.png", this.core.getLabelImageSize()));
      }

      column.setPreferredWidth(label.getPreferredSize().width + 10);
      if (binding) {
         column.setMaxWidth(label.getPreferredSize().width + 15);
         column.setMinWidth(label.getPreferredSize().width + 5);
      }

   }

   public void evaluateAll() {
      Iterator i$ = this.tableModel.getAllProcesses().iterator();

      while(i$.hasNext()) {
         ProcessInformation pi = (ProcessInformation)i$.next();
         procAnalyze.enqueProcess(pi);
      }

   }

   public String toString() {
      return String.format("Processes: %s", this.target.getId());
   }

   public void fini2() {
      this.stop = true;
   }

   static {
      Set<String> set = new HashSet();
      set.add("processes");
      set.add("processinfo");
      set.add("handles");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(set);
      dataTranslator = DataTransformer.newInstance();
      analyzer = Executors.newSingleThreadScheduledExecutor(UtilityConstants.createThreadFactory("TaskProcessSpace"));
      procAnalyze = new ProcessAnalyzer(analyzer);
   }

   public static enum DszProcessGroup {
      INIT("initialprocesslistitem", "initial"),
      START("startprocesslistitem", "started"),
      STOP("stopprocesslistitem", "stopped");

      String data;
      String status;

      private DszProcessGroup(String dataName, String status) {
         this.data = dataName;
         this.status = status;
      }
   }
}
