package ddb.dsz.plugin.netmapviewer.display;

import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class IfConfigInterfaceDisplay extends JPanel {
   private JTextField addressField;
   private JTextField descriptionField;
   private JTextField dhcpEnabledField;
   private JTextField dhcpServerField;
   private JList ipAddressList;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JTextField leaseExpiresField;
   private JTextField leaseObtainedField;

   public IfConfigInterfaceDisplay(IfConfig.Interface var1) {
      this.initComponents();
      this.addressField.setText(var1.getPhysicalAddress());
      this.descriptionField.setText(var1.getDescription());
      if (var1.isDhcpEnabled()) {
         this.dhcpEnabledField.setText("Enabled");
      } else {
         this.dhcpEnabledField.setText("Disabled");
      }

      this.dhcpServerField.setText(var1.getDhcpServer());
      DefaultListModel var2 = new DefaultListModel();
      this.ipAddressList.setModel(var2);
      Iterator var3 = var1.getIpAddresses().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.addElement(var4);
      }

      SimpleDateFormat var5 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
      this.leaseExpiresField.setText(var5.format(var1.getLeaseExpires().getTime()));
      this.leaseObtainedField.setText(var5.format(var1.getLeaseObtained().getTime()));
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.ipAddressList = new JList();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.jLabel5 = new JLabel();
      this.jLabel6 = new JLabel();
      this.leaseExpiresField = new JTextField();
      this.leaseObtainedField = new JTextField();
      this.dhcpServerField = new JTextField();
      this.addressField = new JTextField();
      this.descriptionField = new JTextField();
      this.jLabel7 = new JLabel();
      this.dhcpEnabledField = new JTextField();
      this.setBorder(BorderFactory.createTitledBorder("Interface"));
      this.jLabel1.setText("Description:");
      this.ipAddressList.setModel(new AbstractListModel() {
         String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

         public int getSize() {
            return this.strings.length;
         }

         public Object getElementAt(int var1) {
            return this.strings[var1];
         }
      });
      this.ipAddressList.setVisibleRowCount(4);
      this.jLabel2.setText("Phsyical Address:");
      this.jLabel3.setText("DHCP Server:");
      this.jLabel4.setText("Lease Obtained:");
      this.jLabel5.setText("Lease Expires:");
      this.jLabel6.setText("Ip Addresses:");
      this.leaseExpiresField.setEditable(false);
      this.leaseExpiresField.setText("jTextField1");
      this.leaseObtainedField.setEditable(false);
      this.leaseObtainedField.setText("jTextField2");
      this.dhcpServerField.setEditable(false);
      this.dhcpServerField.setText("jTextField3");
      this.addressField.setEditable(false);
      this.addressField.setText("jTextField4");
      this.descriptionField.setEditable(false);
      this.descriptionField.setText("jTextField5");
      this.jLabel7.setText("DHCP Enabled:");
      this.dhcpEnabledField.setEditable(false);
      this.dhcpEnabledField.setText("jTextField6");
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel1).addComponent(this.jLabel5).addComponent(this.jLabel6).addComponent(this.jLabel3).addComponent(this.jLabel4).addComponent(this.jLabel2).addComponent(this.jLabel7)).addGap(13, 13, 13).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.ipAddressList, -1, 200, 32767).addComponent(this.descriptionField, -1, 200, 32767).addComponent(this.addressField, -1, 200, 32767).addComponent(this.dhcpServerField, -1, 200, 32767).addComponent(this.leaseObtainedField, -1, 200, 32767).addComponent(this.leaseExpiresField, -1, 200, 32767).addComponent(this.dhcpEnabledField, -1, 200, 32767)).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.descriptionField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.addressField, -2, -1, -2)).addGap(7, 7, 7).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel7).addComponent(this.dhcpEnabledField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.dhcpServerField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.leaseObtainedField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.leaseExpiresField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.ipAddressList, -1, 88, 32767)).addContainerGap()));
   }
}
