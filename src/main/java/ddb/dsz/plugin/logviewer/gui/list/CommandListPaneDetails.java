package ddb.dsz.plugin.logviewer.gui.list;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.dsz.plugin.logviewer.gui.renderer.CalendarRenderer;
import ddb.dsz.plugin.logviewer.gui.renderer.GuidRenderer;
import ddb.dsz.plugin.logviewer.gui.renderer.IdRenderer;
import ddb.dsz.plugin.logviewer.gui.renderer.StatusRenderer;
import ddb.dsz.plugin.logviewer.gui.target.TargetLogspace;
import ddb.dsz.plugin.logviewer.models.CommandModel;
import ddb.dsz.plugin.logviewer.models.CommandModelColumns;
import ddb.events.AutoScroll;
import ddb.imagemanager.ImageManager;
import ddb.util.Guid;
import ddb.util.TableSorter;
import ddb.util.FrequentlyAppendedTableModel.RecordState;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedFilterUpdater;
import ddb.util.checkedtablemodel.FilterWatcher;
import ddb.util.predicate.MustMatchOnePredicate;
import ddb.util.predicate.StringContainsPredicate;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.TransformedPredicate;
import org.jdesktop.layout.GroupLayout;

public class CommandListPaneDetails extends JPanel implements Observer {
   CommandModel model;
   TargetLogspace parent;
   CoreController core;
   CheckableFilterList<String> visibleCommands;
   CheckableFilterList<Operation> visibleGuids;
   CheckedFilterUpdater<Operation> guidSelected;
   CheckedFilterUpdater<String> commandSelected;
   ColumnHidingModel hider;
   FilteredTableModel filteredModel;
   TableSorter tableSorter;
   MustMatchOnePredicate visibleCommandsPredicate;
   MustMatchOnePredicate visibleGuidsPredicate;
   StringContainsPredicate commandContains;
   Predicate invalidOnly;
   boolean invalid = false;
   ActionListener filterChanged = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         CommandListPaneDetails.this.filteredModel.filterChanged();
      }
   };
   FilterWatcher filterWatcher = new FilterWatcher() {
      public void predicateChanged() {
         CommandListPaneDetails.this.filteredModel.filterChanged();
      }
   };
   private GuidRenderer guidRenderer = new GuidRenderer();
   AutoScroll auto;
   boolean showInvalidOnly = false;
   final Object TYPED_COMMAND_LOCK = new Object();
   boolean resized = false;
   boolean shown = false;
   private JCheckBox bAutoScroll;
   private JCheckBox bHoldNew;
   private JCheckBox bShowInvalid;
   private JCheckBox bShowInvalidOnly;
   private JCheckBox bShowLocal;
   private JTable commandList;
   private JPanel commandPanel;
   private JScrollPane commandScroll;
   private JTextField filterField;
   private JPanel filters;
   private JPanel guidPanel;
   private JSplitPane jSplitPane1;
   private JPanel keywordFilter;
   private JToggleButton showFiltersButton;
   private JPanel stateFilter;
   private JTextField typedCommand;

   public CommandListPaneDetails(CommandModel model, TargetLogspace parent, CoreController cc, boolean isLocal, boolean invalid) {
      this.model = model;
      this.parent = parent;
      this.core = cc;
      this.invalid = invalid;
      this.filteredModel = new FilteredTableModel(model);
      this.hider = new ColumnHidingModel(CommandModelColumns.class);
      this.tableSorter = new TableSorter(this.filteredModel);
      this.initComponents();
      this.bShowLocal.setVisible(false);
      this.bShowInvalidOnly.setVisible(false);
      this.bShowInvalid.setVisible(false);
      this.bShowInvalid.setSelected(false);
      this.tableSorter.addMouseListenerToHeaderInTable(this.commandList);
      this.hider.applyToTable(this.commandList);
      if (invalid) {
         this.invalidOnly = new Predicate() {
            public boolean evaluate(Object arg0) {
               return !((Task)Task.class.cast(arg0)).hasTaskingInformation();
            }
         };
      }

      this.commandContains = new StringContainsPredicate(false);
      this.commandPanel.setLayout(new BorderLayout());
      this.visibleCommandsPredicate = new MustMatchOnePredicate();
      this.commandSelected = new CheckedFilterUpdater(this.visibleCommandsPredicate, this.filterWatcher);
      this.visibleCommands = new CheckableFilterList("Visible Commands", this.commandSelected, String.CASE_INSENSITIVE_ORDER);
      this.commandPanel.add(this.visibleCommands, "Center");
      this.guidPanel.setLayout(new BorderLayout());
      this.visibleGuidsPredicate = new MustMatchOnePredicate();
      this.guidSelected = new CheckedFilterUpdater<Operation>(this.visibleGuidsPredicate, this.filterWatcher) {
         public void selected(Operation value, boolean selected) {
            super.selected(value, selected);
            if (CommandListPaneDetails.this.visibleGuidsPredicate.getCount() == 1) {
               CommandListPaneDetails.this.hider.hide(CommandModelColumns.OP);
            } else if (CommandListPaneDetails.this.hider.show(CommandModelColumns.OP)) {
               CommandListPaneDetails.this.hider.moveColumnBefore(CommandModelColumns.OP, CommandModelColumns.ID);
            }

         }
      };
      this.visibleGuids = new CheckableFilterList("Visible Operations", this.guidSelected, new Comparator<Operation>() {
         public int compare(Operation o1, Operation o2) {
            if (o1 == o2) {
               return 0;
            } else {
               return o1 == null ? 1 : o1.compareTo(o2);
            }
         }
      });
      this.guidPanel.add(this.visibleGuids, "Center");
      if (!invalid) {
         this.filteredModel.addFilter(this.commandContains, new Enum[]{CommandModelColumns.COMMAND, CommandModelColumns.FULLCOMMAND});
         this.filteredModel.addFilter(this.visibleCommandsPredicate, new Enum[]{CommandModelColumns.COMMAND});
         this.filteredModel.addFilter(new TransformedPredicate(new Transformer() {
            public Object transform(Object arg0) {
               return CommandListPaneDetails.this.visibleGuids.getItem((Integer)Integer.class.cast(arg0));
            }
         }, this.visibleGuidsPredicate), new Enum[]{CommandModelColumns.OP});
      }

      this.commandList.setDefaultRenderer(Integer.class, new IdRenderer());
      this.commandList.setDefaultRenderer(Calendar.class, new CalendarRenderer());
      this.commandList.setDefaultRenderer(TaskState.class, new StatusRenderer(this.core));
      this.commandList.setDefaultRenderer(Guid.class, this.guidRenderer);
      this.setColumnWidth(CommandModelColumns.ID, "99999", false, true);
      this.setColumnWidth(CommandModelColumns.COMMENT, "No Log File", false, true);
      this.setColumnWidth(CommandModelColumns.STATUS, "", true, true);
      this.setColumnWidth(CommandModelColumns.CREATED, "00:00:00 PM", false, true);
      this.setColumnWidth(CommandModelColumns.OP, "000", true, true);
      this.setColumnWidth(CommandModelColumns.COMMAND, "yadayada", false, false);
      this.setColumnWidth(CommandModelColumns.TARGET, "00000001", false, true);
      this.hider.hide(CommandModelColumns.COMMAND);
      this.hider.hide(CommandModelColumns.TARGET);
      this.hider.hide(CommandModelColumns.GUID);
      this.hider.hide(CommandModelColumns.DISPLAY);
      this.hider.hide(CommandModelColumns.STORAGE);
      this.hider.hide(CommandModelColumns.VALID);
      this.hider.hide(CommandModelColumns.TASK);
      if (this.core.isLiveOperation()) {
         this.hider.hide(CommandModelColumns.OP);
      }

      if (!invalid) {
         this.hider.hide(CommandModelColumns.COMMENT);
      } else {
         this.hider.hide(CommandModelColumns.OP);
         this.hider.hide(CommandModelColumns.STATUS);
      }

      this.commandList.getSelectionModel().setSelectionMode(0);
      this.commandList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               synchronized(CommandListPaneDetails.this.TYPED_COMMAND_LOCK) {
                  Object obj = CommandListPaneDetails.this.tableSorter.getValueAt(CommandListPaneDetails.this.commandList.getSelectionModel().getLeadSelectionIndex(), CommandModelColumns.FULLCOMMAND.ordinal());
                  if (obj == null) {
                     obj = "";
                  }

                  CommandListPaneDetails.this.typedCommand.setText(obj.toString());
               }
            }
         }
      });
      this.auto = new AutoScroll(this.commandScroll);
      this.auto.setScroll(false);
      this.bAutoScroll.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CommandListPaneDetails.this.auto.setScroll(CommandListPaneDetails.this.bAutoScroll.isSelected());
         }
      });
      this.filters.setVisible(false);
      if (invalid) {
         this.removeAll();
         this.setLayout(new BorderLayout());
         this.add(new JScrollPane(this.commandList));
      }

   }

   public void update(Observable o, Object arg) {
      if (arg instanceof TaskDataAccess) {
         TaskDataAccess tda = (TaskDataAccess)TaskDataAccess.class.cast(arg);
         if (DataType.STATE.equals(tda.getType())) {
            this.model.addRecord(tda.getTask());
         }
      }
   }

   private void setColumnWidth(Enum<?> col, String string, boolean icon, boolean binding) {
      TableColumn column = this.commandList.getColumnModel().getColumn(col.ordinal());
      JLabel label = new JLabel("000" + string);
      if (icon) {
         label.setIcon(ImageManager.getIcon("images/player_play.png", this.core.getLabelImageSize()));
      }

      column.setPreferredWidth(label.getPreferredSize().width + 10);
      if (binding) {
         column.setMaxWidth(label.getPreferredSize().width + 35);
         column.setMinWidth(label.getPreferredSize().width + 5);
      }

   }

   public void parseTask(Task task) {
      if (task != null) {
         if (this.invalidOnly != null && !this.invalidOnly.evaluate(task)) {
            this.model.deleteRecord(task);
         } else {
            this.model.addRecord(task);
            this.addCommandName(task.getCommandName(), true);
            this.addOperation(task.getId().getOperation(), true);
         }
      }
   }

   public void addCommandName(final String name, final Boolean show) {
      if (EventQueue.isDispatchThread()) {
         if (name == null) {
            return;
         }

         if (this.visibleCommands.addElement(name, (String)null, name, show)) {
            if (show) {
               this.visibleCommandsPredicate.add(name);
            }

            this.filteredModel.filterChanged();
         }
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               CommandListPaneDetails.this.addCommandName(name, show);
            }
         });
      }

   }

   public void setOperation(final Operation operation) {
      if (EventQueue.isDispatchThread()) {
         this.guidRenderer.setGuid(operation.getGuid());
         this.addOperation(operation, true);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               CommandListPaneDetails.this.setOperation(operation);
            }
         });
      }

   }

   public void addOperation(Operation value, boolean show) {
      if (!this.invalid) {
         String tooltip = String.format("%d: %s", this.visibleGuids.getRowCount(), value.getGuid().toString());
         Calendar cal = value.getStartTime();
         String caption = tooltip;
         if (cal != null) {
            caption = String.format("%d: %04d-%02d-%02d %02d:%02d:%02d", this.visibleGuids.getRowCount(), cal.get(1), cal.get(2) + 1, cal.get(5), cal.get(11), cal.get(12), cal.get(13));
         }

         if (this.visibleGuids.addElement(caption, tooltip, value, show) && show) {
            this.visibleGuidsPredicate.add(value);
            this.filteredModel.filterChanged();
         }

      }
   }

   protected void loadSelected() {
      Collection<Task> tasks = new HashSet();
      this.model.readLock();

      try {
         for(int i = this.commandList.getSelectionModel().getMinSelectionIndex(); i <= this.commandList.getSelectionModel().getMaxSelectionIndex(); ++i) {
            int col = this.hider.translateViewToModel(CommandModelColumns.TASK);
            int row = this.tableSorter.convertViewRowToModel(i);
            if (col != -1) {
               tasks.add((Task)this.filteredModel.getValueAt(row, col));
            }
         }
      } finally {
         this.model.readUnlock();
      }

      Iterator i$ = tasks.iterator();

      while(i$.hasNext()) {
         Task task = (Task)i$.next();
         this.parent.loadTask(task, task.getDisplayTransform(), task.getStorageTransform());
      }

   }

   public JComponent getDefaultElement() {
      return this.commandList;
   }

   void doFilter() {
      this.commandContains.setString(this.filterField.getText());
      this.filteredModel.filterChanged();
   }

   public void showInvalidOnly(boolean show) {
      if (show) {
         this.bShowInvalidOnly.setSelected(true);
         this.bShowInvalid.setSelected(true);
      } else {
         this.bShowInvalidOnly.setSelected(false);
      }

      this.bShowInvalid.setSelected(show);
   }

   private void initComponents() {
      this.commandScroll = new JScrollPane();
      this.commandList = new JTable();
      this.commandList.setColumnModel(this.hider);
      this.typedCommand = new JTextField();
      this.filters = new JPanel();
      this.stateFilter = new JPanel();
      this.bShowLocal = new JCheckBox();
      this.bShowInvalid = new JCheckBox();
      this.bHoldNew = new JCheckBox();
      this.bAutoScroll = new JCheckBox();
      this.bShowInvalidOnly = new JCheckBox();
      this.keywordFilter = new JPanel();
      this.filterField = new JTextField();
      this.jSplitPane1 = new JSplitPane();
      this.commandPanel = new JPanel();
      this.guidPanel = new JPanel();
      this.showFiltersButton = new JToggleButton();
      this.commandList.setModel(this.tableSorter);
      this.commandList.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            CommandListPaneDetails.this.commandListMouseClicked(evt);
         }
      });
      this.commandList.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent evt) {
            CommandListPaneDetails.this.commandListKeyTyped(evt);
         }
      });
      this.commandScroll.setViewportView(this.commandList);
      this.typedCommand.setEditable(false);
      this.stateFilter.setBorder(BorderFactory.createEtchedBorder());
      this.bShowLocal.setSelected(true);
      this.bShowLocal.setText("Show Local");
      this.bShowLocal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bShowLocal.setMargin(new Insets(0, 0, 0, 0));
      this.bShowInvalid.setSelected(true);
      this.bShowInvalid.setText("Show Invalid");
      this.bShowInvalid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bShowInvalid.setMargin(new Insets(0, 0, 0, 0));
      this.bHoldNew.setText("Hold New Tasks");
      this.bHoldNew.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bHoldNew.setMargin(new Insets(0, 0, 0, 0));
      this.bHoldNew.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CommandListPaneDetails.this.bHoldNewActionPerformed(evt);
         }
      });
      this.bAutoScroll.setText("Autoscroll On New Task");
      this.bAutoScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bAutoScroll.setMargin(new Insets(0, 0, 0, 0));
      this.bShowInvalidOnly.setText("Show Invalid Only");
      this.bShowInvalidOnly.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      GroupLayout stateFilterLayout = new GroupLayout(this.stateFilter);
      this.stateFilter.setLayout(stateFilterLayout);
      stateFilterLayout.setHorizontalGroup(stateFilterLayout.createParallelGroup(1).add(stateFilterLayout.createSequentialGroup().addContainerGap().add(stateFilterLayout.createParallelGroup(1).add(this.bAutoScroll).add(this.bHoldNew)).addPreferredGap(0).add(stateFilterLayout.createParallelGroup(1).add(stateFilterLayout.createSequentialGroup().add(this.bShowInvalidOnly).addPreferredGap(0).add(this.bShowLocal)).add(this.bShowInvalid)).addContainerGap(-1, 32767)));
      stateFilterLayout.setVerticalGroup(stateFilterLayout.createParallelGroup(1).add(stateFilterLayout.createSequentialGroup().addContainerGap().add(stateFilterLayout.createParallelGroup(2).add(stateFilterLayout.createSequentialGroup().add(this.bHoldNew).addPreferredGap(0).add(this.bAutoScroll)).add(stateFilterLayout.createSequentialGroup().add(this.bShowInvalid).addPreferredGap(0).add(stateFilterLayout.createParallelGroup(3).add(this.bShowInvalidOnly).add(this.bShowLocal)))).addContainerGap(-1, 32767)));
      this.keywordFilter.setBorder(BorderFactory.createTitledBorder("Command Filter"));
      this.filterField.setColumns(10);
      this.filterField.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent evt) {
            CommandListPaneDetails.this.filterFieldKeyReleased(evt);
         }
      });
      GroupLayout keywordFilterLayout = new GroupLayout(this.keywordFilter);
      this.keywordFilter.setLayout(keywordFilterLayout);
      keywordFilterLayout.setHorizontalGroup(keywordFilterLayout.createParallelGroup(1).add(keywordFilterLayout.createSequentialGroup().addContainerGap().add(this.filterField, -1, 311, 32767).addContainerGap()));
      keywordFilterLayout.setVerticalGroup(keywordFilterLayout.createParallelGroup(1).add(keywordFilterLayout.createSequentialGroup().add(this.filterField, -2, 20, -2).addContainerGap()));
      this.jSplitPane1.setDividerLocation(2000);
      this.jSplitPane1.setOrientation(0);
      this.jSplitPane1.setResizeWeight(1.0D);
      this.jSplitPane1.setOneTouchExpandable(true);
      this.commandPanel.setBorder(BorderFactory.createEtchedBorder());
      this.commandPanel.setMinimumSize(new Dimension(0, 100));
      GroupLayout commandPanelLayout = new GroupLayout(this.commandPanel);
      this.commandPanel.setLayout(commandPanelLayout);
      commandPanelLayout.setHorizontalGroup(commandPanelLayout.createParallelGroup(1).add(0, 365, 32767));
      commandPanelLayout.setVerticalGroup(commandPanelLayout.createParallelGroup(1).add(0, 276, 32767));
      this.jSplitPane1.setTopComponent(this.commandPanel);
      this.guidPanel.setBorder(BorderFactory.createEtchedBorder());
      GroupLayout guidPanelLayout = new GroupLayout(this.guidPanel);
      this.guidPanel.setLayout(guidPanelLayout);
      guidPanelLayout.setHorizontalGroup(guidPanelLayout.createParallelGroup(1).add(0, 365, 32767));
      guidPanelLayout.setVerticalGroup(guidPanelLayout.createParallelGroup(1).add(0, 0, 32767));
      this.jSplitPane1.setRightComponent(this.guidPanel);
      GroupLayout filtersLayout = new GroupLayout(this.filters);
      this.filters.setLayout(filtersLayout);
      filtersLayout.setHorizontalGroup(filtersLayout.createParallelGroup(1).add(this.keywordFilter, -1, -1, 32767).add(this.stateFilter, -1, -1, 32767).add(this.jSplitPane1, -1, 343, 32767));
      filtersLayout.setVerticalGroup(filtersLayout.createParallelGroup(1).add(filtersLayout.createSequentialGroup().add(this.keywordFilter, -2, -1, -2).addPreferredGap(0).add(this.stateFilter, -2, -1, -2).addPreferredGap(0).add(this.jSplitPane1, -1, 287, 32767)));
      this.showFiltersButton.setText("Filters");
      this.showFiltersButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CommandListPaneDetails.this.showFiltersButtonActionPerformed(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(2, layout.createSequentialGroup().add(this.commandScroll, -1, 297, 32767).addPreferredGap(0).add(this.filters, -2, -1, -2)).add(2, layout.createSequentialGroup().add(this.typedCommand, -1, 561, 32767).addPreferredGap(0).add(this.showFiltersButton)));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(2, layout.createSequentialGroup().add(layout.createParallelGroup(1).add(this.filters, -1, -1, 32767).add(2, this.commandScroll, -1, 421, 32767)).add(8, 8, 8).add(layout.createParallelGroup(3).add(this.typedCommand, -2, -1, -2).add(this.showFiltersButton))));
   }

   private void bHoldNewActionPerformed(ActionEvent evt) {
      this.model.setRecordState(this.bHoldNew.isSelected() ? RecordState.HOLD : RecordState.SHOW);
   }

   private void commandListKeyTyped(KeyEvent evt) {
      if (evt.getKeyCode() == 10) {
         this.loadSelected();
      }

   }

   private void commandListMouseClicked(MouseEvent evt) {
      if (evt.getClickCount() == 2) {
         this.loadSelected();
      }

   }

   private void filterFieldKeyReleased(KeyEvent evt) {
      this.doFilter();
   }

   private void showFiltersButtonActionPerformed(ActionEvent evt) {
      this.filters.setVisible(this.showFiltersButton.isSelected());
   }
}
