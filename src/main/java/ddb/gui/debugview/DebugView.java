package ddb.gui.debugview;

import ddb.predicate.PredicateClosure;
import ddb.predicate.PredicateClosureImpl;
import ddb.util.FrequentlyAppendedTableModel.RecordState;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedTableSelection;
import ddb.util.predicate.StringContainsPredicate;
import ddb.util.predicate.UnionListPredicate;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import ddb.writequeue.AbstractWritable;
import ddb.writequeue.Writable;
import ddb.writequeue.WriteQueue;
import ddb.writequeue.text.TextClear;
import ddb.writequeue.text.TextWrite;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.functors.SwitchClosure;
import org.jdesktop.layout.GroupLayout;

public class DebugView extends JPanel {
   protected MutableMessageRecord lastRecord = null;
   MessageRecordDisplayModel recordModel = new MessageRecordDisplayModel();
   Importance minimumLevel;
   CheckableFilterList<String> sectionFilterTable;
   final UnionListPredicate<String> sectionPredicate;
   final UnionListPredicate<Integer> threadPredicate;
   final ImportancePredicate importancePredicate;
   final StringContainsPredicate commentContainsPredicate;
   ColumnHidingModel hideColumns;
   FilteredTableModel filterModel;
   PredicateClosure[] closures;
   WriteQueue<AbstractWritable> writeQueue;
   private JRadioButton DiscardNewMessages;
   private JPanel FancyView;
   private JRadioButton HoldNewMessages;
   private JRadioButton KeepAllMessages;
   private ButtonGroup PossibleMessageStates;
   private JRadioButton ShowNewMessages;
   private JPanel SimpleView;
   private JButton applyThreads;
   private JButton clearButton;
   private JButton clearSimpleView;
   private JTextArea details;
   private JScrollPane detailsScroller;
   private JRadioButton discardNewRaw;
   private JRadioButton discardNewRaw1;
   private JPanel filterPanel;
   private JRadioButton holdNewRaw;
   private JSplitPane mainSplit;
   private JTextField message;
   private JLabel messageLabel;
   private JTabbedPane outputDisplayMethod;
   private ButtonGroup rawMessageStates;
   private JTextArea rawOutput;
   private JPanel recordPanel;
   private JScrollPane recordScroll;
   private JTable recordTable;
   private JComboBox scopeChooser;
   private JPanel sectionPanel;
   private JRadioButton showNewRaw;
   private JScrollPane simpleScroll;
   private JLabel threadLabel;
   private JTextField threads;
   private JPanel topPanel;

   void setColumnWidth() {
      for(int column = 0; column < this.recordTable.getColumnModel().getColumnCount(); ++column) {
         TableColumn col = this.recordTable.getColumnModel().getColumn(column);
         OutputMessageColumns[] arr$ = OutputMessageColumns.values();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            OutputMessageColumns imp = arr$[i$];
            if (col.getHeaderValue().equals(imp.getColumnName()) && imp.getDefaultValue() != null) {
               this.setColumnWidth(col, imp.getDefaultValue(), false, imp.isBinding());
            }
         }
      }

   }

   public void setMinimumLevel(Importance minLevel) {
      this.minimumLevel = minLevel;
   }

   public void setCurrentLevel(Importance minLevel) {
      this.scopeChooser.setSelectedItem(minLevel);
   }

   public void setMaximum(int maximum) {
      this.recordModel.setMaximum(maximum);
   }

   public void append(final String text) {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               DebugView.this.append(text);
            }
         });
      } else {
         MutableMessageRecord mr = MessageRecordImpl.ParseMessage(text);
         if (mr != null) {
            this.lastRecord = mr;
            if (this.sectionFilterTable.addElement(mr.getSection(), true)) {
               this.sectionPredicate.addItem(mr.getSection());
            }

            if (mr.getPriority().ordinal() >= this.minimumLevel.ordinal()) {
               this.recordModel.addRecord(mr);
            }
         } else if (this.lastRecord != null) {
            this.lastRecord.append(text);
         }

         this.writeQueue.enqueue(new DebugView.TextWriterImpl(text));
      }
   }

   public void addMessageRecord(MessageRecord newRecord) {
      if (newRecord.getPriority().ordinal() >= this.minimumLevel.ordinal()) {
         if (newRecord != null) {
            if (this.sectionFilterTable.addElement(newRecord.getSection(), true)) {
               this.sectionPredicate.addItem(newRecord.getSection());
            }

            this.recordModel.addRecord(newRecord);
         }

      }
   }

   private void setColumnWidth(TableColumn column, String string, boolean icon, boolean binding) {
      JLabel label = new JLabel(string);
      int size = label.getPreferredSize().width;
      column.setPreferredWidth(size + 10);
      column.setWidth(size + 10);
      if (binding) {
         column.setMaxWidth(size + 15);
         column.setMinWidth(size + 5);
      }

   }

   void rescan() {
      synchronized(this.threadPredicate) {
         String list = this.threads.getText();
         String[] individuals = list.split(" ");
         this.threadPredicate.clear();

         for(int i = 0; i < individuals.length; ++i) {
            try {
               this.threadPredicate.addItem(Integer.valueOf(individuals[i]));
            } catch (Exception var7) {
            }
         }

         this.filterModel.filterChanged();
      }
   }

   void doFilter() {
      this.commentContainsPredicate.setString(this.message.getText());
      this.filterModel.filterChanged();
   }

   public DebugView() {
      this.minimumLevel = Importance.NOT_SET;
      this.sectionPredicate = new UnionListPredicate(false);
      this.threadPredicate = new UnionListPredicate(true);
      this.importancePredicate = new ImportancePredicate();
      this.commentContainsPredicate = new StringContainsPredicate();
      this.hideColumns = new ColumnHidingModel(OutputMessageColumns.class);
      this.filterModel = new FilteredTableModel(this.recordModel);
      this.closures = new PredicateClosure[]{new PredicateClosureImpl(PredicateUtils.instanceofPredicate(DebugView.TextWriterImpl.class), new Closure() {
         public void execute(Object arg0) {
            DebugView.TextWriterImpl twi = (DebugView.TextWriterImpl)arg0;
            if (DebugView.this.showNewRaw.isSelected()) {
               DebugView.this.rawOutput.append(twi.getText());
               int lines = DebugView.this.rawOutput.getLineCount();
               int max = 25000;
               if (lines > max) {
                  int line = lines - max;
                  line += max * 3 / 10;

                  try {
                     DebugView.this.rawOutput.replaceRange("", 0, DebugView.this.rawOutput.getLineStartOffset(line));
                  } catch (BadLocationException var7) {
                  }
               }
            } else if (!DebugView.this.discardNewRaw.isSelected()) {
               DebugView.this.writeQueue.reque(twi);
            }

         }
      }), new PredicateClosureImpl(PredicateUtils.instanceofPredicate(TextClear.class), new Closure() {
         public void execute(Object arg0) {
            DebugView.this.rawOutput.setText("");
         }
      })};
      this.writeQueue = new WriteQueue(SwitchClosure.getInstance(this.closures, this.closures, ClosureUtils.nopClosure()), true);
      this.sectionFilterTable = new CheckableFilterList("Sections", new DebugView.CheckedTableCallback(), String.CASE_INSENSITIVE_ORDER);
      this.filterModel.addFilter(this.importancePredicate, new Enum[]{OutputMessageColumns.PRIORITY});
      this.filterModel.addFilter(this.sectionPredicate, new Enum[]{OutputMessageColumns.SECTION});
      this.filterModel.addFilter(this.threadPredicate, new Enum[]{OutputMessageColumns.THREAD});
      this.filterModel.addFilter(this.commentContainsPredicate, new Enum[]{OutputMessageColumns.MESSAGE});
      this.initComponents();
   }

   private void initComponents() {
      this.PossibleMessageStates = new ButtonGroup();
      this.rawMessageStates = new ButtonGroup();
      this.outputDisplayMethod = new JTabbedPane();
      this.FancyView = new JPanel();
      this.mainSplit = new JSplitPane();
      this.detailsScroller = new JScrollPane();
      this.details = new JTextArea();
      this.topPanel = new JPanel();
      this.filterPanel = new JPanel();
      this.messageLabel = new JLabel();
      this.threadLabel = new JLabel();
      this.message = new JTextField();
      this.threads = new JTextField();
      this.applyThreads = new JButton();
      this.sectionPanel = new JPanel();
      this.DiscardNewMessages = new JRadioButton();
      this.HoldNewMessages = new JRadioButton();
      this.ShowNewMessages = new JRadioButton();
      this.KeepAllMessages = new JRadioButton();
      this.recordPanel = new JPanel();
      this.recordScroll = new JScrollPane();
      this.recordTable = new JTable();
      this.clearButton = new JButton();
      this.scopeChooser = new JComboBox();
      this.SimpleView = new JPanel();
      this.simpleScroll = new JScrollPane();
      this.rawOutput = new JTextArea();
      this.clearSimpleView = new JButton();
      this.showNewRaw = new JRadioButton();
      this.holdNewRaw = new JRadioButton();
      this.discardNewRaw = new JRadioButton();
      this.discardNewRaw1 = new JRadioButton();
      this.mainSplit.setDividerLocation(275);
      this.mainSplit.setOrientation(0);
      this.mainSplit.setResizeWeight(0.8D);
      this.details.setColumns(20);
      this.details.setRows(5);
      this.detailsScroller.setViewportView(this.details);
      this.mainSplit.setRightComponent(this.detailsScroller);
      this.messageLabel.setText("Message:");
      this.threadLabel.setText("Threads:");
      this.message.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent evt) {
            DebugView.this.messageKeyReleased(evt);
         }
      });
      this.applyThreads.setText("Apply");
      this.applyThreads.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.applyThreadsActionPerformed(evt);
         }
      });
      GroupLayout sectionPanelLayout = new GroupLayout(this.sectionPanel);
      this.sectionPanel.setLayout(sectionPanelLayout);
      sectionPanelLayout.setHorizontalGroup(sectionPanelLayout.createParallelGroup(1).add(0, 238, 32767));
      sectionPanelLayout.setVerticalGroup(sectionPanelLayout.createParallelGroup(1).add(0, 113, 32767));
      this.PossibleMessageStates.add(this.DiscardNewMessages);
      this.DiscardNewMessages.setText("Discard New Messages");
      this.DiscardNewMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.DiscardNewMessages.setMargin(new Insets(0, 0, 0, 0));
      this.DiscardNewMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterMessages(evt);
         }
      });
      this.PossibleMessageStates.add(this.HoldNewMessages);
      this.HoldNewMessages.setText("Hold New Messages");
      this.HoldNewMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.HoldNewMessages.setMargin(new Insets(0, 0, 0, 0));
      this.HoldNewMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterMessages(evt);
         }
      });
      this.PossibleMessageStates.add(this.ShowNewMessages);
      this.ShowNewMessages.setSelected(true);
      this.ShowNewMessages.setText("Show New Messages");
      this.ShowNewMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.ShowNewMessages.setMargin(new Insets(0, 0, 0, 0));
      this.ShowNewMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterMessages(evt);
         }
      });
      this.PossibleMessageStates.add(this.KeepAllMessages);
      this.KeepAllMessages.setText("Keep All Messages");
      this.KeepAllMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.KeepAllMessages.setMargin(new Insets(0, 0, 0, 0));
      this.KeepAllMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterMessages(evt);
         }
      });
      GroupLayout filterPanelLayout = new GroupLayout(this.filterPanel);
      this.filterPanel.setLayout(filterPanelLayout);
      filterPanelLayout.setHorizontalGroup(filterPanelLayout.createParallelGroup(1).add(2, filterPanelLayout.createSequentialGroup().add(filterPanelLayout.createParallelGroup(2).add(this.sectionPanel, -1, -1, 32767).add(1, filterPanelLayout.createSequentialGroup().addContainerGap().add(filterPanelLayout.createParallelGroup(1).add(this.ShowNewMessages, -1, 228, 32767).add(this.HoldNewMessages, -1, 228, 32767).add(this.DiscardNewMessages, -1, 228, 32767).add(this.KeepAllMessages, -1, 228, 32767))).add(1, filterPanelLayout.createSequentialGroup().addContainerGap().add(filterPanelLayout.createParallelGroup(2).add(filterPanelLayout.createSequentialGroup().add(this.threadLabel).add(7, 7, 7)).add(this.messageLabel)).addPreferredGap(0).add(filterPanelLayout.createParallelGroup(1).add(2, filterPanelLayout.createSequentialGroup().add(this.threads, -1, 107, 32767).addPreferredGap(0).add(this.applyThreads)).add(this.message, -1, 174, 32767)))).addContainerGap()));
      filterPanelLayout.setVerticalGroup(filterPanelLayout.createParallelGroup(1).add(2, filterPanelLayout.createSequentialGroup().add(this.sectionPanel, -1, -1, 32767).addPreferredGap(0).add(filterPanelLayout.createParallelGroup(2).add(filterPanelLayout.createSequentialGroup().add(this.threadLabel).add(9, 9, 9)).add(filterPanelLayout.createSequentialGroup().add(filterPanelLayout.createParallelGroup(3).add(this.applyThreads).add(this.threads, -2, -1, -2)).addPreferredGap(0))).add(filterPanelLayout.createParallelGroup(3).add(this.messageLabel).add(this.message, -2, -1, -2)).addPreferredGap(0).add(this.ShowNewMessages).addPreferredGap(0).add(this.HoldNewMessages).addPreferredGap(0).add(this.DiscardNewMessages).addPreferredGap(0).add(this.KeepAllMessages).add(11, 11, 11)));
      this.sectionPanel.setLayout(new BorderLayout());
      this.sectionPanel.add(this.sectionFilterTable, "Center");
      this.recordPanel.setBorder(BorderFactory.createTitledBorder("Output Messages"));
      this.recordTable.setModel(new DefaultTableModel(new Object[][]{new Object[0], new Object[0], new Object[0], new Object[0]}, new String[0]));
      this.recordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            try {
               DebugView.this.details.setText(DebugView.this.recordModel.getValueAt(DebugView.this.filterModel.translateViewLocationToModelRow(DebugView.this.recordTable.getSelectedRow(), 0), OutputMessageColumns.MESSAGE).toString());
            } catch (Exception var3) {
               DebugView.this.details.setText("");
            }

         }
      });
      this.recordScroll.setViewportView(this.recordTable);
      this.recordTable.setColumnModel(this.hideColumns);
      this.recordTable.setModel(this.filterModel);
      this.hideColumns.applyToTable(this.recordTable);
      this.recordTable.setDefaultRenderer(Calendar.class, new CalendarRenderer());
      this.hideColumns.hide(OutputMessageColumns.TIME);
      this.setColumnWidth();
      GroupLayout recordPanelLayout = new GroupLayout(this.recordPanel);
      this.recordPanel.setLayout(recordPanelLayout);
      recordPanelLayout.setHorizontalGroup(recordPanelLayout.createParallelGroup(1).add(this.recordScroll, -1, 368, 32767));
      recordPanelLayout.setVerticalGroup(recordPanelLayout.createParallelGroup(1).add(this.recordScroll, -1, 248, 32767));
      GroupLayout topPanelLayout = new GroupLayout(this.topPanel);
      this.topPanel.setLayout(topPanelLayout);
      topPanelLayout.setHorizontalGroup(topPanelLayout.createParallelGroup(1).add(2, topPanelLayout.createSequentialGroup().add(this.recordPanel, -1, -1, 32767).addPreferredGap(0).add(this.filterPanel, -2, -1, -2)));
      topPanelLayout.setVerticalGroup(topPanelLayout.createParallelGroup(1).add(2, topPanelLayout.createSequentialGroup().addContainerGap().add(this.filterPanel, -1, -1, 32767)).add(2, this.recordPanel, -1, -1, 32767));
      this.mainSplit.setLeftComponent(this.topPanel);
      this.clearButton.setText("Clear");
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.clearButtonActionPerformed(evt);
         }
      });
      Importance[] arr$ = Importance.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Importance imp = arr$[i$];
         this.scopeChooser.addItem(imp);
      }

      this.scopeChooser.setSelectedItem(this.importancePredicate.getImportance());
      this.scopeChooser.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            DebugView.this.scopeChooserItemStateChanged(evt);
         }
      });
      GroupLayout FancyViewLayout = new GroupLayout(this.FancyView);
      this.FancyView.setLayout(FancyViewLayout);
      FancyViewLayout.setHorizontalGroup(FancyViewLayout.createParallelGroup(1).add(FancyViewLayout.createSequentialGroup().addContainerGap().add(FancyViewLayout.createParallelGroup(1).add(2, FancyViewLayout.createSequentialGroup().add(this.scopeChooser, 0, 571, 32767).addPreferredGap(0).add(this.clearButton)).add(2, this.mainSplit, -1, 636, 32767)).addContainerGap()));
      FancyViewLayout.setVerticalGroup(FancyViewLayout.createParallelGroup(1).add(FancyViewLayout.createSequentialGroup().addContainerGap().add(FancyViewLayout.createParallelGroup(3).add(this.clearButton).add(this.scopeChooser, -2, -1, -2)).addPreferredGap(0).add(this.mainSplit, -1, 499, 32767).addContainerGap()));
      this.outputDisplayMethod.addTab("Formatted Output", this.FancyView);
      this.rawOutput.setColumns(20);
      this.rawOutput.setEditable(false);
      this.rawOutput.setRows(5);
      this.simpleScroll.setViewportView(this.rawOutput);
      this.clearSimpleView.setText("Clear");
      this.clearSimpleView.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.clearSimpleViewActionPerformed(evt);
         }
      });
      this.rawMessageStates.add(this.showNewRaw);
      this.showNewRaw.setSelected(true);
      this.showNewRaw.setText("Show New Messages");
      this.showNewRaw.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterRawMessages(evt);
         }
      });
      this.rawMessageStates.add(this.holdNewRaw);
      this.holdNewRaw.setText("Hold New Messages");
      this.holdNewRaw.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterRawMessages(evt);
         }
      });
      this.rawMessageStates.add(this.discardNewRaw);
      this.discardNewRaw.setText("Discard New Messages");
      this.discardNewRaw.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterRawMessages(evt);
         }
      });
      this.rawMessageStates.add(this.discardNewRaw1);
      this.discardNewRaw1.setText("Keep All Messages");
      this.discardNewRaw1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DebugView.this.filterRawMessages(evt);
         }
      });
      GroupLayout SimpleViewLayout = new GroupLayout(this.SimpleView);
      this.SimpleView.setLayout(SimpleViewLayout);
      SimpleViewLayout.setHorizontalGroup(SimpleViewLayout.createParallelGroup(1).add(SimpleViewLayout.createSequentialGroup().addContainerGap().add(SimpleViewLayout.createParallelGroup(1).add(this.simpleScroll, -1, 636, 32767).add(SimpleViewLayout.createSequentialGroup().add(this.showNewRaw).addPreferredGap(0).add(this.holdNewRaw).addPreferredGap(0).add(this.discardNewRaw).addPreferredGap(0).add(this.discardNewRaw1).addPreferredGap(0, 83, 32767).add(this.clearSimpleView))).addContainerGap()));
      SimpleViewLayout.setVerticalGroup(SimpleViewLayout.createParallelGroup(1).add(2, SimpleViewLayout.createSequentialGroup().addContainerGap().add(SimpleViewLayout.createParallelGroup(1).add(SimpleViewLayout.createParallelGroup(3).add(this.showNewRaw).add(this.holdNewRaw).add(this.discardNewRaw).add(this.clearSimpleView)).add(this.discardNewRaw1)).addPreferredGap(0).add(this.simpleScroll, -1, 499, 32767).addContainerGap()));
      this.outputDisplayMethod.addTab("Raw Output", this.SimpleView);
      this.outputDisplayMethod.setSelectedIndex(1);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(this.outputDisplayMethod, -1, 661, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(2, this.outputDisplayMethod, -1, 575, 32767));
   }

   private void filterMessages(ActionEvent evt) {
      if (this.ShowNewMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.SHOW);
      }

      if (this.KeepAllMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.KEEP);
      }

      if (this.DiscardNewMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.DISCARD);
      }

      if (this.HoldNewMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.HOLD);
      }

   }

   private void scopeChooserItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         Object o = evt.getItem();
         if (o instanceof Importance) {
            Importance imp = (Importance)Importance.class.cast(o);
            if (imp != null) {
               this.importancePredicate.setImportance(imp);
               this.filterModel.filterChanged();
            }

            if (this.minimumLevel.ordinal() > imp.ordinal()) {
               this.minimumLevel = imp;
            }
         }

      }
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      this.recordModel.clear();
   }

   private void applyThreadsActionPerformed(ActionEvent evt) {
      this.rescan();
   }

   private void messageKeyReleased(KeyEvent evt) {
      this.doFilter();
   }

   private void clearSimpleViewActionPerformed(ActionEvent evt) {
      this.writeQueue.enqueue(new TextClear());
   }

   private void filterRawMessages(ActionEvent evt) {
   }

   public void clear() {
      this.recordModel.clear();
   }

   class TextWriterImpl extends TextWrite {
      public TextWriterImpl(String text) {
         super(text);
      }

      public boolean combine(Writable second) {
         if (second instanceof DebugView.TextWriterImpl) {
            DebugView.TextWriterImpl twi = (DebugView.TextWriterImpl)second;
            this.sb.append(twi.getText());
            return true;
         } else {
            return false;
         }
      }
   }

   public static enum MessageState {
      SHOW,
      HOLD,
      DISCARD;
   }

   private final class CheckedTableCallback implements CheckedTableSelection<String> {
      private CheckedTableCallback() {
      }

      public void selected(String caption, boolean selected) {
         if (selected) {
            DebugView.this.sectionPredicate.addItem(caption.toString());
         } else {
            DebugView.this.sectionPredicate.removeItem(caption.toString());
         }

         DebugView.this.filterModel.filterChanged();
      }

      // $FF: synthetic method
      CheckedTableCallback(Object x1) {
         this();
      }
   }
}
