package ddb.dsz.plugin.requesthandler;

import ddb.dsz.plugin.requesthandler.model.RequestStatus;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.CommandTransformer;
import ddb.dsz.plugin.requesthandler.tranformers.DisplayTransformer;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class RequestInformation extends JDialog {
   RequestHandler handler;
   RequestedOperation reqOp;
   private JButton approveButton;
   private JButton closeButton;
   private JTextArea commandLineField;
   private JPanel commandLinePanel;
   private JButton denyButton;
   private JTextArea descriptionField;
   private JPanel descriptionPanel;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JTextField sourceField;
   private JLabel sourceLabel;

   public RequestInformation(RequestedOperation var1, RequestHandler var2, boolean var3, RequestStatus var4) {
      super((Frame)null, true);
      this.handler = var2;
      this.reqOp = var1;
      this.initComponents();
      if (!var3) {
         this.approveButton.setVisible(false);
         this.denyButton.setText("Cancel");
         this.denyButton.setToolTipText("Cancel this operation");
      }

      try {
         this.sourceField.setText(var1.getSource());
         this.descriptionField.setText(DisplayTransformer.getInstance().transform(var1).toString());
         this.commandLineField.setText(CommandTransformer.getInstance().transform(var1).toString());
      } catch (Exception var6) {
      }

      switch(var4) {
      case ALLOWED:
         this.approveButton.setVisible(false);
         break;
      case CANCELLED:
      case EXECUTED:
      case DENIED:
         this.approveButton.setVisible(false);
         this.denyButton.setVisible(false);
      }

   }

   private void initComponents() {
      this.sourceLabel = new JLabel();
      this.sourceField = new JTextField();
      this.descriptionPanel = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.descriptionField = new JTextArea();
      this.commandLinePanel = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.commandLineField = new JTextArea();
      this.closeButton = new JButton();
      this.denyButton = new JButton();
      this.approveButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Request Information");
      this.sourceLabel.setText("Source:");
      this.sourceField.setEditable(false);
      this.descriptionPanel.setBorder(BorderFactory.createTitledBorder("Description"));
      this.descriptionField.setColumns(20);
      this.descriptionField.setEditable(false);
      this.descriptionField.setRows(1);
      this.jScrollPane1.setViewportView(this.descriptionField);
      GroupLayout var1 = new GroupLayout(this.descriptionPanel);
      this.descriptionPanel.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(this.jScrollPane1, -1, 542, 32767).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.jScrollPane1).addContainerGap()));
      this.commandLinePanel.setBorder(BorderFactory.createTitledBorder("Expected Command Line"));
      this.commandLineField.setColumns(20);
      this.commandLineField.setEditable(false);
      this.commandLineField.setRows(1);
      this.jScrollPane2.setViewportView(this.commandLineField);
      GroupLayout var2 = new GroupLayout(this.commandLinePanel);
      this.commandLinePanel.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().addContainerGap().add(this.jScrollPane2, -1, 542, 32767).addContainerGap()));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(this.jScrollPane2).addContainerGap()));
      this.closeButton.setText("Close");
      this.closeButton.setToolTipText("Close without changing the state");
      this.closeButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            RequestInformation.this.closeButtonActionPerformed(var1);
         }
      });
      this.denyButton.setText("Deny");
      this.denyButton.setToolTipText("Deny this operation");
      this.denyButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            RequestInformation.this.denyButtonActionPerformed(var1);
         }
      });
      this.approveButton.setText("Approve");
      this.approveButton.setToolTipText("Approve this operation");
      this.approveButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            RequestInformation.this.approveButtonActionPerformed(var1);
         }
      });
      GroupLayout var3 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().addContainerGap().add(var3.createParallelGroup(1).add(var3.createSequentialGroup().add(this.sourceLabel).addPreferredGap(0).add(this.sourceField, -1, 533, 32767)).add(this.descriptionPanel, -1, -1, 32767).add(this.commandLinePanel, -1, -1, 32767).add(2, var3.createSequentialGroup().add(this.approveButton).addPreferredGap(0).add(this.denyButton).addPreferredGap(0).add(this.closeButton))).addContainerGap()));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().addContainerGap().add(var3.createParallelGroup(1).add(this.sourceLabel).add(this.sourceField, -2, -1, -2)).addPreferredGap(0).add(this.descriptionPanel, -1, -1, 32767).addPreferredGap(0).add(this.commandLinePanel, -1, -1, 32767).addPreferredGap(0).add(var3.createParallelGroup(3).add(this.closeButton).add(this.denyButton).add(this.approveButton)).addContainerGap()));
      this.pack();
   }

   private void approveButtonActionPerformed(ActionEvent var1) {
      if (this.handler != null) {
         this.handler.approve(this.reqOp.getId());
      }

      this.setVisible(false);
      this.dispose();
   }

   private void denyButtonActionPerformed(ActionEvent var1) {
      if (this.handler != null) {
         this.handler.cancel(this.reqOp.getId());
      }

      this.setVisible(false);
      this.dispose();
   }

   private void closeButtonActionPerformed(ActionEvent var1) {
      this.setVisible(false);
      this.dispose();
   }
}
