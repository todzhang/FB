package ddb.dsz.plugin.logviewer;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

final class TabPopUpListener extends MouseAdapter {
   private final LogViewer _viewer;
   private final CoreController core;

   TabPopUpListener(LogViewer viewer, CoreController core) {
      this._viewer = viewer;
      this.core = core;
   }

   public void mousePressed(MouseEvent e) {
      this.maybeShowPopup(e);
   }

   public void mouseReleased(MouseEvent e) {
      this.maybeShowPopup(e);
   }

   private void maybeShowPopup(MouseEvent e) {
      try {
         if (e.isPopupTrigger()) {
            MultipleTargetWorkbench tabPane = this._viewer.getWorkbench();
            int tabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
            if (tabIndex < 0 || tabIndex >= tabPane.getTabCount()) {
               return;
            }
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
         this.core.logEvent(Level.SEVERE, "Can't show popup", var4);
      }

   }

   public void mouseClicked(MouseEvent e) {
      this.maybeShowPopup(e);
   }
}
