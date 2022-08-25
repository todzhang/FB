package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.netmapviewer.Node;
import ddb.dsz.plugin.netmapviewer.data.Traceroute;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.util.AbstractEnumeratedTableModel;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class TracerouteDisplay extends JPanel {
   CoreController core;
   TracerouteDisplay.TracerouteTableModel modelofTraceroute;
   DefaultListModel listOfTraceroutes;
   Node node;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSplitPane jSplitPane1;
   private JTable traceRouteDetails;
   private JList traceRouteList;

   public TracerouteDisplay(CoreController var1, Node var2) {
      this.core = var1;
      this.initComponents();
      this.node = var2;
      List var3 = this.node.getTraceroutes();
      this.modelofTraceroute = new TracerouteDisplay.TracerouteTableModel();
      this.listOfTraceroutes = new DefaultListModel();
      ArrayList var4 = new ArrayList(var3);
      Collections.sort(var4, new Comparator<Traceroute>() {
         public int compare(Traceroute var1, Traceroute var2) {
            long var3 = var1.getLpTimestamp() - var2.getLpTimestamp();
            if (var3 < 0L) {
               return -1;
            } else {
               return var3 == 0L ? 0 : 1;
            }
         }
      });
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Traceroute var6 = (Traceroute)var5.next();
         this.listOfTraceroutes.addElement(var6);
      }

      this.traceRouteList.setModel(this.listOfTraceroutes);
      this.traceRouteDetails.setModel(this.modelofTraceroute);
      if (var4.size() > 0) {
         this.traceRouteList.setSelectedValue(var4.get(0), true);
      }

      this.traceRouteDetails.setDefaultRenderer(String.class, new DszTableCellRenderer() {
         Font BOLD;
         Font PLAIN;

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (this.BOLD == null) {
               this.BOLD = super.getFont().deriveFont(1);
            }

            if (this.PLAIN == null) {
               this.PLAIN = super.getFont().deriveFont(0);
            }

            Font var7 = this.PLAIN;
            if (value instanceof String && (TracerouteDisplay.this.node.doesAddressMatch(value.toString()) || TracerouteDisplay.this.node.doesNameMatch(value.toString()))) {
               var7 = this.BOLD;
            }

            Component var8 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            var8.setFont(var7);
            return var8;
         }
      });
      this.traceRouteDetails.setDefaultRenderer(Integer.class, new DszTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, String.format("%d", value), isSelected, hasFocus, row, column);
         }
      });
      this.traceRouteDetails.setDefaultRenderer(Long.class, new DszTableCellRenderer() {
         {
            super.setHorizontalTextPosition(0);
         }

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
               Long var7 = (Long) value;
               StringBuilder var12 = new StringBuilder();
               if (var7 >= 60000L) {
                  var12.append(var7 / 60000L);
                  var12.append(":");
                  var7 = var7 % 60000L;
               }

               if (var7 < 1000L && var12.length() <= 0) {
                  var12.append("0.");
               } else {
                  var12.append(String.format("%02d.", var7 / 1000L));
                  var7 = var7 % 1000L;
               }

               var12.append(String.format("%03d s", var7));
               return super.getTableCellRendererComponent(table, var12.toString(), isSelected, hasFocus, row, column);
            } else {
               return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
         }
      });
      this.traceRouteList.setCellRenderer(new DefaultListCellRenderer() {
         Calendar cal = Calendar.getInstance();

         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            if (var2 instanceof Traceroute) {
               Calendar var6 = ((Traceroute)Traceroute.class.cast(var2)).getLpTimestampAsCalendar();
               var2 = String.format("%04d-%02d-%02d   %02d:%02d:%02d", var6.get(1), var6.get(2) + 1, var6.get(5), var6.get(11), var6.get(12), var6.get(13));
            }

            return super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         }
      });
   }

   private void initComponents() {
      this.jSplitPane1 = new JSplitPane();
      this.jScrollPane1 = new JScrollPane();
      this.traceRouteList = new JList();
      this.jScrollPane2 = new JScrollPane();
      this.traceRouteDetails = new JTable();
      this.jSplitPane1.setDividerLocation(150);
      this.jSplitPane1.setOrientation(0);
      this.traceRouteList.setModel(new AbstractListModel() {
         String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

         public int getSize() {
            return this.strings.length;
         }

         public Object getElementAt(int var1) {
            return this.strings[var1];
         }
      });
      this.traceRouteList.setSelectionMode(0);
      this.traceRouteList.setLayoutOrientation(1);
      this.traceRouteList.setPrototypeCellValue("1970-01-01   12:00:00 GMT");
      this.traceRouteList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            TracerouteDisplay.this.traceRouteListValueChanged(var1);
         }
      });
      this.jScrollPane1.setViewportView(this.traceRouteList);
      this.jSplitPane1.setLeftComponent(this.jScrollPane1);
      this.traceRouteDetails.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.jScrollPane2.setViewportView(this.traceRouteDetails);
      this.jSplitPane1.setRightComponent(this.jScrollPane2);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jSplitPane1, Alignment.TRAILING, -1, 539, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jSplitPane1, Alignment.TRAILING, -1, 408, 32767));
   }

   private void traceRouteListValueChanged(ListSelectionEvent var1) {
      if (!var1.getValueIsAdjusting()) {
         Traceroute var2 = (Traceroute)this.traceRouteList.getSelectedValue();
         this.modelofTraceroute.setTraceroute(var2);
      }
   }

   public class TracerouteTableModel extends AbstractEnumeratedTableModel<TracerouteDisplay.TracerouteColumns> {
      List<Traceroute.Hop> hops = new ArrayList();

      public TracerouteTableModel() {
         super(TracerouteDisplay.TracerouteColumns.class);
      }

      public void setTraceroute(Traceroute var1) {
         this.hops.clear();
         if (var1 != null) {
            this.hops.addAll(var1.getHops());
         }

         this.fireTableDataChanged();
      }

      public Object getValueAt(int i, TracerouteDisplay.TracerouteColumns e) {
         try {
            if (i < this.hops.size() && i >= 0) {
               switch(e) {
               case Address:
                  return ((Traceroute.Hop)this.hops.get(i)).getAddress();
               case Hop:
                  return ((Traceroute.Hop)this.hops.get(i)).getHop();
               case Time:
                  return ((Traceroute.Hop)this.hops.get(i)).getTime();
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return null;
      }

      public int getRowCount() {
         return this.hops.size();
      }

      @Override
      public String getColumnName(TracerouteDisplay.TracerouteColumns e) {
         switch(e) {
         case Address:
            return "Address";
         case Hop:
            return "Hop";
         case Time:
            return "Time (ms)";
         default:
            return super.getColumnName(e);
         }
      }

      @Override
      public Class<?> getColumnClass(TracerouteDisplay.TracerouteColumns e) {
         return e.getClazz();
      }
   }

   public static enum TracerouteColumns {
      Address(String.class),
      Hop(Integer.class),
      Time(Long.class);

      Class<?> clazz;

      private TracerouteColumns(Class<?> var3) {
         this.clazz = var3;
      }

      public Class<?> getClazz() {
         return this.clazz;
      }
   }
}
