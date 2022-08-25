package ddb.dsz.plugin.filemanager.ver3.search;

import ddb.dsz.core.contextmenu.CommandCallbackListener;
import ddb.dsz.core.contextmenu.ContextMenuAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.filemanager.ver3.FileManager;
import ddb.dsz.plugin.filemanager.ver3.FileManagerHost;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.targetmodel.TargetModel;
import ddb.targetmodel.TargetModelFactory;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileSystemListener;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import org.apache.commons.collections.Closure;

public class Search extends FileManagerHost implements FileSystemListener, CommandCallbackListener, ContextMenuAction {
   public static String SEARCH_ROOT = "-searchRoot";
   SearchParameters parameters;
   SearchResultModel resultsModel;
   JTable results = new JTable();
   Searcher searcher = null;
   ColumnHidingModel columnModel = new ColumnHidingModel(SearchResultColumns.class);
   Closure startSearching = new Closure() {
      public void execute(Object var1) {
         if (Search.this.searcher != null) {
            if (Search.this.searcher.isRunning()) {
               return;
            }

            Search.this.searcher = null;
         }

         Search.this.resultsModel.clear();
         Search.this.parameters.searchingStarted();
         SearchParameters var2 = (SearchParameters)var1;
         Search.this.searcher = new Searcher(Search.this.model, Search.this, Search.this.onFinished, Search.this.addNode);
         if (!var2.configure(Search.this.searcher)) {
            JOptionPane.showMessageDialog(var2, "Unable to configure parser");
            Search.this.parameters.searchingStopped();
         } else {
            Thread var3 = Search.this.core.newThread(Search.this.searcher);
            var3.start();
            Search.this.status.setIndeterminate(true);
            Search.this.status.notifyObservers();
         }
      }
   };
   Closure stopSearching = new Closure() {
      public void execute(Object var1) {
         Searcher var2 = Search.this.searcher;
         if (var2 != null) {
            var2.stop();
         }
      }
   };
   Closure onFinished = new Closure() {
      public void execute(Object var1) {
         Search.this.parameters.searchingStopped();
         Search.this.status.setIndeterminate(false);
         Search.this.status.notifyObservers();
      }
   };
   Closure addNode = new Closure() {
      public void execute(Object var1) {
         Search.this.resultsModel.addFile((FileObject)FileObject.class.cast(var1));
      }
   };
   long searchRoot = -1L;
   JSplitPane searchDisplay2;

   public Search(HostInfo var1, CoreController var2, FileManager var3, long var4) {
      super(var1, var2, var3);
      super.setName("Search");
      super.setShowButtons(false);
      this.results.setColumnModel(this.columnModel);
      this.columnModel.applyToTable(this.results);
      TargetModel var6 = TargetModelFactory.getTargetModel(var2, var1);
      if (var6 == null) {
         var2.logEvent(Level.SEVERE, "Unable to get TargetModel");
      } else {
         this.model = var6.getFileSystemModel();
         if (this.model == null) {
            var2.logEvent(Level.SEVERE, "Unable to get FileSystemModel");
         } else {
            this.setFileSystemModel(this.model);
            this.parameters = new SearchParameters(var2.getHosts(), var1);
            this.searchRoot = var4;
            this.searchDisplay2 = new JSplitPane(1);
            super.setDisplay(this.searchDisplay2);
            JScrollPane var7 = new JScrollPane(this.parameters);
            var7.setHorizontalScrollBarPolicy(31);
            this.searchDisplay2.setLeftComponent(var7);
            this.searchDisplay2.setRightComponent(new JScrollPane(this.results));
            this.searchDisplay2.setOneTouchExpandable(true);
            this.results.setDefaultRenderer(Calendar.class, new DszTableCellRenderer() {
               SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy h:mm:ss a");

               public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                  Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                  if (var7 instanceof JLabel && value instanceof Calendar) {
                     Calendar var8 = (Calendar) value;
                     JLabel var9 = (JLabel)var7;
                     var9.setText(this.format.format(var8.getTime()));
                  }

                  return var7;
               }
            });
            this.results.setDefaultRenderer(Integer.class, new DszTableCellRenderer() {
               public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                  Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                  if (var7 instanceof JLabel && value instanceof Integer) {
                     Integer var8 = (Integer) value;
                     JLabel var9 = (JLabel)var7;
                     if (var8.equals(SearchResultModel.IS_DIR)) {
                        var9.setText("<DIR>");
                     }

                     var9.setHorizontalAlignment(4);
                  }

                  return var7;
               }
            });
            this.results.setDefaultRenderer(FileObject.class, new DszTableCellRenderer() {
               public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                  Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                  if (var7 instanceof JLabel && value instanceof FileObject) {
                     JLabel var8 = (JLabel)var7;
                     FileObject var9 = (FileObject) value;
                     var8.setIcon(FileManagerHost.getIcon(var9.getIcon()));
                     var8.setText(var9.getName());
                  }

                  return var7;
               }
            });
            this.results.setSelectionMode(2);
            this.results.addMouseListener(new MouseAdapter() {
               private boolean maybePopup(MouseEvent var1) {
                  if (!var1.isPopupTrigger()) {
                     return false;
                  } else {
                     var1.consume();
                     int var2 = var1.getY() / Search.this.results.getRowHeight();
                     if (Search.this.results.getSelectionModel().isSelectedIndex(var2)) {
                        ArrayList var3 = new ArrayList();
                        ArrayList var4 = new ArrayList();
                        int[] var5 = Search.this.results.getSelectedRows();
                        int var6 = var5.length;

                        for(int var7 = 0; var7 < var6; ++var7) {
                           int var8 = var5[var7];
                           Object var9 = Search.this.resultsModel.getFileObjectAt(var8);
                           if (var9 instanceof FileObject) {
                              var3.add(FileObject.class.cast(var9));
                           } else if (var9 instanceof Integer) {
                              var4.add(Integer.class.cast(var9));
                           }
                        }

                        Search.this.showPopup(var1, var3, var4);
                     } else if (var2 < Search.this.results.getRowCount()) {
                        Search.this.results.getSelectionModel().setSelectionInterval(var2, var2);
                        FileObject var10 = (FileObject)Search.this.resultsModel.getValueAt(var2, SearchResultColumns.Name);
                        Search.this.showPopup(var1, var10);
                     }

                     return true;
                  }
               }

               public void mouseClicked(MouseEvent var1) {
                  if (!this.maybePopup(var1)) {
                     ;
                  }
               }

               public void mousePressed(MouseEvent var1) {
                  this.maybePopup(var1);
               }

               public void mouseReleased(MouseEvent var1) {
                  this.maybePopup(var1);
               }
            });
            if (this.model == null) {
               var2.logEvent(Level.WARNING, "Unable to open search window");
               throw new RuntimeException("No model");
            } else {
               this.resultsModel = new SearchResultModel(this.model);
               this.results.setModel(this.resultsModel);
               this.columnModel.hide(SearchResultColumns.Accessed);
               this.columnModel.hide(SearchResultColumns.Created);
               this.columnModel.hide(SearchResultColumns.Modified);
               Iterator var8 = this.model.getDrives().iterator();

               while(var8.hasNext()) {
                  FileObject var9 = (FileObject)var8.next();
                  this.parameters.addSearchRoot(var9, false);
               }

               if (this.searchRoot != -1L) {
                  this.parameters.addSearchRoot(this.model.getSearchFile(this.searchRoot), true);
               }

               this.parameters.setSearchActions(this.startSearching, this.stopSearching);
            }
         }
      }
   }

   protected void modelChanged() {
   }

   @Override
   public void setStatus(final String status) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            Search.super.setStatus(status);
         }
      });
   }

   @Override
   public boolean isVerifyClose() {
      return false;
   }
}
