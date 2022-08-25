package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import ddb.imagemanager.ImageManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;

public class LogViewPane extends LogViewerDetachable {
   float size = 14.0F;
   File logFile;
   JTextArea pane = new JTextArea();
   JScrollPane scroller;
   JTextField title;
   JToolBar menu;
   CoreController core;

   public LogViewPane(CoreController core, File logFile) {
      this.scroller = new JScrollPane(this.pane);
      this.title = new JTextField();
      this.menu = new JToolBar(0);
      this.logFile = logFile;
      this.core = core;
      super.setName(logFile.getName());
      super.setAlignment(Alignment.RIGHT);
      super.setShowButtons(true);
      super.setVerifyClose(false);
      this.title.setBorder((Border)null);
      this.title.setEditable(false);
      this.title.setText(logFile.getAbsolutePath());
      this.pane.setEditable(false);
      this.update();
      this.display.setLayout(new BorderLayout());
      this.display.add(this.scroller, "Center");
      this.display.add(this.menu, "North");
      this.menu.add(new JLabel("Font Size:"));
      JButton increaseFont = new JButton("");
      increaseFont.addActionListener(new LogViewPane.IncreaseAction());
      increaseFont.setToolTipText("Increase font size");
      increaseFont.setIcon(ImageManager.getIcon("images/blue-plus.png", core.getLabelImageSize()));
      this.menu.add(increaseFont);
      JButton decreaseFont = new JButton("");
      decreaseFont.addActionListener(new LogViewPane.DecreaseAction());
      decreaseFont.setToolTipText("Decrease font size");
      decreaseFont.setIcon(ImageManager.getIcon("images/blue-minus.png", core.getLabelImageSize()));
      this.menu.add(decreaseFont);
   }

   void update() {
      if (this.logFile != null) {
         long currentLength = (long)this.pane.getText().length();
         long desiredLength = this.logFile.length();
         if (currentLength != desiredLength) {
            try {
               FileInputStream fis = new FileInputStream(this.logFile);
               fis.skip(currentLength);
               int read = 0;

               for(byte[] buffer = new byte[8096]; read != -1; read = fis.read(buffer)) {
                  this.pane.append(new String(buffer, 0, read));
               }
            } catch (IOException var8) {
            }

         }
      }
   }

   @Override
   public boolean isClosable() {
      return true;
   }

   @Override
   public JComponent getHeader() {
      return this.title;
   }

   private final class DecreaseAction implements ActionListener {
      private DecreaseAction() {
      }

      public void actionPerformed(ActionEvent e) {
         Font f = LogViewPane.this.pane.getFont().deriveFont(0, --LogViewPane.this.size);
         LogViewPane.this.pane.setFont(f);
      }

      // $FF: synthetic method
      DecreaseAction(Object x1) {
         this();
      }
   }

   private final class IncreaseAction implements ActionListener {
      private IncreaseAction() {
      }

      public void actionPerformed(ActionEvent e) {
         Font f = LogViewPane.this.pane.getFont().deriveFont(0, ++LogViewPane.this.size);
         LogViewPane.this.pane.setFont(f);
      }

      // $FF: synthetic method
      IncreaseAction(Object x1) {
         this();
      }
   }
}
