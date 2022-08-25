package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.plugin.netmapviewer.data.Arp;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ArpDisplay extends JPanel {
   ColumnHidingModel hider = new ColumnHidingModel(ArpDisplay.ArpColumns.class);
   private JTable arpTable;
   private JScrollPane jScrollPane1;

   public ArpDisplay(Collection<Arp> var1) {
      this.initComponents();
      this.arpTable.setColumnModel(this.hider);
      ArpDisplay.ArpTableModel var2 = new ArpDisplay.ArpTableModel(var1);
      this.arpTable.setModel(var2);
      this.hider.applyToTable(this.arpTable);
      this.arpTable.setDefaultRenderer(Calendar.class, new CalendarRenderer());
      this.arpTable.setRowSorter(new TableRowSorter(var2));
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.arpTable = new JTable();
      this.arpTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.jScrollPane1.setViewportView(this.arpTable);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 394, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 379, 32767));
   }

   public class ArpTableModel extends AbstractEnumeratedTableModel<ArpDisplay.ArpColumns> {
      List<Arp> arps = new ArrayList();

      public ArpTableModel(Collection<Arp> var2) {
         super(ArpDisplay.ArpColumns.class);
         this.arps.addAll(var2);
         Collections.sort(this.arps, new Comparator<Arp>() {
            public int compare(Arp var1, Arp var2) {
               return (int)(var1.getLpTimestamp() - var2.getLpTimestamp());
            }
         });
      }

      public Object getValueAt(int i, ArpDisplay.ArpColumns e) {
         try {
            if (i < this.arps.size() && i >= 0) {
               switch(e) {
               case NetAddress:
                  return ((Arp)this.arps.get(i)).getInetAddress();
               case State:
                  return ((Arp)this.arps.get(i)).getState();
               case PhysicalAddress:
                  return ((Arp)this.arps.get(i)).getPhysAddress();
               case Interface:
                  return ((Arp)this.arps.get(i)).getInterface();
               case Timestamp:
                  Calendar var3 = Calendar.getInstance();
                  var3.setTimeInMillis(((Arp)this.arps.get(i)).getLpTimestamp());
                  return var3;
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return null;
      }

      public int getRowCount() {
         return this.arps.size();
      }

      @Override
      public String getColumnName(ArpDisplay.ArpColumns e) {
         return e.getName();
      }

      @Override
      public Class<?> getColumnClass(ArpDisplay.ArpColumns e) {
         return e.getClazz();
      }
   }

   public static enum ArpColumns {
      NetAddress(String.class, "Internet Address"),
      State(String.class, "State/Type"),
      PhysicalAddress(String.class, "Physical Address"),
      Interface(String.class, "Interface"),
      Timestamp(Calendar.class, "Timestamp");

      Class<?> clazz;
      String name;

      private ArpColumns(Class<?> var3, String var4) {
         this.clazz = var3;
         this.name = var4;
      }

      public Class<?> getClazz() {
         return this.clazz;
      }

      public String getName() {
         return this.name;
      }
   }
}
