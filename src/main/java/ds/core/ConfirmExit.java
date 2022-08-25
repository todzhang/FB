package ds.core;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ConfirmExit extends JDialog {
   int count = 5;
   ScheduledExecutorService exec;
   String exitText = "Exit Operation";
   boolean m_quit = false;
   private JButton exit;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JButton resume;

   public ConfirmExit(Frame owner, boolean model, ScheduledExecutorService exec) {
      super(owner, model);
      this.exec = exec;
      this.initComponents();
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.resume = new JButton();
      this.exit = new JButton();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.setDefaultCloseOperation(2);
      this.setTitle("Confirm Exit");
      this.setAlwaysOnTop(true);
      this.setModal(true);
      this.setResizable(false);
      this.jLabel1.setText("You are still connected to at least one target.");
      this.jLabel2.setText("Are you certain you wish to exit?");
      this.resume.setText("Resume Operation");
      this.resume.addActionListener(actionEvent -> ConfirmExit.this.resumeActionPerformed(actionEvent));
      this.exit.setText("Exit Operation (5 seconds)");
      this.exit.setEnabled(false);
      this.exit.addActionListener(actionEvent -> ConfirmExit.this.exitActionPerformed(actionEvent));
      this.jLabel3.setText(" ");
      this.jLabel4.setText(" ");
      GroupLayout var1 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.jLabel4, -1, -1, 32767).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addComponent(this.jLabel1, -1, 295, 32767).addGap(14, 14, 14)).addGroup(var1.createSequentialGroup().addComponent(this.jLabel2, -1, 235, 32767).addGap(74, 74, 74)).addGroup(var1.createSequentialGroup().addComponent(this.resume).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.exit, -2, 163, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel3, -1, -1, 32767).addGap(10, 10, 10))).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel2).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.resume).addComponent(this.exit).addComponent(this.jLabel4).addComponent(this.jLabel3)).addContainerGap(-1, 32767)));
      this.pack();
   }

   private void exitActionPerformed(ActionEvent var1) {
      this.m_quit = true;
      this.setVisible(false);
      this.dispose();
   }

   private void resumeActionPerformed(ActionEvent var1) {
      this.setVisible(false);
      this.dispose();
   }

   public static void main(String[] var0) {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            ConfirmExit var1 = new ConfirmExit(new JFrame(), true, new ScheduledThreadPoolExecutor(1));
            var1.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent var1) {
                  System.exit(0);
               }
            });
            var1.setVisible(true);
         }
      });
   }

   public boolean query() {
      this.exit.setText(String.format("(%d seconds)", this.count));
      this.exec.schedule(new Runnable() {
         @Override
         public void run() {
            if (!EventQueue.isDispatchThread()) {
               EventQueue.invokeLater(this);
            } else {
               --ConfirmExit.this.count;
               if (ConfirmExit.this.count == 0) {
                  ConfirmExit.this.exit.setText(ConfirmExit.this.exitText);
                  ConfirmExit.this.exit.setEnabled(true);
               } else {
                  ConfirmExit.this.exit.setText(String.format("(%d seconds)", ConfirmExit.this.count));
                  ConfirmExit.this.exec.schedule(this, 1L, TimeUnit.SECONDS);
               }
            }

         }
      }, 1L, TimeUnit.SECONDS);
      Point var1 = this.getParent().getLocation();
      var1.x += (this.getParent().getWidth() - this.getWidth()) / 2;
      var1.y += (this.getParent().getHeight() - this.getHeight()) / 2;
      this.setLocation(var1);
      this.setVisible(true);
      return this.m_quit;
   }
}
