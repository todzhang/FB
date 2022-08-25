package ddb.dsz.plugin.verifier;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.imagemanager.ImageManager;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedFilterUpdater;
import ddb.util.checkedtablemodel.FilterWatcher;
import ddb.util.predicate.MustMatchOnePredicate;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AllPredicate;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/gkrellm2.png")
@DszName("Log Verifier")
@DszDescription("Verifies that the XML logs are in the proper format")
@DszUserStartable(true)
public class Verifier extends NoHostAbstractPlugin {
   VerifierModel2 model;
   OptionalHidingPredicate success;
   OptionalHidingPredicate failure;
   OptionalHidingPredicate noLog;
   final JCheckBox suspendVerification;
   MustMatchOnePredicate visibleGuidsPredicate;
   ColumnHidingModel hider;
   FilteredTableModel filteredModel;
   FilterWatcher guidWatcher;
   CheckedFilterUpdater<Operation> guidSelected;
   CheckableFilterList<Operation> visibleGuids;
   private JTable table;
   final JTextArea verifierOutput;
   int selectedRow;
   JButton reverify;

   public Verifier() {
      this.success = new OptionalHidingPredicate(VerifierState.VerifySuccess);
      this.failure = new OptionalHidingPredicate(VerifierState.VerifyFailure);
      this.noLog = new OptionalHidingPredicate(VerifierState.NoLog, false);
      this.suspendVerification = new JCheckBox("Suspend Verification");
      this.visibleGuidsPredicate = new MustMatchOnePredicate();
      this.hider = new ColumnHidingModel(VerifierColumn.class);
      this.guidWatcher = new FilterWatcher() {
         public void predicateChanged() {
            Verifier.this.filteredModel.filterChanged();
         }
      };
      this.guidSelected = new CheckedFilterUpdater<Operation>(this.visibleGuidsPredicate, this.guidWatcher) {
         public void selected(Operation var1, boolean var2) {
            super.selected(var1, var2);
            if (Verifier.this.visibleGuidsPredicate.getCount() == 1) {
               Verifier.this.hider.hide(VerifierColumn.OP);
            } else if (Verifier.this.hider.show(VerifierColumn.OP)) {
               Verifier.this.hider.moveColumnBefore(VerifierColumn.OP, VerifierColumn.ID);
            }

         }
      };
      this.visibleGuids = new CheckableFilterList("Visible Operations", this.guidSelected, new Comparator<Operation>() {
         public int compare(Operation var1, Operation var2) {
            if (var1 == var2) {
               return 0;
            } else {
               return var1 == null ? 1 : var1.compareTo(var2);
            }
         }
      });
      this.verifierOutput = new JTextArea();
      this.selectedRow = -1;
      this.reverify = new JButton("Reverify");
      super.setCareAboutLocalEvents(true);
      super.setCanClose(true);
      super.setUserClosable(true);
   }

   @Override
   protected boolean parseArgument2(String var1, String var2) {
      if (var1.equals("-maxDefault")) {
         try {
            this.model.setMaximumLength(Integer.parseInt(var2));
         } catch (NumberFormatException var4) {
            this.core.logEvent(Level.WARNING, String.format("Invalid format of parameter %s=%s", var1, var2), var4);
         }
      }

      return false;
   }

   @Override
   protected int init2() {
      this.core.logEvent(Level.FINE, "Initializing Log Verifier");
      this.model = new VerifierModel2(this, this.core.getTaskList(), this.core);
      this.filteredModel = new FilteredTableModel(this.model);
      this.filteredModel.addFilter(AllPredicate.getInstance(new Predicate[]{this.success, this.failure, this.noLog}), new Enum[]{VerifierColumn.VERIFY_STATUS});
      this.filteredModel.addFilter(this.visibleGuidsPredicate, new Enum[]{VerifierColumn.GUID});
      this.table = new JTable();
      this.table.setColumnModel(this.hider);
      this.table.setModel(this.filteredModel);
      this.hider.applyToTable(this.table);
      JScrollPane var1 = new JScrollPane(this.table);
      this.table.setDefaultRenderer(VerifierState.class, new StateRenderer());
      this.table.setDefaultRenderer(TaskState.class, new TaskStateRenderer(this.core));
      this.table.setSelectionMode(0);
      JPanel var2 = this.layoutFilterPanel();
      this.verifierOutput.setEditable(false);
      JScrollPane var3 = new JScrollPane(this.verifierOutput);
      final JSplitPane var4 = new JSplitPane(0, var1, var3);
      var4.setOneTouchExpandable(true);
      var4.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            var4.removeComponentListener(this);
            var4.setDividerLocation(0.6D);
            var4.setDividerLocation(1.0D);
         }
      });
      this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            if (!var1.getValueIsAdjusting()) {
               int var2 = Verifier.this.table.getSelectedRow();
               Verifier.this.selectedRow = Verifier.this.filteredModel.translateViewLocationToModelRow(var2, 0);
               if (Verifier.this.selectedRow != -1) {
                  Verifier.this.setOutput(Verifier.this.selectedRow);
                  if (Verifier.this.verifierOutput.getText().length() > 0) {
                     if ((double)var4.getDividerLocation() >= (double)var4.getMaximumDividerLocation() * 0.8D) {
                        if (var4.getLastDividerLocation() >= var4.getMaximumDividerLocation()) {
                           var4.setDividerLocation(0.8D);
                        } else {
                           var4.setDividerLocation(var4.getLastDividerLocation());
                        }
                     }
                  } else if (var4.getDividerLocation() <= var4.getMaximumDividerLocation()) {
                     var4.setLastDividerLocation(var4.getDividerLocation());
                     var4.setDividerLocation(1.0D);
                  }

               }
            }
         }
      });
      this.filteredModel.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent var1) {
            if (var1.getType() == 0 && var1.getFirstRow() <= Verifier.this.selectedRow && var1.getLastRow() >= Verifier.this.selectedRow) {
               Verifier.this.setOutput(Verifier.this.selectedRow);
            }

         }
      });
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            this.maybeDoPopup(var1);
         }

         public void mousePressed(MouseEvent var1) {
            this.maybeDoPopup(var1);
         }

         public void mouseReleased(MouseEvent var1) {
            this.maybeDoPopup(var1);
         }

         private void maybeDoPopup(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               final int var2 = Verifier.this.filteredModel.translateViewLocationToModelRow(var1.getY() / Verifier.this.table.getRowHeight(), 0);
               if (var2 >= 0 && var2 <= Verifier.this.model.getRowCount()) {
                  Object var3 = Verifier.this.model.getValueAt(var2, VerifierColumn.VERIFY_STATUS.ordinal());
                  if (var3 instanceof VerifierState) {
                     VerifierState var4 = (VerifierState)VerifierState.class.cast(var3);
                     if (!var4.canBeVerified()) {
                     }

                     JPopupMenu var5 = new JPopupMenu();
                     JMenuItem var6 = new JMenuItem("Reverify");
                     var5.add(var6);
                     var6.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent var1) {
                           Verifier.this.model.reverify(var2);
                        }
                     });
                     var5.show(Verifier.this.table, var1.getX(), var1.getY());
                  }
               }
            }
         }
      });
      JPanel var5 = new JPanel(new BorderLayout());
      var5.add(var2, "East");
      var5.add(var4, "Center");
      super.setDisplay(var5);
      this.setColumnWidth(VerifierColumn.TASK_STATUS, "", true, true);
      this.setColumnWidth(VerifierColumn.ID, "0000000", false, true);
      this.setColumnWidth(VerifierColumn.OP, "000", true, true);
      this.hider.hide(VerifierColumn.GUID);
      this.hider.hide(VerifierColumn.COMMAND);
      this.hider.hide(VerifierColumn.OP);
      this.hider.hide(VerifierColumn.OUTPUT);
      return 0;
   }

   public void setOutput(int var1) {
      String var2 = (String)this.model.getValueAt(var1, VerifierColumn.OUTPUT);
      String var3 = this.verifierOutput.getText();
      if (!var2.equals(var3)) {
         this.verifierOutput.setText(var2);
         this.verifierOutput.setCaretPosition(0);
      }
   }

   @Override
   public void fini2() {
      this.model.clear();
   }

   @Override
   protected void commandInfo(CommandEvent commandEvent) {
      Task var2 = this.core.getTaskById(commandEvent.getId());
      this.model.addTask(var2);
      String var3 = String.format("%d: %s", this.visibleGuids.getRowCount(), var2.getId().getOperation());
      boolean var4 = this.visibleGuids.getRowCount() == 0;
      if (this.visibleGuids.addElement(var3, var2.getId().getOperation(), var4) && var4) {
         this.visibleGuidsPredicate.add(var2.getId().getOperation());
         this.filteredModel.filterChanged();
      }

   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      Task var2 = this.core.getTaskById(var1.getId());
      this.model.addTask(var2);
   }

   public JPanel layoutFilterPanel() {
      JPanel var1 = new JPanel(new BorderLayout());
      JPanel var2 = new JPanel(new GridLayout(3, 1));
      JPanel var3 = new JPanel(new BorderLayout());
      var1.add(var2, "North");
      var1.add(var3, "Center");
      final JCheckBox var4 = new JCheckBox("Successes");
      final JCheckBox var5 = new JCheckBox("Failures");
      final JCheckBox var6 = new JCheckBox("Commands without logs");
      JPanel var7 = new JPanel(new GridLayout(2, 1));
      var7.add(this.suspendVerification);
      var7.add(this.reverify);
      var1.add(var7, "South");
      var2.add(var4);
      var2.add(var5);
      var2.add(var6);
      var2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Show:"));
      var4.setSelected(true);
      var5.setSelected(true);
      var6.setSelected(false);
      var4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            Verifier.this.success.setShow(var4.isSelected());
            Verifier.this.filteredModel.filterChanged();
         }
      });
      var5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            Verifier.this.failure.setShow(var5.isSelected());
            Verifier.this.filteredModel.filterChanged();
         }
      });
      var6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            Verifier.this.noLog.setShow(var6.isSelected());
            Verifier.this.filteredModel.filterChanged();
         }
      });
      this.reverify.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            SwingWorker var2 = new SwingWorker<Object, Void>() {
               protected Object doInBackground() throws Exception {
                  for(int var1 = 0; var1 < Verifier.this.model.getRowCount(); ++var1) {
                     Object var2 = Verifier.this.model.getValueAt(var1, VerifierColumn.VERIFY_STATUS.ordinal());
                     if (var2 instanceof VerifierState) {
                        VerifierState var3 = (VerifierState)VerifierState.class.cast(var2);
                        Verifier.this.model.reverify(var1);
                     }
                  }

                  return null;
               }
            };
            var2.execute();
         }
      });
      var3.add(this.visibleGuids);
      var1.setPreferredSize(new Dimension(375, 0));
      return var1;
   }

   private void setColumnWidth(Enum<?> var1, String var2, boolean var3, boolean var4) {
      TableColumn var5 = this.table.getColumnModel().getColumn(var1.ordinal());
      JLabel var6 = new JLabel(var2);
      if (var3) {
         var6.setIcon(ImageManager.getIcon(super.getLogo(), this.core.getLabelImageSize()));
      }

      var5.setPreferredWidth(var6.getPreferredSize().width + 10);
      if (var4) {
         var5.setMaxWidth(var6.getPreferredSize().width + 15);
         var5.setMinWidth(var6.getPreferredSize().width + 5);
      }

   }

   boolean isSuspended() {
      return this.suspendVerification.isSelected();
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var4 = var2.getMethod("main", var0.getClass());
      var4.invoke((Object)null, var0);
   }
}
