package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.netmapviewer.data.Ping;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

public class PingDisplay extends JPanel {
   CoreController core;
   ColumnHidingModel hider;
   private JScrollPane jScrollPane1;
   private JTable pingTable;

   public PingDisplay(CoreController var1, List<Ping> var2) {
      this.core = var1;
      this.hider = new ColumnHidingModel(PingDisplay.PingColumns.class);
      this.initComponents();
      this.pingTable.setColumnModel(this.hider);
      PingDisplay.PingTableModel var3 = new PingDisplay.PingTableModel(var2);
      this.pingTable.setModel(var3);
      this.hider.applyToTable(this.pingTable);
      this.pingTable.setDefaultRenderer(Calendar.class, new DszTableCellRenderer() {
         {
            super.setHorizontalTextPosition(0);
         }

         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
               Calendar var7 = (Calendar) value;
               return super.getTableCellRendererComponent(table, String.format("%04d-%02d-%02d   %02d:%02d:%02d", var7.get(1), var7.get(2) + 1, var7.get(5), var7.get(11), var7.get(12), var7.get(13)), isSelected, hasFocus, row, column);
            } else {
               return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
         }
      });
      this.pingTable.setDefaultRenderer(Long.class, new DszTableCellRenderer() {
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
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.pingTable = new JTable();
      this.pingTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
      this.jScrollPane1.setViewportView(this.pingTable);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, Alignment.TRAILING, -1, 400, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, Alignment.TRAILING, -1, 300, 32767));
   }

   public class PingTableModel extends AbstractEnumeratedTableModel<PingDisplay.PingColumns> {
      List<Ping> pings = new ArrayList();

      public PingTableModel(List<Ping> var2) {
         super(PingDisplay.PingColumns.class);
         this.pings.addAll(var2);
         Collections.sort(this.pings, new Comparator<Ping>() {
            public int compare(Ping var1, Ping var2) {
               long var3 = var1.getLpTimestamp() - var2.getLpTimestamp();
               if (var3 < 0L) {
                  return -1;
               } else {
                  return var3 == 0L ? 0 : 1;
               }
            }
         });
      }

      public Object getValueAt(int i, PingDisplay.PingColumns e) {
         try {
            if (i < this.pings.size() && i >= 0) {
               switch(e) {
               case Destination:
                  return ((Ping)this.pings.get(i)).getDestination();
               case Time:
                  return ((Ping)this.pings.get(i)).getElapsed();
               case Timestamp:
                  Calendar var3 = Calendar.getInstance();
                  var3.setTimeInMillis(((Ping)this.pings.get(i)).getLpTimestamp());
                  return var3;
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

         return null;
      }

      public int getRowCount() {
         return this.pings.size();
      }

      @Override
      public String getColumnName(PingDisplay.PingColumns e) {
         switch(e) {
         case Destination:
            return "Destination";
         case Time:
            return "Time (ms)";
         case Timestamp:
            return "Time";
         default:
            return super.getColumnName(e);
         }
      }

      @Override
      public Class<?> getColumnClass(PingDisplay.PingColumns e) {
         return e.getClazz();
      }
   }

   public static enum PingColumns {
      Destination(String.class),
      Time(Long.class),
      Timestamp(Calendar.class);

      Class<?> clazz;

      private PingColumns(Class<?> var3) {
         this.clazz = var3;
      }

      public Class<?> getClazz() {
         return this.clazz;
      }
   }
}
