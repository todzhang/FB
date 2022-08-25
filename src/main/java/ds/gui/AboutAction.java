package ds.gui;

import ddb.imagemanager.ImageManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AboutAction extends AbstractAction {
   JComponent parent;
   Icon aboutIcon;

   public AboutAction(JComponent parent) {
      this.parent = parent;
      this.aboutIcon = ImageManager.getIcon("images/Rupert.png", ImageManager.SIZE64);
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      Runnable var2 = new Runnable() {
         public void run() {
            JOptionPane.showMessageDialog(AboutAction.this.parent, "DSClient 1.2", "About", 1, AboutAction.this.aboutIcon);
         }
      };
      SwingUtilities.invokeLater(var2);
   }
}
