package ddb.gui.javalogviewer;

import ddb.imagemanager.ImageManager;
import ddb.util.FrequentlyAppendedTableModel.RecordState;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedTableSelection;
import ddb.util.predicate.StringContainsPredicate;
import ddb.util.predicate.UnionListPredicate;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.layout.GroupLayout;

public class JavaLogViewer extends JPanel {
   LogRecordTableModel recordModel = new LogRecordTableModel();
   CheckableFilterList<String> sectionFilterTable;
   UnionListPredicate<String> sectionPredicate = new UnionListPredicate(false);
   UnionListPredicate<Integer> threadPredicate = new UnionListPredicate(true);
   LevelPredicate levelPredicate = new LevelPredicate();
   StringContainsPredicate commentContainsPredicate = new StringContainsPredicate();
   ColumnHidingModel hideColumns = new ColumnHidingModel(LogRecordTableColumns.class);
   FilteredTableModel filterModel;
   private JPanel bottomPanel;
   private ButtonGroup buttonGroup1;
   private JLabel classField;
   private JLabel classLabel;
   private JButton clearButton;
   private JTextArea detailsDisplay;
   private JScrollPane detailsScroller;
   private JRadioButton discardMessages;
   private JLabel filterLabel;
   private JPanel filterPanel;
   private JPanel headerPanel;
   private JRadioButton holdMessages;
   private JLabel jLabel1;
   private JLabel jLabel5;
   private JRadioButton keepAllMessages;
   private JPanel leftPanel;
   private JLabel levelField;
   private JLabel levelLabel;
   private JComboBox levelSelector;
   private JSplitPane mainDisplay;
   private JTextField messageFilter;
   private JLabel methodField;
   private JLabel methodLabel;
   private JTable recordTable;
   private JPanel rightPanel;
   private JPanel sectionPanel;
   private JRadioButton showMessages;
   private JScrollPane tablePanel;
   private JLabel timeField;
   private JLabel timeLabel;
   private JPanel topPanel;

   void setColumnWidth() {
      for(int var1 = 0; var1 < this.recordTable.getColumnModel().getColumnCount(); ++var1) {
         TableColumn var2 = this.recordTable.getColumnModel().getColumn(var1);
         LogRecordTableColumns[] var3 = LogRecordTableColumns.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LogRecordTableColumns var6 = var3[var5];
            if (var2.getHeaderValue().equals(var6.getName()) && var6.getDefaultValue() != null) {
               this.setColumnWidth(var2, var6.getDefaultValue(), false, var6.isBinding());
            }
         }
      }

   }

   private void setColumnWidth(TableColumn var1, String var2, boolean var3, boolean var4) {
      JLabel var5 = new JLabel(var2);
      int var6 = var5.getPreferredSize().width;
      var1.setPreferredWidth(var6 + 10);
      var1.setWidth(var6 + 10);
      if (var4) {
         var1.setMaxWidth(var6 + 15);
         var1.setMinWidth(var6 + 5);
      }

   }

   public void setMaximum(int var1) {
      this.recordModel.setMaximum(var1);
   }

   public void append(final LogRecord var1) {
      if (var1.getLoggerName() == null || var1.getLoggerName().trim().length() == 0) {
         var1.setLoggerName("Unknown");
      }

      EventQueue.invokeLater(new Runnable() {
         public void run() {
            if (JavaLogViewer.this.sectionFilterTable.addElement(var1.getLoggerName(), true)) {
               JavaLogViewer.this.sectionPredicate.addItem(var1.getLoggerName());
            }

         }
      });
      this.recordModel.addRecord(var1);
   }

   void doFilter() {
      this.commentContainsPredicate.setString(this.messageFilter.getText());
      this.filterModel.filterChanged();
   }

   public JavaLogViewer() {
      this.filterModel = new FilteredTableModel(this.recordModel);
      this.sectionFilterTable = new CheckableFilterList("Sections", new JavaLogViewer.CheckedTableCallback(), String.CASE_INSENSITIVE_ORDER);
      this.filterModel.setModel(this.recordModel);
      this.filterModel.addFilter(this.levelPredicate, new Enum[]{LogRecordTableColumns.LEVEL});
      this.filterModel.addFilter(this.sectionPredicate, new Enum[]{LogRecordTableColumns.LOGGER});
      this.filterModel.addFilter(this.commentContainsPredicate, new Enum[]{LogRecordTableColumns.MESSAGE});
      this.initComponents();
      this.levelSelector.setSelectedItem(this.levelPredicate.level);
   }

   private void initComponents() {
      this.jLabel5 = new JLabel();
      this.jLabel1 = new JLabel();
      this.buttonGroup1 = new ButtonGroup();
      this.levelSelector = new JComboBox();
      this.clearButton = new JButton();
      this.mainDisplay = new JSplitPane();
      this.topPanel = new JPanel();
      this.filterPanel = new JPanel();
      this.sectionPanel = new JPanel();
      this.discardMessages = new JRadioButton();
      this.holdMessages = new JRadioButton();
      this.showMessages = new JRadioButton();
      this.filterLabel = new JLabel();
      this.messageFilter = new JTextField();
      this.keepAllMessages = new JRadioButton();
      this.tablePanel = new JScrollPane();
      this.recordTable = new JTable();
      this.bottomPanel = new JPanel();
      this.headerPanel = new JPanel();
      this.rightPanel = new JPanel();
      this.levelLabel = new JLabel();
      this.methodLabel = new JLabel();
      this.methodField = new JLabel();
      this.levelField = new JLabel();
      this.leftPanel = new JPanel();
      this.timeLabel = new JLabel();
      this.classLabel = new JLabel();
      this.timeField = new JLabel();
      this.classField = new JLabel();
      this.detailsScroller = new JScrollPane();
      this.detailsDisplay = new JTextArea();
      this.jLabel5.setText("jLabel5");
      this.jLabel1.setText("jLabel1");
      this.levelSelector.addItem(Level.SEVERE);
      this.levelSelector.addItem(Level.WARNING);
      this.levelSelector.addItem(Level.INFO);
      this.levelSelector.addItem(Level.CONFIG);
      this.levelSelector.addItem(Level.FINE);
      this.levelSelector.addItem(Level.FINER);
      this.levelSelector.addItem(Level.FINEST);
      this.levelSelector.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent var1) {
            JavaLogViewer.this.levelSelectorItemStateChanged(var1);
         }
      });
      this.clearButton.setText("Clear");
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            JavaLogViewer.this.clearButtonActionPerformed(var1);
         }
      });
      this.mainDisplay.setOrientation(0);
      this.mainDisplay.setResizeWeight(0.8D);
      GroupLayout var1 = new GroupLayout(this.sectionPanel);
      this.sectionPanel.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(0, 177, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(0, 192, 32767));
      this.buttonGroup1.add(this.discardMessages);
      this.discardMessages.setText("Discard New Messages");
      this.discardMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.discardMessages.setMargin(new Insets(0, 0, 0, 0));
      this.discardMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            JavaLogViewer.this.filterPerformed(var1);
         }
      });
      this.buttonGroup1.add(this.holdMessages);
      this.holdMessages.setText("Hold New Messages");
      this.holdMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.holdMessages.setMargin(new Insets(0, 0, 0, 0));
      this.holdMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            JavaLogViewer.this.filterPerformed(var1);
         }
      });
      this.buttonGroup1.add(this.showMessages);
      this.showMessages.setSelected(true);
      this.showMessages.setText("Show New Messages");
      this.showMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.showMessages.setMargin(new Insets(0, 0, 0, 0));
      this.showMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            JavaLogViewer.this.filterPerformed(var1);
         }
      });
      this.filterLabel.setText("Message:");
      this.messageFilter.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent var1) {
            JavaLogViewer.this.messageFilterKeyReleased(var1);
         }
      });
      this.buttonGroup1.add(this.keepAllMessages);
      this.keepAllMessages.setText("Keep All Messages");
      this.keepAllMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.keepAllMessages.setMargin(new Insets(0, 0, 0, 0));
      this.keepAllMessages.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            JavaLogViewer.this.filterPerformed(var1);
         }
      });
      GroupLayout var2 = new GroupLayout(this.filterPanel);
      this.filterPanel.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(2, var2.createSequentialGroup().addContainerGap().add(var2.createParallelGroup(1).add(this.holdMessages, -1, 157, 32767).add(2, this.showMessages, -1, 157, 32767).add(var2.createSequentialGroup().add(this.filterLabel).addPreferredGap(0).add(this.messageFilter, -1, 107, 32767)).add(2, this.discardMessages, -1, 157, 32767).add(var2.createSequentialGroup().add(this.keepAllMessages, -1, 155, 32767).add(2, 2, 2))).addContainerGap()).add(this.sectionPanel, -1, -1, 32767));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(2, var2.createSequentialGroup().add(this.sectionPanel, -1, -1, 32767).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.filterLabel).add(this.messageFilter, -2, -1, -2)).addPreferredGap(0).add(this.showMessages).addPreferredGap(0).add(this.holdMessages).addPreferredGap(0).add(this.discardMessages).addPreferredGap(0).add(this.keepAllMessages)));
      this.sectionPanel.setLayout(new BorderLayout());
      this.sectionPanel.add(this.sectionFilterTable, "Center");
      this.recordTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.recordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            try {
               Object var2 = JavaLogViewer.this.filterModel.getValueAt(JavaLogViewer.this.recordTable.getSelectedRow(), LogRecordTableColumns.LEVEL.ordinal());
               if (var2 != null && var2 instanceof LogRecord) {
                  LogRecord var3 = (LogRecord)LogRecord.class.cast(var2);
                  JavaLogViewer.this.timeField.setText(String.format("%d", var3.getMillis()));
                  JavaLogViewer.this.classField.setText(var3.getSourceClassName());
                  JavaLogViewer.this.levelField.setText(var3.getLevel().getLocalizedName());
                  JavaLogViewer.this.methodField.setText(var3.getSourceMethodName());
                  if (var3.getThrown() != null) {
                     Throwable var4 = var3.getThrown();
                     StringWriter var5 = new StringWriter();
                     var4.printStackTrace(new PrintWriter(var5));
                     JavaLogViewer.this.detailsDisplay.setText(var5.toString());
                  } else {
                     JavaLogViewer.this.detailsDisplay.setText(var3.getMessage());
                  }

                  JavaLogViewer.this.detailsDisplay.setSelectionStart(0);
                  JavaLogViewer.this.detailsDisplay.setSelectionEnd(0);
                  return;
               }
            } catch (Exception var6) {
            }

            JavaLogViewer.this.timeField.setText("");
            JavaLogViewer.this.classField.setText("");
            JavaLogViewer.this.levelField.setText("");
            JavaLogViewer.this.methodField.setText("");
            JavaLogViewer.this.detailsDisplay.setText("");
         }
      });
      this.tablePanel.setViewportView(this.recordTable);
      this.recordTable.setColumnModel(this.hideColumns);
      this.recordTable.setModel(this.filterModel);
      this.hideColumns.applyToTable(this.recordTable);
      this.recordTable.setDefaultRenderer(Calendar.class, new CalendarTimeCellRenderer());
      this.recordTable.setDefaultRenderer(LogRecord.class, new LevelTableCellRenderer(ImageManager.SIZE16));
      this.hideColumns.hide(LogRecordTableColumns.TIME);
      this.setColumnWidth();
      GroupLayout var3 = new GroupLayout(this.topPanel);
      this.topPanel.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(2, var3.createSequentialGroup().add(this.tablePanel, -1, 480, 32767).addPreferredGap(0).add(this.filterPanel, -2, -1, -2)));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().add(this.filterPanel, -1, -1, 32767).add(11, 11, 11)).add(this.tablePanel, -1, 313, 32767));
      this.mainDisplay.setTopComponent(this.topPanel);
      this.levelLabel.setFont(this.levelLabel.getFont().deriveFont(this.levelLabel.getFont().getStyle() | 1));
      this.levelLabel.setText("Level:");
      this.methodLabel.setFont(this.methodLabel.getFont().deriveFont(this.methodLabel.getFont().getStyle() | 1));
      this.methodLabel.setText("Method:");
      this.methodField.setText(" ");
      this.levelField.setText(" ");
      GroupLayout var4 = new GroupLayout(this.rightPanel);
      this.rightPanel.setLayout(var4);
      var4.setHorizontalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().add(var4.createParallelGroup(1).add(var4.createSequentialGroup().add(this.methodLabel).addPreferredGap(0).add(this.methodField, -1, 275, 32767)).add(var4.createSequentialGroup().add(this.levelLabel).add(18, 18, 18).add(this.levelField, -1, 276, 32767))).addContainerGap()));
      var4.setVerticalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().add(var4.createParallelGroup(3).add(this.levelLabel).add(this.levelField)).addPreferredGap(0).add(var4.createParallelGroup(3).add(this.methodLabel).add(this.methodField)).addContainerGap(-1, 32767)));
      this.timeLabel.setFont(this.timeLabel.getFont().deriveFont(this.timeLabel.getFont().getStyle() | 1));
      this.timeLabel.setText("Time:");
      this.classLabel.setFont(this.classLabel.getFont().deriveFont(this.classLabel.getFont().getStyle() | 1));
      this.classLabel.setText("Class:");
      this.timeField.setText(" ");
      this.classField.setText(" ");
      GroupLayout var5 = new GroupLayout(this.leftPanel);
      this.leftPanel.setLayout(var5);
      var5.setHorizontalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(this.classLabel).addPreferredGap(0).add(this.classField, -1, 272, 32767)).add(var5.createSequentialGroup().add(this.timeLabel).addPreferredGap(0).add(this.timeField, -1, 273, 32767))).addContainerGap()));
      var5.setVerticalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(var5.createParallelGroup(3).add(this.timeLabel).add(this.timeField)).addPreferredGap(0).add(var5.createParallelGroup(3, false).add(this.classLabel).add(this.classField, -2, 14, -2)).addContainerGap()));
      GroupLayout var6 = new GroupLayout(this.headerPanel);
      this.headerPanel.setLayout(var6);
      var6.setHorizontalGroup(var6.createParallelGroup(1).add(2, var6.createSequentialGroup().add(this.leftPanel, -1, -1, 32767).addPreferredGap(0).add(this.rightPanel, -1, -1, 32767)));
      var6.setVerticalGroup(var6.createParallelGroup(1).add(var6.createSequentialGroup().add(var6.createParallelGroup(1).add(this.rightPanel, -2, -1, -2).add(this.leftPanel, -1, -1, 32767)).addContainerGap()));
      this.detailsDisplay.setColumns(20);
      this.detailsDisplay.setRows(5);
      this.detailsScroller.setViewportView(this.detailsDisplay);
      GroupLayout var7 = new GroupLayout(this.bottomPanel);
      this.bottomPanel.setLayout(var7);
      var7.setHorizontalGroup(var7.createParallelGroup(1).add(2, this.headerPanel, -1, -1, 32767).add(this.detailsScroller, -1, 663, 32767));
      var7.setVerticalGroup(var7.createParallelGroup(1).add(var7.createSequentialGroup().add(this.headerPanel, -2, 48, -2).addPreferredGap(0).add(this.detailsScroller, -1, 72, 32767)));
      this.mainDisplay.setRightComponent(this.bottomPanel);
      GroupLayout var8 = new GroupLayout(this);
      this.setLayout(var8);
      var8.setHorizontalGroup(var8.createParallelGroup(1).add(2, var8.createSequentialGroup().add(this.levelSelector, 0, 600, 32767).addPreferredGap(0).add(this.clearButton)).add(this.mainDisplay, -1, 665, 32767));
      var8.setVerticalGroup(var8.createParallelGroup(1).add(var8.createSequentialGroup().add(var8.createParallelGroup(3).add(this.clearButton).add(this.levelSelector, -2, -1, -2)).addPreferredGap(0).add(this.mainDisplay, -1, 446, 32767)));
   }

   private void clearButtonActionPerformed(ActionEvent var1) {
      this.recordModel.clear();
   }

   private void levelSelectorItemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() == 1) {
         Object var2 = var1.getItem();
         if (var2 instanceof Level) {
            Level var3 = (Level)Level.class.cast(var2);
            if (var3 != null) {
               this.levelPredicate.level = var3;
               this.filterModel.filterChanged();
            }
         }

      }
   }

   private void messageFilterKeyReleased(KeyEvent var1) {
      this.doFilter();
   }

   private void filterPerformed(ActionEvent var1) {
      if (this.showMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.SHOW);
      }

      if (this.discardMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.DISCARD);
      }

      if (this.holdMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.HOLD);
      }

      if (this.keepAllMessages.isSelected()) {
         this.recordModel.setRecordState(RecordState.KEEP);
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

      public void selected(String var1, boolean var2) {
         if (var2) {
            JavaLogViewer.this.sectionPredicate.addItem(var1.toString());
         } else {
            JavaLogViewer.this.sectionPredicate.removeItem(var1.toString());
         }

         JavaLogViewer.this.filterModel.filterChanged();
      }

      // $FF: synthetic method
      CheckedTableCallback(Object var2) {
         this();
      }
   }
}
