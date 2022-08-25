package ddb.dsz.plugin.notify;

import ddb.dsz.core.task.Task;
import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NotifyDisplay extends JPanel {
   private JLabel commandField;
   private JLabel durationField;
   private JLabel hostField;
   private JLabel idField;
   private JButton jButton1;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JPanel jPanel1;
   private JLabel stateField;

   public NotifyDisplay(Task task) {
      this.initComponents();
      switch(task.getState()) {
      case FAILED:
         this.stateField.setText("FAILURE");
         this.stateField.setForeground(Color.RED);
         break;
      case KILLED:
         this.stateField.setText("STOPPED");
         this.stateField.setForeground(Color.RED);
         break;
      case SUCCEEDED:
         this.stateField.setText("SUCCESS");
         this.stateField.setForeground(Color.GREEN);
         break;
      default:
         this.stateField.setText("OTHER");
         this.stateField.setForeground(Color.YELLOW);
         return;
      }

      this.idField.setText(String.format("%d", task.getId().getId()));
      this.hostField.setText(task.getTargetId());
      this.commandField.setText(task.getTypedCommand());
      Calendar var2 = Calendar.getInstance(task.getCreated().getTimeZone());
      long var3 = var2.getTimeInMillis() - task.getCreated().getTimeInMillis();
      long var5 = var3 % 1000L;
      var3 /= 1000L;
      if (var5 > 0L) {
         ++var3;
      }

      long var7 = var3 % 60L;
      var3 /= 60L;
      long var9 = var3 % 60L;
      var3 /= 60L;
      long var11 = var3 % 24L;
      var3 /= 24L;
      String var15 = "";
      if (var3 > 0L) {
         var15 = String.format("%d days, ");
      }

      this.durationField.setText(String.format("%s%02d:%02d:%02d", var15, var11, var9, var7, var5));
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.idField = new JLabel();
      this.jLabel5 = new JLabel();
      this.hostField = new JLabel();
      this.commandField = new JLabel();
      this.stateField = new JLabel();
      this.durationField = new JLabel();
      this.jPanel1 = new JPanel();
      this.jButton1 = new JButton();
      this.jLabel1.setText("Id:");
      this.jLabel2.setText("Command:");
      this.jLabel3.setText("State:");
      this.jLabel4.setText("Duration:");
      this.idField.setFont(new Font("Tahoma", 1, 11));
      this.idField.setText("jLabel5");
      this.jLabel5.setText("Target:");
      this.hostField.setFont(new Font("Tahoma", 1, 11));
      this.hostField.setText("jLabel6");
      this.commandField.setFont(new Font("Tahoma", 1, 11));
      this.commandField.setText("jLabel6");
      this.stateField.setFont(new Font("Tahoma", 1, 11));
      this.stateField.setText("jLabel6");
      this.durationField.setFont(new Font("Tahoma", 1, 11));
      this.durationField.setText("jLabel6");
      GroupLayout var1 = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGap(0, 100, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGap(0, 125, 32767));
      this.jButton1.setText("Close");
      GroupLayout var2 = new GroupLayout(this);
      this.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.idField, -1, 44, 32767).addGap(18, 18, 18).addComponent(this.jLabel5).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.hostField, -1, 44, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.commandField, -1, 111, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel3).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.stateField, -1, 132, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel4).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.durationField, -1, 117, 32767)).addComponent(this.jButton1)).addContainerGap()));
      var2.setVerticalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.idField).addComponent(this.jLabel5).addComponent(this.hostField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.commandField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.stateField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.durationField)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton1).addContainerGap(-1, 32767)).addComponent(this.jPanel1, -1, -1, 32767));
   }
}
