package ddb.dsz.plugin.filemanager.ver3.browser;

import ddb.bcb.BreadcrumbBar;
import ddb.bcb.BreadcrumbBarCallBack;
import ddb.bcb.BreadcrumbBarEvent;
import ddb.bcb.BreadcrumbBarListener;
import ddb.bcb.BreadcrumbItem;
import ddb.bcb.BreadcrumbItemChoices;
import ddb.detach.TabbableStatus;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.filemanager.ver3.FileManager;
import ddb.dsz.plugin.filemanager.ver3.FileManagerHost;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.targetmodel.TargetModel;
import ddb.targetmodel.TargetModelFactory;
import ddb.targetmodel.filemodel.DriveType;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;

public class Browser extends FileManagerHost implements BreadcrumbBarCallBack, BreadcrumbBarListener {
   public static final int MAX_LENGTH = 100;
   BreadcrumbBar navigator = new BreadcrumbBar(">", this);
   JTable files = new JTable();
   DirectoryListingModel2 directoryModel = null;
   ColumnHidingModel columnModel = new ColumnHidingModel(DirectoryListingColumns.class);
   SortingTableHeader sorter;
   FileObject root = null;
   JPanel browseDisplay;
   JButton rootButton = new JButton("");
   final Object LOCK = new Object();

   public Browser(HostInfo var1, CoreController var2, FileManager var3, long var4) {
      super(var1, var2, var3);
      super.setName(var1.getId());
      super.setShowButtons(false);
      this.files.setColumnModel(this.columnModel);
      this.columnModel.applyToTable(this.files);
      this.browseDisplay = new JPanel(new BorderLayout());
      JToolBar var6 = new JToolBar(0);
      var6.add(this.rootButton);
      var6.add(this.navigator);
      var6.setFloatable(false);
      this.rootButton.setIcon(getIcon(DriveType.FILEMANAGER.getIcon()));
      this.rootButton.setToolTipText("File System Root");
      this.rootButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            Browser.this.setNavigation(FileSystemModel.ROOT);
            if (Browser.this.directoryModel != null) {
               Browser.this.directoryModel.setParent(FileSystemModel.ROOT, (String)null, (FileObject)null);
            }

         }
      });
      this.browseDisplay.add(var6, "North");
      JScrollPane var7 = new JScrollPane(this.files);
      this.browseDisplay.add(var7);
      super.setDisplay(this.browseDisplay);
      this.files.setDefaultRenderer(Calendar.class, new DszTableCellRenderer() {
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
      this.files.setDefaultRenderer(Integer.class, new DszTableCellRenderer() {
         {
            super.setHorizontalAlignment(4);
         }

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Number) {
               Number var8 = (Number) value;
               JLabel var9 = (JLabel)var7;
               if (var8.equals(DirectoryListingModel2.IS_DIR)) {
                  var9.setText("<DIR>");
               }
            }

            return var7;
         }
      });
      this.files.setDefaultRenderer(FileObject.class, new DszTableCellRenderer() {
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
      this.files.setSelectionMode(2);
      this.files.addMouseListener(new MouseAdapter() {
         private boolean maybePopup(MouseEvent var1) {
            if (!var1.isPopupTrigger()) {
               return false;
            } else {
               var1.consume();
               int var2 = var1.getY() / Browser.this.files.getRowHeight();
               if (!Browser.this.files.getSelectionModel().isSelectedIndex(var2)) {
                  if (var2 >= Browser.this.files.getRowCount()) {
                     return false;
                  }

                  Browser.this.files.getSelectionModel().setSelectionInterval(var2, var2);
               }

               ArrayList var3 = new ArrayList();
               ArrayList var4 = new ArrayList();
               int[] var5 = Browser.this.files.getSelectedRows();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  int var8 = var5[var7];
                  Object var9 = Browser.this.directoryModel.getFileObjectAt(var8);
                  if (var9 instanceof FileObject) {
                     var3.add(FileObject.class.cast(var9));
                  } else if (var9 instanceof Integer) {
                     var4.add(Integer.class.cast(var9));
                  }
               }

               Browser.this.showPopup(var1, var3, var4);
               return true;
            }
         }

         public void mouseClicked(MouseEvent var1) {
            if (!this.maybePopup(var1)) {
               if (var1.getClickCount() == 2) {
                  int var2 = var1.getY() / Browser.this.files.getRowHeight();
                  if (var2 < Browser.this.files.getRowCount()) {
                     FileObject var3 = (FileObject)Browser.this.directoryModel.getValueAt(var2, DirectoryListingColumns.Name);
                     if (var3 != null && var3.isDirectory()) {
                        BreadcrumbItem var4 = Browser.this.makeBreadcrumb(var3);
                        Browser.this.navigator.pushChoice(var4);
                        BreadcrumbItemChoices var5 = new BreadcrumbItemChoices(new BreadcrumbItem[0]);
                        Browser.this.navigator.pushChoices(var5, true);
                        Browser.exec.execute(Browser.this.new AdjustChoices(var5, var3.getId(), FileManagerHost.makePath(var3)));
                     }
                  }
               }

            }
         }

         public void mousePressed(MouseEvent var1) {
            this.maybePopup(var1);
         }

         public void mouseReleased(MouseEvent var1) {
            this.maybePopup(var1);
         }
      });
      var7.addMouseListener(new MouseAdapter() {
         private boolean maybePopup(MouseEvent var1) {
            if (!var1.isPopupTrigger()) {
               return false;
            } else {
               var1.consume();
               FileObject var2 = Browser.this.directoryModel.getParentFile();
               Browser.this.showPopup(var1, var2);
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
      this.sorter = new SortingTableHeader(new SortingTableHeader.SortingTableHeaderCallback() {
         public void setSorting(TableColumn var1, boolean var2) {
            if (var1 == null) {
               Browser.this.directoryModel.setSorting(DirectoryListingColumns.Name, true);
            }

            if (Browser.this.directoryModel != null) {
               DirectoryListingColumns var3 = null;
               DirectoryListingColumns[] var4 = DirectoryListingColumns.values();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  DirectoryListingColumns var7 = var4[var6];
                  if (var7.text.equals(var1.getHeaderValue().toString())) {
                     var3 = var7;
                     break;
                  }
               }

               Browser.this.directoryModel.setSorting(var3, var2);
            }

         }
      });
      this.sorter.addMouseListenerToHeaderInTable(this.files);
      TargetModel var8 = TargetModelFactory.getTargetModel(var2, var1);
      if (var8 == null) {
         var2.logEvent(Level.SEVERE, "Unable to get TargetModel");
      } else {
         FileSystemModel var9 = var8.getFileSystemModel();
         if (var9 == null) {
            var2.logEvent(Level.SEVERE, "Unable to get FileSystemModel");
         } else {
            this.setFileSystemModel(var9);
            this.navigator.addListener(this);
            this.setNavigation(var4);
         }
      }
   }

   private String setNavigation(long var1) {
      String var3 = null;
      BreadcrumbItemChoices var4 = new BreadcrumbItemChoices(new BreadcrumbItem[0]);

      String var10;
      try {
         if (var1 == FileSystemModel.ROOT) {
            this.navigator.getStack().clear();
            this.navigator.pushChoices(var4);
         } else {
            FileObject var5 = this.model.getFile(var1, (String)null);
            BreadcrumbItem var6;
            if (var5 == null) {
               var6 = null;
               return null;
            }

            var3 = this.setNavigation((long)Long.valueOf(var5.getParent()).intValue());
            var5.setPath(var3);
            var3 = makePath(var5);
            var6 = this.makeBreadcrumb(var5);
            this.navigator.pushChoice(var6);
            this.navigator.pushChoices(var4);
         }

         var10 = var3;
      } finally {
         exec.execute(new Browser.AdjustChoices(var4, var1, var3));
      }

      return var10;
   }

   protected BreadcrumbItem makeBreadcrumb(FileObject var1) {
      BreadcrumbItem var2 = new BreadcrumbItem(new String[]{var1.getName(), var1.getId().toString(), var1.getPath()});
      var2.setIcon(getIcon(var1.getIcon()));
      return var2;
   }

   protected void setFileSystemModel(FileSystemModel var1) {
      super.setFileSystemModel(var1);
      this.directoryModel.setModel(var1);
   }

   protected void modelChanged() {
      if (this.model != null) {
         BreadcrumbItemChoices var1 = new BreadcrumbItemChoices(new BreadcrumbItem[0]);
         synchronized(this.LOCK) {
            this.navigator.getStack().clear();
            this.directoryModel = new DirectoryListingModel2(this.model, this.status);
            this.sorter.setNoSort();
            this.files.setModel(this.directoryModel);
            DirectoryListingColumns[] var3 = DirectoryListingColumns.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               DirectoryListingColumns var6 = var3[var5];
               if (!var6.isShow()) {
                  this.columnModel.hide(var6);
               } else {
                  this.columnModel.show(var6);
               }
            }

            this.root = this.model.getFile(FileSystemModel.ROOT, (String)null);
            if (this.root == null) {
               return;
            }

            this.navigator.getStack().clear();
            this.navigator.pushChoices(var1);
         }

         exec.execute(new Browser.AdjustChoices(var1, FileSystemModel.ROOT, (String)null));
      }
   }

   public BreadcrumbItemChoices getChoices(BreadcrumbItem[] var1) {
      BreadcrumbItemChoices var2 = new BreadcrumbItemChoices(new BreadcrumbItem[0]);

      try {
         exec.execute(new Browser.AdjustChoices(var2, (long)Integer.parseInt(var1[var1.length - 1].getValue()[1]), makePath(var1[var1.length - 1].getValue()[2], var1[var1.length - 1].getValue()[0])));
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return var2;
   }

   public void fileChanged(FileObject var1) {
      if (var1 != null) {
         this.directoryModel.fileChanged(var1);
         if (var1.isDirectory()) {
            long var2 = var1.getParent();
            if (var2 != -1L) {
               if (var2 == Long.valueOf(FileSystemModel.ROOT)) {
                  BreadcrumbItemChoices var4 = new BreadcrumbItemChoices(new BreadcrumbItem[0]);
                  EventQueue.invokeLater(new Browser.SetNavigatorChoices(0, var4, true));
                  exec.execute(new Browser.AdjustChoices(var4, FileSystemModel.ROOT, (String)null));
               }

               for(int var11 = 1; var11 < this.navigator.getStack().size() - 1; var11 += 2) {
                  try {
                     Object var5 = this.navigator.getStack().get(var11);
                     Object var6 = this.navigator.getStack().get(var11 + 1);
                     String var7 = ((BreadcrumbItem)BreadcrumbItem.class.cast(var5)).getValue()[1];
                     Long var8 = Long.parseLong(var7);
                     if (var8.equals(var2)) {
                        if (var6 instanceof BreadcrumbItemChoices) {
                           exec.execute(new Browser.AddChoice((BreadcrumbItemChoices)var6, var1));
                        }
                        break;
                     }
                  } catch (ArrayIndexOutOfBoundsException var9) {
                  } catch (Exception var10) {
                     var10.printStackTrace();
                  }
               }

            }
         }
      }
   }

   public void breadcrumbBarEvent(BreadcrumbBarEvent var1) {
      BreadcrumbItem[] var2 = (BreadcrumbItem[])((BreadcrumbItem[])var1.getNewValue());
      if (var2.length != 0) {
         BreadcrumbItem var3 = var2[var2.length - 1];
         Integer var4 = Integer.parseInt(var3.getValue()[1]);
         if (this.directoryModel != null) {
            this.directoryModel.setParent((long)var4, makePath(var3.getValue()[2], var3.getValue()[0]), (FileObject)null);
         }

      }
   }

   public List<String> getAdditionalOptions() {
      ArrayList var1 = new ArrayList();
      var1.add("/FileBrowser/BrowseContextMenu.xml");
      return var1;
   }

   @Override
   public TabbableStatus getStatus() {
      return this.model.getTabbableStatus();
   }

   private class AddChoice implements Runnable {
      BreadcrumbItemChoices choices;
      FileObject fo;

      public AddChoice(BreadcrumbItemChoices var2, FileObject var3) {
         this.choices = var2;
         this.fo = var3;
      }

      public void run() {
         if (this.choices.getChoices().length <= 100) {
            BreadcrumbItem[] var1 = this.choices.getChoices();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               BreadcrumbItem var4 = var1[var3];
               if (var4.getValue()[1].equals(this.fo.getId().toString())) {
                  var4.setIcon(FileManagerHost.getIcon(this.fo.getIcon()));
                  var4.setName(this.fo.getName());
                  return;
               }
            }

            BreadcrumbItem var5 = Browser.this.makeBreadcrumb(this.fo);
            this.choices.addItem(var5);
         }
      }
   }

   private class AdjustChoices implements Runnable {
      BreadcrumbItemChoices choices;
      long parent;
      String path;

      public AdjustChoices(BreadcrumbItemChoices var2, long var3, String var5) {
         this.choices = var2;
         this.parent = var3;
         this.path = var5;
      }

      public void run() {
         if (this.choices.getChoices().length <= 100) {
            List var1 = Browser.this.model.getSubdirectories(this.parent, this.path);
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               FileObject var3 = (FileObject)var2.next();
               if (this.choices.getChoices().length > 100) {
                  return;
               }

               if (var3.isDirectory()) {
                  BreadcrumbItem var4 = Browser.this.makeBreadcrumb(var3);
                  this.choices.addItem(var4);
               }
            }

         }
      }
   }

   private class SetNavigatorChoices implements Runnable {
      int index;
      BreadcrumbItemChoices choices;
      boolean updateUI;

      public SetNavigatorChoices(int var2, BreadcrumbItemChoices var3, boolean var4) {
         this.index = var2;
         this.choices = var3;
         this.updateUI = var4;
      }

      public void run() {
         Browser.this.navigator.setChoices(this.index, this.choices, this.updateUI);
      }
   }
}
