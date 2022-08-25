package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.netmapviewer.data.Group;
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

public class GroupsDisplay extends JPanel {
   ColumnHidingModel hider = new ColumnHidingModel(GroupsDisplay.GroupsColumns.class);
   private JTable dispTable;
   private JScrollPane scroller;

   public GroupsDisplay(CoreController var1, List<Group> var2) {
      this.initComponents();
      this.dispTable.setColumnModel(this.hider);
      GroupsDisplay.GroupsTableModel var3 = new GroupsDisplay.GroupsTableModel(var2);
      this.dispTable.setModel(var3);
      this.hider.applyToTable(this.dispTable);
      this.dispTable.setRowSorter(new TableRowSorter(var3));
   }

   private void initComponents() {
      this.scroller = new JScrollPane();
      this.dispTable = new JTable();
      this.dispTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.scroller.setViewportView(this.dispTable);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(2, this.scroller, -1, 418, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(this.scroller, -1, 749, 32767));
   }

   public class GroupsTableModel extends AbstractEnumeratedTableModel<GroupsDisplay.GroupsColumns> {
      List<Group> groups = new ArrayList();

      public GroupsTableModel(List<Group> var2) {
         super(GroupsDisplay.GroupsColumns.class);
         this.groups.addAll(var2);
         Collections.sort(this.groups, new Comparator<Group>() {
            public int compare(Group var1, Group var2) {
               long var3 = var1.getLpTimestamp() - var2.getLpTimestamp();
               if (var3 < 0L) {
                  return -1;
               } else {
                  return var3 == 0L ? 0 : 1;
               }
            }
         });
      }

      public Object getValueAt(int i, GroupsDisplay.GroupsColumns e) {
         try {
            if (i < this.groups.size() && i >= 0) {
               switch(e) {
               case GroupName:
                  return ((Group)this.groups.get(i)).getGroupName();
               case GroupComment:
                  return ((Group)this.groups.get(i)).getGroupComment();
               case GroupId:
                  return ((Group)this.groups.get(i)).getGroupId();
               case GroupAttributes:
                  return ((Group)this.groups.get(i)).getGroupAttributes();
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return null;
      }

      public int getRowCount() {
         return this.groups.size();
      }

      @Override
      public String getColumnName(GroupsDisplay.GroupsColumns e) {
         switch(e) {
         case GroupName:
            return "Group Name";
         case GroupComment:
            return "Group Comment";
         case GroupId:
            return "Group Id";
         case GroupAttributes:
            return "Group Attributes";
         default:
            return super.getColumnName(e);
         }
      }

      @Override
      public Class<?> getColumnClass(GroupsDisplay.GroupsColumns e) {
         return e.getClazz();
      }
   }

   public static enum GroupsColumns {
      GroupName(String.class),
      GroupComment(String.class),
      GroupId(String.class),
      GroupAttributes(String.class);

      Class<?> clazz;

      private GroupsColumns(Class<?> var3) {
         this.clazz = var3;
      }

      public Class<?> getClazz() {
         return this.clazz;
      }
   }
}
