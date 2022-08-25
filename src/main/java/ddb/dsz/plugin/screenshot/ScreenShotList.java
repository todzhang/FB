package ddb.dsz.plugin.screenshot;

import ddb.dsz.core.controller.CoreController;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ScreenShotList extends JPanel {
   public final ImageObserver observer = new ImageObserver() {
      public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
         ScreenShotList.this.scale();
         return false;
      }
   };
   final DefaultListModel listOfFilesModel = new DefaultListModel();
   Image image;
   final CoreController core;
   boolean bDisabled;
   private JScrollPane jScrollPane1;
   private JList listOfFiles;
   private JLabel mainDisplay;

   public ScreenShotList(CoreController var1) {
      this.initComponents();
      this.core = var1;
      this.listOfFiles.setModel(this.listOfFilesModel);
      this.listOfFiles.setCellRenderer(ImageDataRenderer.Instance);
      this.mainDisplay.setBorder((Border)null);
   }

   public synchronized void addFile(final File var1, final Calendar var2) {
      if (EventQueue.isDispatchThread()) {
         if (var1.exists()) {
            ImageData var3 = new ImageData(var1, var2);

            for(int var4 = 0; var4 < this.listOfFilesModel.getSize(); ++var4) {
               if (var3.time < ((ImageData)ImageData.class.cast(this.listOfFilesModel.getElementAt(var4))).time) {
                  this.listOfFilesModel.add(var4, var3);
                  return;
               }
            }

            this.listOfFilesModel.addElement(var3);
         }
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               ScreenShotList.this.addFile(var1, var2);
            }
         });
      }

   }

   private void scale() {
      if (this.image == null) {
         this.bDisabled = false;
         this.mainDisplay.setIcon((Icon)null);
      } else {
         int var1 = this.image.getHeight(this.observer);
         int var2 = this.image.getWidth(this.observer);
         if (var1 != -1 && var2 != -1) {
            Insets var3 = new Insets(3, 3, 3, 3);
            int var4 = this.getWidth() - this.mainDisplay.getX();
            int var5 = this.getHeight() - this.mainDisplay.getY();
            var4 -= var3.left + var3.right;
            var5 -= var3.top + var3.bottom;
            double var6 = (double)var4 / (double)var2;
            double var8 = (double)var5 / (double)var1;
            double var10 = Math.min(var6, var8);
            if (var10 < 1.0D) {
               var2 = (int)((double)var2 * var10);
               var1 = (int)((double)var1 * var10);
            }

            this.core.submit(new ScreenShotList.ImageResizer(this.image, var1, var2));
         } else {
            this.mainDisplay.setIcon((Icon)null);
         }
      }
   }

   private void enableDisplay(boolean var1) {
      this.mainDisplay.setEnabled(var1);
      this.listOfFiles.setEnabled(var1);
   }

   private void initComponents() {
      this.mainDisplay = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.listOfFiles = new JList();
      this.mainDisplay.setBackground(new Color(204, 255, 204));
      this.mainDisplay.setHorizontalAlignment(0);
      this.mainDisplay.setText(" ");
      this.mainDisplay.setAlignmentX(0.5F);
      this.mainDisplay.setFocusable(false);
      this.mainDisplay.setHorizontalTextPosition(10);
      this.mainDisplay.setIconTextGap(0);
      this.mainDisplay.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            ScreenShotList.this.mainDisplayComponentResized(var1);
         }
      });
      this.listOfFiles.setModel(new AbstractListModel() {
         String[] strings = new String[]{"01:02:03", "99:00:11", "99:99:99"};

         public int getSize() {
            return this.strings.length;
         }

         public Object getElementAt(int var1) {
            return this.strings[var1];
         }
      });
      this.listOfFiles.setPrototypeCellValue("99:99:99");
      this.listOfFiles.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            ScreenShotList.this.listOfFilesValueChanged(var1);
         }
      });
      this.jScrollPane1.setViewportView(this.listOfFiles);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addComponent(this.jScrollPane1, -2, 98, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.mainDisplay, -1, 475, 32767)));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.mainDisplay, -1, 601, 32767).addComponent(this.jScrollPane1, -1, 601, 32767));
   }

   private void mainDisplayComponentResized(ComponentEvent var1) {
      if (!this.bDisabled) {
         if (this.image != null) {
            this.bDisabled = true;
            this.enableDisplay(false);
            this.scale();
         }

      }
   }

   private void listOfFilesValueChanged(ListSelectionEvent var1) {
      if (!var1.getValueIsAdjusting()) {
         this.enableDisplay(false);
         ImageData var2 = (ImageData)((JList)JList.class.cast(var1.getSource())).getSelectedValue();
         this.core.submit(new ScreenShotList.ImageLoader(var2.file));
         var2.setViewed();
         this.bDisabled = true;
      }
   }

   private class ImageResizer implements Runnable {
      final Image image;
      final int height;
      final int width;
      ImageIcon resized;

      public ImageResizer(Image var2, int var3, int var4) {
         this.image = var2;
         this.height = var3;
         this.width = var4;
      }

      public void run() {
         if (EventQueue.isDispatchThread()) {
            ScreenShotList.this.enableDisplay(true);
            ScreenShotList.this.bDisabled = false;
            ScreenShotList.this.mainDisplay.setIcon(this.resized);
         } else {
            this.resized = new ImageIcon(this.image.getScaledInstance(this.width, this.height, 4));
            EventQueue.invokeLater(this);
         }

      }
   }

   private class ImageLoader implements Runnable {
      final File file;
      Image localImage;

      public ImageLoader(File var2) {
         this.file = var2;
      }

      public void run() {
         if (EventQueue.isDispatchThread()) {
            ScreenShotList.this.image = this.localImage;
            ScreenShotList.this.scale();
         } else {
            try {
               this.localImage = ImageIO.read(this.file);
            } catch (Exception var2) {
               this.localImage = null;
               var2.printStackTrace();
            }

            EventQueue.invokeLater(this);
         }

      }
   }
}
