package ds.gui;

import ddb.detach.WorkbenchPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenuItem;

public class PluginWorkbenchPopupMenu extends WorkbenchPopupMenu {
   private PluginWorkbench workbench;

   public PluginWorkbenchPopupMenu(PluginWorkbench workbench) {
      super(workbench);
      this.workbench = workbench;
      List var2 = this.workbench.getNewInstance();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         JMenuItem var4 = (JMenuItem)var3.next();
         this.add(var4);
      }

      JMenuItem jMenuItem = new JMenuItem("Running plugins");
      jMenuItem.addActionListener(var1 -> PluginWorkbenchPopupMenu.this.workbench.showRunningPluginsList());
      this.add(jMenuItem);
   }
}
