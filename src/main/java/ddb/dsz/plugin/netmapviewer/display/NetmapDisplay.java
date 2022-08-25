package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.netmapviewer.data.Netmap;
import java.awt.EventQueue;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NetmapDisplay extends JPanel {
   Netmap current = null;
   CoreController core;
   private JTextArea addressField;
   private JTextField commentField;
   private JLabel estimatedTimeField;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JTextField localNameField;
   private JTextField nameField;
   private JTextField osField;
   private JTextField providerField;
   private JLabel reportedTimeField;
   private JTextArea softwareField;

   public NetmapDisplay(CoreController var1, Netmap var2) {
      this.core = var1;
      this.initComponents();
      this.setNode(var2);
   }

   public void setNode(Netmap var1) {
      this.current = var1;
      this.core.submit(new NetmapDisplay.UpdateTime(var1));
      this.nameField.setText(var1 != null ? var1.getName() : "");
      this.localNameField.setText(var1 != null ? var1.getLocalName() : "");
      this.commentField.setText(var1 != null ? var1.getComment() : "");
      this.providerField.setText(var1 != null ? var1.getProvider() : "");
      this.reportedTimeField.setText(var1 != null ? this.calcTimeZone(var1.getTimeZone()) : "");
      this.addressField.setText("");
      this.softwareField.setText("");
      if (var1 != null) {
         this.reportedTimeField.setText(this.formatCalendar(var1.getReportedTime()));
      } else {
         this.reportedTimeField.setText("");
      }

      if (var1 != null) {
         Iterator var2 = var1.getAddresses().iterator();

         String var3;
         while(var2.hasNext()) {
            var3 = (String)var2.next();
            this.addressField.append(var3 + "\n");
         }

         var2 = var1.getSoftware().iterator();

         while(var2.hasNext()) {
            var3 = (String)var2.next();
            this.softwareField.append(var3 + "\n");
         }
      }

      String var4 = "Unknown";
      if (var1 != null && var1.getOsPlatform() != null && var1.getOsPlatform().length() != 0) {
         var4 = String.format("%s %d.%d", var1.getOsPlatform(), var1.getMajorVersion(), var1.getMinorVersion());
      }

      this.osField.setText(var4);
   }

   private String calcTimeZone(long var1) {
      boolean var3 = false;
      if (var1 < 0L) {
         var3 = true;
         var1 = Math.abs(var1);
      }

      var1 /= 1000L;
      var1 /= 60L;
      long var4 = var1 % 60L;
      long var6 = var1 / 60L;
      return (var3 ? "-" : "") + String.format("%02d:%02d", var6, var4);
   }

   private String formatCalendar(long var1) {
      Calendar var3 = Calendar.getInstance();
      var3.setTimeInMillis(var1);
      return this.formatCalendar(var3);
   }

   private String formatCalendar(Calendar var1) {
      return var1 == null ? "Unknown" : String.format("%04d-%02d-%02d %02d:%02d:%02d", var1.get(1), var1.get(2) + 1, var1.get(5), var1.get(11), var1.get(12), var1.get(13));
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.nameField = new JTextField();
      this.jLabel2 = new JLabel();
      this.estimatedTimeField = new JLabel();
      this.jLabel3 = new JLabel();
      this.commentField = new JTextField();
      this.jLabel4 = new JLabel();
      this.providerField = new JTextField();
      this.jLabel5 = new JLabel();
      this.localNameField = new JTextField();
      this.jLabel6 = new JLabel();
      this.reportedTimeField = new JLabel();
      this.jPanel1 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.addressField = new JTextArea();
      this.jPanel2 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.softwareField = new JTextArea();
      this.jLabel7 = new JLabel();
      this.osField = new JTextField();
      this.jLabel1.setText("Name:");
      this.nameField.setEditable(false);
      this.jLabel2.setText("Estimated Time:");
      this.estimatedTimeField.setText("Unknown");
      this.jLabel3.setText("Comment:");
      this.commentField.setEditable(false);
      this.jLabel4.setText("Provider:");
      this.providerField.setEditable(false);
      this.jLabel5.setText("Local Name:");
      this.localNameField.setEditable(false);
      this.jLabel6.setText("Reported Time:");
      this.reportedTimeField.setText("Unknown");
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Addresses"));
      this.addressField.setColumns(20);
      this.addressField.setEditable(false);
      this.addressField.setRows(5);
      this.jScrollPane1.setViewportView(this.addressField);
      GroupLayout var1 = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGap(0, 169, 32767).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 169, 32767)));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGap(0, 181, 32767).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, Alignment.TRAILING, -1, 181, 32767)));
      this.jPanel2.setBorder(BorderFactory.createTitledBorder("Software"));
      this.softwareField.setColumns(20);
      this.softwareField.setEditable(false);
      this.softwareField.setRows(5);
      this.jScrollPane2.setViewportView(this.softwareField);
      GroupLayout var2 = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane2, -1, 186, 32767));
      var2.setVerticalGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane2, -1, 181, 32767));
      this.jLabel7.setText("OS:");
      this.osField.setEditable(false);
      GroupLayout var3 = new GroupLayout(this);
      this.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(Alignment.LEADING).addGroup(var3.createSequentialGroup().addContainerGap().addGroup(var3.createParallelGroup(Alignment.LEADING).addGroup(var3.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel2, -1, -1, 32767)).addGroup(var3.createSequentialGroup().addGroup(var3.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel1).addComponent(this.jLabel3).addComponent(this.jLabel4).addComponent(this.jLabel2).addComponent(this.jLabel5).addComponent(this.jLabel6).addComponent(this.jLabel7)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.LEADING).addComponent(this.nameField, -1, 303, 32767).addComponent(this.commentField, -1, 303, 32767).addComponent(this.estimatedTimeField, -1, 303, 32767).addComponent(this.providerField, -1, 303, 32767).addComponent(this.localNameField, -1, 303, 32767).addComponent(this.reportedTimeField, -1, 303, 32767).addComponent(this.osField, -1, 303, 32767)))).addContainerGap()));
      var3.setVerticalGroup(var3.createParallelGroup(Alignment.LEADING).addGroup(var3.createSequentialGroup().addContainerGap().addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.nameField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.localNameField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.commentField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.providerField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel7).addComponent(this.osField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.estimatedTimeField).addComponent(this.jLabel2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.reportedTimeField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var3.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel2, -1, -1, 32767).addComponent(this.jPanel1, -1, -1, 32767)).addContainerGap()));
   }

   private class UpdateTime implements Runnable {
      Netmap node;

      public UpdateTime(Netmap var2) {
         this.node = var2;
      }

      public void run() {
         Netmap var1 = this.node;
         if (var1 != NetmapDisplay.this.current) {
            this.node = null;
         } else {
            if (EventQueue.isDispatchThread()) {
               if (var1 != null && var1.getTimeOffset() != 0L) {
                  Calendar var2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                  var2.add(14, (int)var1.getTimeOffset());
                  NetmapDisplay.this.estimatedTimeField.setText(String.format("%s (TZ %s)", NetmapDisplay.this.formatCalendar(var2), NetmapDisplay.this.calcTimeZone(var1.getTimeZone())));
               } else {
                  NetmapDisplay.this.estimatedTimeField.setText("Unknown");
               }
            } else {
               EventQueue.invokeLater(this);
               if (var1 != null) {
                  NetmapDisplay.this.core.schedule(this, 500L, TimeUnit.MILLISECONDS);
               } else {
                  System.out.println("Stopping updates");
               }
            }

         }
      }
   }
}
