package ddb.splash;

import ddb.GuiConstants;
import ddb.imagemanager.ImageManager;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SplashScreen3 extends JFrame {
   private JLabel bannerImage;
   private JProgressBar majorProgress;
   private JProgressBar minorProgress;
   private JButton skip;
   private JLabel stageIcon;
   private JLabel state;

   public SplashScreen3(String var1) {
      this.initComponents();
      GuiConstants.locate(this, 0.5D, 0.25D);
      this.setTitle(var1);
      this.skip.setVisible(false);
   }

   public void setStageIcon(final String var1) {
      if (EventQueue.isDispatchThread()) {
         this.stageIcon.setIcon(ImageManager.getIcon(var1, ImageManager.SIZE64));
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen3.this.setStageIcon(var1);
            }
         });
      }

   }

   public void setDisplayedText(final String var1) {
      if (EventQueue.isDispatchThread()) {
         this.state.setText(var1);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen3.this.setDisplayedText(var1);
            }
         });
      }

   }

   public void showSkip(final ActionListener var1) {
      if (EventQueue.isDispatchThread()) {
         if (var1 != null) {
            this.skip.addActionListener(var1);
         }

         this.skip.setVisible(true);
         super.validate();
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen3.this.showSkip(var1);
            }
         });
      }

   }

   public void setMinorProgress(int var1, int var2, int var3) {
      this.setProgress(this.minorProgress, var1, var2, var3);
   }

   public void setMajorProgress(int var1, int var2, int var3) {
      this.setProgress(this.majorProgress, var1, var2, var3);
   }

   public void setMinorIndeterminate(boolean var1) {
      this.setIndeterminate(this.minorProgress, var1);
   }

   public void setMajorIndeterminate(boolean var1) {
      this.setIndeterminate(this.majorProgress, var1);
   }

   private void setIndeterminate(final JProgressBar var1, final boolean var2) {
      if (EventQueue.isDispatchThread()) {
         var1.setIndeterminate(var2);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen3.this.setIndeterminate(var1, var2);
            }
         });
      }

   }

   private void setProgress(final JProgressBar var1, final int var2, final int var3, final int var4) {
      if (EventQueue.isDispatchThread()) {
         try {
            var1.setMinimum(var2);
            var1.setMaximum(var3);
            var1.setValue(var4);
            var1.setIndeterminate(false);
         } catch (Exception var6) {
            System.out.printf("Bar: %s, min = %d, max = %d, value = %d\n", var1 == this.majorProgress ? "Major" : "Minor", var2, var3, var4);
         }
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen3.this.setProgress(var1, var2, var3, var4);
            }
         });
      }

   }

   private void initComponents() {
      this.bannerImage = new JLabel();
      this.minorProgress = new JProgressBar();
      this.state = new JLabel();
      this.majorProgress = new JProgressBar();
      this.stageIcon = new JLabel();
      this.skip = new JButton();
      this.setDefaultCloseOperation(0);
      this.setResizable(false);
      this.setUndecorated(true);
      this.bannerImage.setIcon(new ImageIcon(this.getClass().getResource("/images/earth.png")));
      this.stageIcon.setIcon(new ImageIcon(this.getClass().getResource("/images/blockdevice.png")));
      this.skip.setText("Skip");
      GroupLayout var1 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.bannerImage).addGroup(var1.createSequentialGroup().addComponent(this.stageIcon).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.state, -1, 326, 32767).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.TRAILING).addComponent(this.minorProgress, Alignment.LEADING, -1, 267, 32767).addComponent(this.majorProgress, -1, 267, 32767)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.skip))))).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addGap(11, 11, 11).addComponent(this.bannerImage).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.TRAILING).addGroup(var1.createSequentialGroup().addComponent(this.state, -2, 20, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addComponent(this.majorProgress, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED, 13, 32767).addComponent(this.minorProgress, -2, -1, -2)).addComponent(this.skip, -1, 45, 32767))).addComponent(this.stageIcon)).addContainerGap()));
      this.pack();
   }
}
