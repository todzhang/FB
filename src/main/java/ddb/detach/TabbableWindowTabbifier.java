package ddb.detach;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import javax.swing.Icon;
import javax.swing.JOptionPane;

public class TabbableWindowTabbifier extends WindowAdapter implements WindowListener, WindowStateListener {
   private Workbench workbench;
   private Tabbable plugin;

   public TabbableWindowTabbifier(Tabbable var1, Workbench var2) {
      this.workbench = var2;
      this.plugin = var1;
   }

   public void windowClosing(WindowEvent var1) {
      if (this.plugin.isClosable()) {
         switch(JOptionPane.showOptionDialog((Component)null, "Choose 'Yes' to close, or 'No' to return the tool to the main window.", "Close this window?", 1, 3, (Icon)null, (Object[])null, (Object)null)) {
         case 0:
            this.workbench.enqueAction(Workbench.WorkbenchAction.CLOSETAB, this.plugin);
            break;
         case 1:
            this.workbench.enqueAction(Workbench.WorkbenchAction.TABBIFYTAB, this.plugin);
            break;
         case 2:
            return;
         }
      } else {
         this.workbench.enqueAction(Workbench.WorkbenchAction.TABBIFYTAB, this.plugin);
      }

   }
}
