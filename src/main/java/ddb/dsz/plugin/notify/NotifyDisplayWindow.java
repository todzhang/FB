package ddb.dsz.plugin.notify;

import ddb.dsz.core.task.Task;
import ddb.imagemanager.ImageManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NotifyDisplayWindow extends JFrame {
   public static final String SUCCESS = "images/256x256/agt_action_success.png";
   public static final String FAILURE = "images/256x256/cancel.png";
   public static final String RUNNING = "images/256x256/agt_pause-queue.png";
   public static final String OTHER = "images/256x256/agt_update_critical.png";
   public static final Object LOCATION_LOCK = new Object();
   public static final Point STEP = new Point(32, 32);
   public static Point LAST_LOCATION = null;
   private JLabel commandField;
   private JLabel durationField;
   private JLabel hostField;
   private JLabel iconDisplay;
   private JLabel idField;
   private JButton jButton1;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JPanel jPanel1;
   private JLabel stateField;

   public NotifyDisplayWindow(Task task) {
      this.initComponents();
      synchronized(LOCATION_LOCK) {
         if (LAST_LOCATION == null) {
            LAST_LOCATION = new Point(0, 0);
         } else {
            Point var10000 = LAST_LOCATION;
            var10000.x += STEP.x;
            var10000 = LAST_LOCATION;
            var10000.y += STEP.y;
         }

         Dimension var3 = new Dimension(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height);
         if (this.getSize().width > var3.width || LAST_LOCATION.x + this.getSize().width > var3.width) {
            LAST_LOCATION.x = 0;
         }

         if (this.getSize().height > var3.height || LAST_LOCATION.y + this.getSize().height > var3.height) {
            LAST_LOCATION.y = 0;
         }

         this.setLocation(LAST_LOCATION.x, LAST_LOCATION.y);
      }

      switch(task.getState()) {
      case FAILED:
      case KILLED:
         this.stateField.setText("FAILURE");
         this.stateField.setForeground(Color.RED);
         this.iconDisplay.setIcon(ImageManager.getIcon("images/256x256/cancel.png", ImageManager.NOTICE_SIZE));
         break;
      case INITIALIZED:
      case PAUSED:
      case RUNNING:
      case TASKED:
         this.stateField.setText("IN PROGRESS");
         this.stateField.setForeground(Color.BLUE);
         this.iconDisplay.setIcon(ImageManager.getIcon("images/256x256/agt_pause-queue.png", ImageManager.NOTICE_SIZE));
         break;
      case SUCCEEDED:
         this.stateField.setText("SUCCESS");
         this.stateField.setForeground(Color.GREEN);
         this.iconDisplay.setIcon(ImageManager.getIcon("images/256x256/agt_action_success.png", ImageManager.NOTICE_SIZE));
         break;
      default:
         this.stateField.setText("OTHER");
         this.stateField.setForeground(Color.YELLOW);
         this.iconDisplay.setIcon(ImageManager.getIcon("images/256x256/agt_update_critical.png", ImageManager.NOTICE_SIZE));
         return;
      }

      this.idField.setText(String.format("%d", task.getId().getId()));
      this.hostField.setText(task.getTargetId());
      this.commandField.setText(task.getTypedCommand());
      Calendar var2 = Calendar.getInstance(task.getCreated().getTimeZone());
      long var17 = var2.getTimeInMillis() - task.getCreated().getTimeInMillis();
      long var5 = var17 % 1000L;
      var17 /= 1000L;
      if (var5 > 0L) {
         ++var17;
      }

      long var7 = var17 % 60L;
      var17 /= 60L;
      long var9 = var17 % 60L;
      var17 /= 60L;
      long var11 = var17 % 24L;
      var17 /= 24L;
      String var15 = "";
      if (var17 > 0L) {
         var15 = String.format("%d days, ");
      }

      this.durationField.setText(String.format("%s%02d:%02d:%02d", var15, var11, var9, var7, var5));
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.iconDisplay = new JLabel();
      this.jLabel1 = new JLabel();
      this.idField = new JLabel();
      this.jLabel5 = new JLabel();
      this.hostField = new JLabel();
      this.jLabel2 = new JLabel();
      this.commandField = new JLabel();
      this.jLabel3 = new JLabel();
      this.stateField = new JLabel();
      this.jLabel4 = new JLabel();
      this.durationField = new JLabel();
      this.jButton1 = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Command Notification");
      this.setAlwaysOnTop(true);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.iconDisplay.setHorizontalAlignment(0);
      this.iconDisplay.setMinimumSize(new Dimension(128, 128));
      GroupLayout var1 = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.iconDisplay, -1, 128, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.iconDisplay, -1, 128, 32767));
      this.jLabel1.setText("Id:");
      this.idField.setFont(new Font("Tahoma", 1, 11));
      this.idField.setText("jLabel5");
      this.jLabel5.setText("Target:");
      this.hostField.setFont(new Font("Tahoma", 1, 11));
      this.hostField.setText("jLabel6");
      this.jLabel2.setText("Command:");
      this.commandField.setFont(new Font("Tahoma", 1, 11));
      this.commandField.setText("jLabel6");
      this.jLabel3.setText("State:");
      this.stateField.setFont(new Font("Tahoma", 1, 11));
      this.stateField.setText("jLabel6");
      this.jLabel4.setText("Duration:");
      this.durationField.setFont(new Font("Tahoma", 1, 11));
      this.durationField.setText("jLabel6");
      this.jButton1.setText("Ok");
      this.jButton1.addActionListener(actionEvent -> NotifyDisplayWindow.this.jButton1ActionPerformed(actionEvent));
      GroupLayout var2 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.idField, -1, 62, 32767).addGap(18, 18, 18).addComponent(this.jLabel5).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.hostField, -1, 63, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.commandField, -1, 148, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel3).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.stateField, -1, 169, 32767)).addGroup(Alignment.LEADING, var2.createSequentialGroup().addComponent(this.jLabel4).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.durationField, -1, 154, 32767)))).addGroup(var2.createSequentialGroup().addGap(74, 74, 74).addComponent(this.jButton1))).addContainerGap()));
      var2.setVerticalGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel1, -1, -1, 32767).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.idField).addComponent(this.jLabel5).addComponent(this.hostField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.commandField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.stateField)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.durationField)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton1).addContainerGap(14, 32767)));
      this.pack();
   }

   private void jButton1ActionPerformed(ActionEvent var1) {
      this.dispose();
   }
}
