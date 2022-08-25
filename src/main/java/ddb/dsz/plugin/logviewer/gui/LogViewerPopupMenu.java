package ddb.dsz.plugin.logviewer.gui;

import ddb.detach.WorkbenchPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class LogViewerPopupMenu extends WorkbenchPopupMenu {
   public LogViewerPopupMenu(final LogViewerWorkBench workbench) {
      super(workbench);
      if (!workbench.isShowingAll()) {
         JMenuItem showAll = new JMenuItem("All Commands");
         showAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               workbench.showAll();
            }
         });
         this.add(showAll);
      }

   }
}
