package ddb.dsz.plugin.commandviewer;

import ddb.detach.WorkbenchPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class CommandViewerPopupMenu extends WorkbenchPopupMenu {
   public CommandViewerPopupMenu(final CommandViewerWorkBench var1) {
      super(var1);
      if (!var1.isShowingAll()) {
         JMenuItem var2 = new JMenuItem("All Commands");
         var2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1x) {
               var1.showAll();
            }
         });
         this.add(var2);
      }

   }
}
