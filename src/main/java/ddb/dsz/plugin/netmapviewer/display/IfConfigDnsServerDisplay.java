package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class IfConfigDnsServerDisplay extends JPanel {
   private JTextField broadcastField;
   private JTextField ipField;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JTextField maskField;

   public IfConfigDnsServerDisplay(IfConfig.DnsServer var1) {
      this.initComponents();
      this.broadcastField.setText(var1.getBroadcast());
      this.ipField.setText(var1.getIp());
      this.maskField.setText(var1.getMask());
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.broadcastField = new JTextField();
      this.maskField = new JTextField();
      this.ipField = new JTextField();
      this.setBorder(BorderFactory.createTitledBorder("Dns Server"));
      this.jLabel1.setText("Broadcast:");
      this.jLabel2.setText("Mask:");
      this.jLabel3.setText("Ip:");
      this.broadcastField.setEditable(false);
      this.maskField.setEditable(false);
      this.ipField.setEditable(false);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel1).addComponent(this.jLabel2).addComponent(this.jLabel3)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.broadcastField, -1, 140, 32767).addComponent(this.maskField, -1, 140, 32767).addComponent(this.ipField, -1, 140, 32767)).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.broadcastField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.maskField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.ipField, -2, -1, -2))));
   }
}
