package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.netmapviewer.Resource;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.layout.GroupLayout;

public class ShareDisplay extends JPanel {
   ColumnHidingModel hider = new ColumnHidingModel(ShareDisplay.SharesColumns.class);
   private JScrollPane scroller;
   private JTable sharesTable;

   public ShareDisplay(CoreController var1, List<Resource> var2) {
      this.initComponents();
      this.sharesTable.setColumnModel(this.hider);
      ShareDisplay.SharesTableModel var3 = new ShareDisplay.SharesTableModel(var2);
      this.sharesTable.setModel(var3);
      this.hider.applyToTable(this.sharesTable);
      this.sharesTable.setRowSorter(new TableRowSorter(var3));
   }

   private void initComponents() {
      this.scroller = new JScrollPane();
      this.sharesTable = new JTable();
      this.sharesTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.scroller.setViewportView(this.sharesTable);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(this.scroller, -1, 410, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(this.scroller, -1, 399, 32767));
   }

   public class SharesTableModel extends AbstractEnumeratedTableModel<ShareDisplay.SharesColumns> {
      List<Resource> shares = new ArrayList();

      public SharesTableModel(List<Resource> var2) {
         super(ShareDisplay.SharesColumns.class);
         this.shares.addAll(var2);
         Collections.sort(this.shares, new Comparator<Resource>() {
            public int compare(Resource var1, Resource var2) {
               return var1.getName().compareTo(var2.getName());
            }
         });
      }

      public Object getValueAt(int i, ShareDisplay.SharesColumns e) {
         try {
            if (i < this.shares.size() && i >= 0) {
               switch(e) {
               case Name:
                  return ((Resource)this.shares.get(i)).getName();
               case Path:
                  return ((Resource)this.shares.get(i)).getPath();
               case Type:
                  return ((Resource)this.shares.get(i)).getNodeType();
               case Description:
                  return ((Resource)this.shares.get(i)).getDescription();
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return null;
      }

      public int getRowCount() {
         return this.shares.size();
      }

      @Override
      public String getColumnName(ShareDisplay.SharesColumns e) {
         switch(e) {
         case Name:
            return "Name";
         case Path:
            return "Path";
         case Type:
            return "Type";
         case Description:
            return "Description";
         default:
            return super.getColumnName(e);
         }
      }

      @Override
      public Class<?> getColumnClass(ShareDisplay.SharesColumns e) {
         return e.getClazz();
      }
   }

   public static enum SharesColumns {
      Name(String.class),
      Path(String.class),
      Type(Resource.TYPE.class),
      Description(String.class);

      Class<?> clazz;

      private SharesColumns(Class<?> var3) {
         this.clazz = var3;
      }

      public Class<?> getClazz() {
         return this.clazz;
      }
   }
}
