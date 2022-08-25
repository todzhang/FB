package ddb.splash;

import ddb.GuiConstants;
import ddb.imagemanager.ImageManager;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SplashScreen2 extends JFrame {
   public static final Object SHOW = new Object();
   public static final Object HIDE = new Object();
   List<SplashScreen2.MajorStage> stages = new Vector();
   private JLabel bannerImage;
   private JProgressBar majorProgress;
   private JProgressBar minorProgress;
   private JButton skip;
   private JLabel stageIcon;
   private JLabel state;

   public void begin() {
      SwingWorker var1 = new SwingWorker<Object, Object>() {
         protected Object doInBackground() throws Exception {
            this.publish(new Object[]{SplashScreen2.SHOW});
            this.publish(new Object[]{SplashScreen2.this.stages.size()});
            Iterator var1 = SplashScreen2.this.stages.iterator();

            while(var1.hasNext()) {
               SplashScreen2.MajorStage var2 = (SplashScreen2.MajorStage)var1.next();
               this.publish(new Object[]{var2});
               var2.run();
               Iterator var3 = var2.getStages().iterator();

               while(var3.hasNext()) {
                  SplashScreen2.MinorStage var4 = (SplashScreen2.MinorStage)var3.next();
                  this.publish(new Object[]{var4});
                  var4.run();
               }
            }

            this.publish(new Object[]{SplashScreen2.HIDE});
            return SplashScreen2.HIDE;
         }

         protected void process(List<Object> var1) {
            if (var1 != null) {
               Iterator var2 = var1.iterator();

               while(var2.hasNext()) {
                  Object var3 = var2.next();
                  if (var3 == SplashScreen2.SHOW) {
                     SplashScreen2.this.setVisible(true);
                     return;
                  }

                  if (var3 == SplashScreen2.HIDE) {
                     SplashScreen2.this.setVisible(false);
                     SplashScreen2.this.dispose();
                     return;
                  }

                  if (var3 instanceof SplashScreen2.MajorStage) {
                     SplashScreen2.MajorStage var4 = (SplashScreen2.MajorStage)var3;
                     if (var4.getStages().size() == 0) {
                        SplashScreen2.this.minorProgress.setIndeterminate(true);
                     } else {
                        SplashScreen2.this.minorProgress.setIndeterminate(false);
                        SplashScreen2.this.minorProgress.setValue(0);
                        SplashScreen2.this.minorProgress.setMaximum(var4.getStages().size() + 1);
                     }

                     SplashScreen2.this.majorProgress.setValue(SplashScreen2.this.majorProgress.getValue() + 1);
                     SplashScreen2.this.state.setText(var4.name);
                     SplashScreen2.this.stageIcon.setIcon(ImageManager.getIcon(var4.icon, ImageManager.SIZE64));
                  } else if (var3 instanceof SplashScreen2.MinorStage) {
                     SplashScreen2.MinorStage var5 = (SplashScreen2.MinorStage)var3;
                     if (var5.name != null) {
                        SplashScreen2.this.state.setText(var5.name);
                     }

                     SplashScreen2.this.minorProgress.setValue(SplashScreen2.this.minorProgress.getValue() + 1);
                     SplashScreen2.this.state.setText(var5.name);
                  } else if (var3 instanceof Integer) {
                     SplashScreen2.this.majorProgress.setMaximum((Integer)var3);
                  }
               }

            }
         }
      };
      var1.execute();
      if (!EventQueue.isDispatchThread()) {
         try {
            var1.get();
         } catch (Exception var3) {
         }
      }

   }

   public SplashScreen2(String var1) {
      this.initComponents();
      GuiConstants.locate(this, 0.5D, 0.25D);
      this.setTitle(var1);
      this.skip.setVisible(false);
   }

   public void addStage(SplashScreen2.MajorStage var1) {
      this.stages.add(var1);
   }

   public void setStageIcon(String var1) {
      this.stageIcon.setIcon(ImageManager.getIcon(var1, ImageManager.SIZE64));
   }

   public void setState(String var1) {
      this.state.setText(var1);
   }

   public void setSkipVisibility(boolean var1) {
      this.skip.setVisible(var1);
   }

   public void setMinorProgress(int var1, int var2, int var3) {
      this.setProgress(this.minorProgress, var1, var2, var3);
   }

   public void setMajorProgress(int var1, int var2, int var3) {
      this.setProgress(this.majorProgress, var1, var2, var3);
   }

   public void setMinorIndeterminate(boolean var1) {
      this.minorProgress.setIndeterminate(var1);
   }

   public void setMajorIndeterminate(boolean var1) {
      this.majorProgress.setIndeterminate(var1);
   }

   private void setProgress(JProgressBar var1, int var2, int var3, int var4) {
      var1.setMinimum(var2);
      var1.setMaximum(var3);
      var1.setValue(var4);
      var1.setIndeterminate(false);
   }

   private void initComponents() {
      this.bannerImage = new JLabel();
      this.majorProgress = new JProgressBar();
      this.minorProgress = new JProgressBar();
      this.state = new JLabel();
      this.stageIcon = new JLabel();
      this.skip = new JButton();
      this.setDefaultCloseOperation(0);
      this.setResizable(false);
      this.setUndecorated(true);
      this.bannerImage.setIcon(new ImageIcon(this.getClass().getResource("/images/earth.png")));
      this.state.setText("State");
      this.stageIcon.setIcon(new ImageIcon(this.getClass().getResource("/images/3d.png")));
      this.skip.setText("Skip");
      GroupLayout var1 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.bannerImage).addGroup(var1.createSequentialGroup().addContainerGap().addComponent(this.stageIcon).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.minorProgress, -1, 251, 32767).addComponent(this.majorProgress, -1, 251, 32767).addComponent(this.state, -1, 251, 32767)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.skip).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addComponent(this.bannerImage).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createParallelGroup(Alignment.LEADING, false).addComponent(this.stageIcon).addGroup(var1.createSequentialGroup().addComponent(this.state).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.majorProgress, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addComponent(this.minorProgress, -2, -1, -2))).addComponent(this.skip, -1, 64, 32767)).addContainerGap()));
      this.pack();
   }

   public abstract class MinorStage {
      String name = null;

      public MinorStage() {
      }

      public MinorStage(String var2) {
         this.name = var2;
      }

      public abstract void run();
   }

   public abstract class MajorStage {
      String name = null;
      String icon = null;

      public MajorStage() {
      }

      public MajorStage(String var2, String var3) {
         this.name = var2;
         this.icon = var3;
      }

      public abstract List<SplashScreen2.MinorStage> getStages();

      public void run() {
      }
   }
}
