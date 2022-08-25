package ddb.dsz.plugin.transfermonitor.tabs;

import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.TransferMonitorColumns;
import ddb.dsz.plugin.transfermonitor.TransferMonitorHost;
import ddb.dsz.plugin.transfermonitor.TransferTabbable;
import ddb.dsz.plugin.transfermonitor.model.TransferDirection;
import ddb.dsz.plugin.transfermonitor.model.TransferMonitorModel;
import ddb.dsz.plugin.transfermonitor.model.TransferMonitorTableSorter;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import ddb.dsz.plugin.transfermonitor.renderer.ProgressCellRenderer;
import ddb.dsz.plugin.transfermonitor.renderer.StateRenderer;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import ddb.listeners.mouse.MousePopup;
import ddb.util.TableSorter;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

public class TransferMainTab extends TransferTabbable {
   TransferMonitorModel getMonitorModel;
   int runningFileTypes = 0;
   JTable getTable;
   TableSorter sorter;
   JPopupMenu contextMenu = new JPopupMenu();
   TransferMonitorHost monitor;
   CoreController core;
   private static final String[] LIMITATIONS = new String[]{"-range", "-tail"};

   public TransferMainTab(TransferMonitorHost var1, CoreController var2) {
      super("Transfers");
      this.core = var2;
      super.setShowButtons(true);
      this.monitor = var1;
      this.getMonitorModel = new TransferMonitorModel();
      this.sorter = new TransferMonitorTableSorter(this.getMonitorModel);
      this.getTable = new JTable();
      ColumnHidingModel var3 = new ColumnHidingModel(TransferMonitorColumns.class);
      this.getTable.setColumnModel(var3);
      this.getTable.setModel(this.sorter);
      this.sorter.addMouseListenerToHeaderInTable(this.getTable);
      var3.applyToTable(this.getTable);
      this.sorter.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent var1) {
            TransferMainTab.this.sorter.sort(TransferMainTab.this);
         }
      });
      this.getTable.setDefaultRenderer(Calendar.class, new DszTableCellRenderer() {
         SimpleDateFormat FullFormat = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
         SimpleDateFormat ShortFormat = new SimpleDateFormat("h:mm:ss a");

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Calendar) {
               Calendar var8 = (Calendar) value;
               JLabel var9 = (JLabel)var7;
               Calendar var10 = Calendar.getInstance();
               var10.add(5, -1);
               if (var8.compareTo(var10) < 0) {
                  var9.setText(this.FullFormat.format(var8.getTime()));
               } else {
                  var9.setText(this.ShortFormat.format(var8.getTime()));
               }
            }

            return var7;
         }
      });
      this.getTable.setDefaultRenderer(TransferRecord.class, new ProgressCellRenderer());
      this.getTable.setDefaultRenderer(TransferState.class, new StateRenderer(this.core));
      this.display.add(new JScrollPane(this.getTable));
      this.setColumnWidths();
      final JMenuItem var4 = new JMenuItem("Save");
      final JMenuItem var5 = new JMenuItem("Save All");
      final JMenuItem var6 = new JMenuItem("Open");
      final JMenuItem var7 = new JMenuItem("Copy Path");
      final JMenuItem var8 = new JMenuItem("Resume");
      this.contextMenu.add(var6);
      this.contextMenu.add(var4);
      this.contextMenu.add(var5);
      this.contextMenu.add(var7);
      this.contextMenu.add(var8);
      final JMenuItem[] var9 = new JMenuItem[]{var4, var5, var6, var7, var8};
      var4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TransferRecord[] var2 = TransferMainTab.this.getSelectedRecords();
            TransferRecord[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               TransferRecord var6 = var3[var5];
               if (!TransferMainTab.this.monitor.save(var6)) {
                  break;
               }
            }

         }
      });
      var6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TransferRecord[] var2 = TransferMainTab.this.getSelectedRecords();
            TransferRecord[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               TransferRecord var6 = var3[var5];
               TransferMainTab.this.monitor.open(var6);
            }

         }
      });
      var7.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TransferRecord[] var2 = TransferMainTab.this.getSelectedRecords();
            TransferRecord[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               TransferRecord var6 = var3[var5];
               File var7 = TransferMainTab.this.monitor.getFileFromRecord(var6);

               String var8;
               try {
                  var8 = var7.getCanonicalPath();
               } catch (Exception var11) {
                  var8 = var7.getAbsolutePath();
               }

               StringSelection var9 = new StringSelection(var8);
               Clipboard var10 = Toolkit.getDefaultToolkit().getSystemClipboard();
               var10.setContents(var9, (ClipboardOwner)null);
            }

         }
      });
      var8.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TransferRecord[] var2 = TransferMainTab.this.getSelectedRecords();
            TransferRecord[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               TransferRecord var6 = var3[var5];
               if (var6.isResumable()) {
                  String[] var7 = new String[]{"request", "get", "fullpath=" + var6.getRemote(), "bytes=" + (var6.getSize() - var6.getTransfered()), "offset=" + var6.getTransfered(), "host=" + TransferMainTab.this.monitor.getTarget().getId()};
                  TransferMainTab.this.core.internalCommand(new InternalCommandCallback() {
                     @Override
                     public void taskingRecieved(List<String> var1, Object var2) {
                     }

                     @Override
                     public void taskingExecuted(Object var1, Object var2) {
                     }

                     @Override
                     public void taskingRejected(Object var1, Object var2) {
                     }
                  }, var7);
               }
            }

         }
      });
      var5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TransferMainTab.this.monitor.saveAll(TransferMainTab.this.getSelectedRecords());
         }
      });
      this.getTable.addMouseListener(new MousePopup() {
         protected void maybePopup(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               int var2 = TransferMainTab.this.getTable.rowAtPoint(var1.getPoint());
               if (var2 < 0 || var2 >= TransferMainTab.this.getTable.getRowCount()) {
                  return;
               }

               int[] var3 = TransferMainTab.this.getTable.getSelectedRows();
               boolean var4x = false;
               int[] var5x = var3;
               int var6x = var3.length;

               int var7x;
               int var8x;
               for(var7x = 0; var7x < var6x; ++var7x) {
                  var8x = var5x[var7x];
                  if (var8x == var2) {
                     var4x = true;
                  }
               }

               if (var4x && var3.length > 1) {
                  var5.setEnabled(true);
               } else {
                  TransferMainTab.this.getTable.getSelectionModel().clearSelection();
                  TransferMainTab.this.getTable.getSelectionModel().addSelectionInterval(var2, var2);
               }

               JMenuItem[] var10 = var9;
               var6x = var10.length;

               for(var7x = 0; var7x < var6x; ++var7x) {
                  JMenuItem var13 = var10[var7x];
                  var13.setEnabled(false);
               }

               TransferRecord[] var11 = TransferMainTab.this.getSelectedRecords();
               TransferRecord[] var12 = var11;
               var7x = var11.length;

               for(var8x = 0; var8x < var7x; ++var8x) {
                  TransferRecord var9x = var12[var8x];
                  if (var9x.isResumable()) {
                     var8.setEnabled(true);
                  }

                  if (var9x.getDirection().equals(TransferDirection.GET) && !var9x.getState().equals(TransferState.STARTED)) {
                     var7.setEnabled(true);
                     if (var4.isEnabled()) {
                        var5.setEnabled(true);
                     }

                     var4.setEnabled(true);
                     var6.setEnabled(true);
                  }
               }

               TransferMainTab.this.contextMenu.show(TransferMainTab.this.getTable, var1.getX(), var1.getY());
            }

         }
      });
      this.getTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            if (var1.getClickCount() == 2) {
               int var2 = TransferMainTab.this.getTable.rowAtPoint(var1.getPoint());
               if (var2 < 0 || var2 >= TransferMainTab.this.getTable.getRowCount()) {
                  return;
               }

               TransferRecord var3 = (TransferRecord)TransferRecord.class.cast(TransferMainTab.this.sorter.getValueAt(var2, TransferMonitorColumns.SIZE.ordinal()));
               TransferMainTab.this.monitor.open(var3);
            }

         }
      });
      var3.hide(TransferMonitorColumns.TYPE);
      var3.hide(TransferMonitorColumns.ID);
      var3.hide(TransferMonitorColumns.TIME_ACCESSED);
      var3.hide(TransferMonitorColumns.TIME_CREATED);
      var3.hide(TransferMonitorColumns.TIME_MODIFIED);
   }

   private TransferRecord[] getSelectedRecords() {
      int[] var1 = this.getTable.getSelectedRows();
      TransferRecord[] var2 = new TransferRecord[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = (TransferRecord)TransferRecord.class.cast(this.sorter.getValueAt(var1[var3], TransferMonitorColumns.SIZE.ordinal()));
      }

      return var2;
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }

   public JComponent initialFocus() {
      return null;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.LEFT;
   }

   @Override
   public boolean isClosable() {
      return false;
   }

   @Override
   public boolean isHideable() {
      return false;
   }

   @Override
   public boolean isUnhideable() {
      return true;
   }

   public void commandEnded(Task var1) {
      this.getMonitorModel.commandEnded(var1);
   }

   private void setColumnWidth(TransferMonitorColumns var1, String var2, boolean var3, boolean var4) {
      JLabel var5 = new JLabel(var2);
      if (var3) {
         try {
            var5.setIcon(ImageManager.getIcon("images/up.png", this.core.getLabelImageSize()));
         } catch (NullPointerException var8) {
         }
      }

      TableColumn var6 = this.getTable.getColumnModel().getColumn(var1.ordinal());
      int var7 = var5.getPreferredSize().width;
      var6.setWidth(var7);
      if (var4) {
         var6.setMinWidth(var7);
         var6.setMaxWidth(var7);
      }

   }

   private void setColumnWidths() {
      this.setColumnWidth(TransferMonitorColumns.ID, " 88 ", true, true);
      this.setColumnWidth(TransferMonitorColumns.STATE, " state ", true, true);
      this.setColumnWidth(TransferMonitorColumns.SIZE, "0000000000 of 0000000000  ", true, false);
   }

   public void addRecord(TransferRecord var1) {
      this.getMonitorModel.addRecord(var1);
   }

   public int getNext() {
      return this.getMonitorModel.getNext();
   }

   public TransferRecord getRecord(int var1) {
      return this.getMonitorModel.getRecord(var1);
   }

   public void recordChanged(TransferRecord var1) {
      this.getMonitorModel.recordChanged(var1);
   }
}
