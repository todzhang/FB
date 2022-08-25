package ddb.splash;

import ddb.imagemanager.ImageManager;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.apache.commons.collections.Closure;

public class SplashScreen extends JFrame {
   private JPanel mainImagePanel;
   private JLabel progressLabel;
   private JLabel progressIcon;
   private JProgressBar majorProgressBar;
   private JProgressBar minorProgressBar;
   private List<ProgressItem> stages;
   private int currentStage;
   private JButton skip = new JButton("Skip");
   private String text = null;
   public static int DONE = -2;
   public static int NOT_STARTED = -1;

   public SplashScreen(String var1, Icon var2, int var3, int var4) throws HeadlessException {
      super(var1);
      this.setSize(var3, var4);
      this.setResizable(false);
      this.setUndecorated(true);
      this.mainImagePanel = new JPanel();
      this.progressLabel = new JLabel();
      this.progressIcon = new JLabel();
      this.stages = new Vector();
      this.currentStage = NOT_STARTED;
      this.majorProgressBar = new JProgressBar(0, 0);
      this.minorProgressBar = new JProgressBar(0, 0);
      this.setDefaultCloseOperation(2);
      this.progressIcon.setText("");
      Container var5 = this.getContentPane();
      GridBagLayout var6 = new GridBagLayout();
      GridBagConstraints var7 = new GridBagConstraints();
      var5.setLayout(var6);
      var5.add(this.mainImagePanel);
      var5.add(this.majorProgressBar);
      var5.add(this.minorProgressBar);
      var5.add(this.progressIcon);
      var5.add(this.progressLabel);
      var5.add(this.skip);
      var7.gridx = 0;
      var7.gridy = 0;
      var7.gridwidth = 3;
      var7.fill = 1;
      var7.weightx = 10.0D;
      var7.weighty = 100.0D;
      var6.addLayoutComponent(this.mainImagePanel, var7);
      var7.weightx = 0.0D;
      var7.weighty = 10.0D;
      var7.gridx = 0;
      var7.gridy = 1;
      var7.gridwidth = 1;
      var7.gridheight = 3;
      var6.addLayoutComponent(this.progressIcon, var7);
      var7.weightx = 0.0D;
      var7.gridheight = 1;
      var7.gridwidth = 0;
      var7.gridx = 1;
      var7.gridy = 1;
      var6.addLayoutComponent(this.progressLabel, var7);
      var7.gridx = 2;
      var7.gridy = 2;
      var7.gridheight = 2;
      var7.gridwidth = 1;
      var7.weightx = 0.0D;
      var6.addLayoutComponent(this.skip, var7);
      var7.insets = new Insets(1, 1, 1, 1);
      var7.gridheight = 1;
      var7.weighty = 0.0D;
      var7.weightx = 10.0D;
      var7.gridx = 1;
      var7.gridy = 2;
      var6.addLayoutComponent(this.majorProgressBar, var7);
      var7.gridx = 1;
      var7.gridy = 3;
      var6.addLayoutComponent(this.minorProgressBar, var7);
      this.skip.setVisible(false);
      this.mainImagePanel.add(new JLabel(var2));
   }

   public void begin() {
      try {
         this.majorProgressBar.setMaximum(this.stages.size());
         this.majorProgressBar.setMinimum(0);

         for(int var1 = 0; var1 < this.stages.size(); ++var1) {
            final ProgressItem var2 = (ProgressItem)this.stages.get(var1);
            int finalVar = var1;
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  SplashScreen.this.majorProgressBar.setValue(finalVar);
                  SplashScreen.this.minorProgressBar.setMaximum(10);
                  SplashScreen.this.minorProgressBar.setMinimum(0);
                  SplashScreen.this.minorProgressBar.setIndeterminate(true);
                  SplashScreen.this.progressLabel.setText(var2.getText());
                  SplashScreen.this.progressIcon.setIcon(ImageManager.getIcon(var2.getIcon(), ImageManager.SIZE48));
                  SplashScreen.this.text = var2.getText();
               }
            });
            var2.execute((Object)null);
         }
      } finally {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen.this.setVisible(false);
               SplashScreen.this.dispose();
            }
         });
      }

   }

   public void addStage(String var1, String var2, Closure var3) {
      this.stages.add(new ProgressItem(var1, var2, var3));
      this.majorProgressBar.setMaximum(this.majorProgressBar.getMaximum() + 1);
   }

   public int getStageCount() {
      return this.stages.size();
   }

   public void showSkip(ActionListener var1) {
      if (var1 != null) {
         this.skip.addActionListener(var1);
      }

      EventQueue.invokeLater(new Runnable() {
         public void run() {
            SplashScreen.this.skip.setVisible(true);
         }
      });
   }

   public void updateMinor(final int var1, final int var2, final String var3) {
      if (EventQueue.isDispatchThread()) {
         this.progressLabel.setText(String.format("%s: %s", this.text, var3));
         this.minorProgressBar.setIndeterminate(false);
         this.minorProgressBar.setMaximum(var2);
         this.minorProgressBar.setValue(var1);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen.this.updateMinor(var1, var2, var3);
            }
         });
      }

   }

   public void updateMinorIndeterminate(final String var1) {
      if (EventQueue.isDispatchThread()) {
         this.progressLabel.setText(String.format("%s: %s", this.text, var1));
         this.minorProgressBar.setIndeterminate(true);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SplashScreen.this.updateMinorIndeterminate(var1);
            }
         });
      }

   }

   public int getStage() {
      return this.currentStage;
   }
}
