package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class IfConfigDisplay extends JPanel {
   private JPanel dnsPanel;
   private JTextField domainSuffixField;
   private JTextField hostNameField;
   private JPanel interfacePanel;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator1;
   private JSeparator jSeparator2;

   public IfConfigDisplay(IfConfig var1) {
      this.initComponents();
      this.hostNameField.setText(var1.getHostName());
      this.domainSuffixField.setText(var1.getDomainName());
      Iterator var2 = var1.getDnsServers().iterator();

      while(var2.hasNext()) {
         IfConfig.DnsServer var3 = (IfConfig.DnsServer)var2.next();
         this.dnsPanel.add(new IfConfigDnsServerDisplay(var3));
      }

      var2 = var1.getInterfaces().iterator();

      while(var2.hasNext()) {
         IfConfig.Interface var4 = (IfConfig.Interface)var2.next();
         this.interfacePanel.add(new IfConfigInterfaceDisplay(var4));
      }

   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.jLabel1 = new JLabel();
      this.hostNameField = new JTextField();
      this.jLabel2 = new JLabel();
      this.domainSuffixField = new JTextField();
      this.jSeparator1 = new JSeparator();
      this.dnsPanel = new JPanel();
      this.jSeparator2 = new JSeparator();
      this.interfacePanel = new JPanel();
      this.jLabel1.setText("Host Name:");
      this.hostNameField.setEditable(false);
      this.jLabel2.setText("Primary Domain Suffix:");
      this.domainSuffixField.setEditable(false);
      this.dnsPanel.setLayout(new BoxLayout(this.dnsPanel, 1));
      this.interfacePanel.setLayout(new BoxLayout(this.interfacePanel, 1));
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jSeparator1, -1, 287, 32767).addGroup(var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel1).addComponent(this.jLabel2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.hostNameField, -1, 174, 32767).addComponent(this.domainSuffixField, -1, 174, 32767))))).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.dnsPanel, -1, 287, 32767)).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.jSeparator2, -1, 287, 32767)).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.interfacePanel, -1, 287, 32767))).addContainerGap()).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGap(0, 295, 32767).addComponent(this.jScrollPane1, -2, -1, -2).addGap(0, 10, 32767))));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.hostNameField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.domainSuffixField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jSeparator1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.dnsPanel, -1, 21, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jSeparator2, -2, 5, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.interfacePanel, -1, 26, 32767)).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGap(0, 77, 32767).addComponent(this.jScrollPane1, -2, -1, -2).addGap(0, 45, 32767))));
   }
}
