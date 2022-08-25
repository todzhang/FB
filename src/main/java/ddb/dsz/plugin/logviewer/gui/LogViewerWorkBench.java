package ddb.dsz.plugin.logviewer.gui;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.logviewer.LogViewer;
import ddb.dsz.plugin.logviewer.gui.target.TargetLogspace;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class LogViewerWorkBench extends MultipleTargetWorkbench {
   private boolean showingAll = false;

   public LogViewerWorkBench(LogViewer viewer) {
      super(viewer);
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, LogViewerWorkBench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return new LogViewerPopupMenu(this);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new LogViewerPopupListener(this);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   public JComponent getDefaultElement() {
      Component c = this.getSelectedComponent();
      if (c == null) {
         return null;
      } else {
         Tabbable t = this.getTabbableForDisplay(c);
         return t instanceof LogViewerDetachable ? ((LogViewerDetachable)LogViewerDetachable.class.cast(c)).getDefaultElement() : null;
      }
   }

   public synchronized void showAll() {
      if (!this.showingAll) {
         this.showingAll = true;
         final TargetLogspace allTargets = ((LogViewer)LogViewer.class.cast(this.owner)).createLogspace((HostInfo)null, true, false);
         allTargets.setIgnoreHost(true);
         allTargets.setName("All");
         ((LogViewer)LogViewer.class.cast(this.owner)).getDemultiplexor().addCommandEventListenerAll(allTargets);
         this.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{allTargets});
         this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{allTargets});
         final CoreController core = ((LogViewer)LogViewer.class.cast(this.owner)).getCore();
         core.execute(new Runnable() {
            public void run() {
               allTargets.addTasks(core.getTaskList());
            }
         });
      }
   }

   public boolean isShowingAll() {
      return this.showingAll;
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable plugin) {
         super( plugin);
      }

      public void runTask() {
         LogViewerWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         LogViewerWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }

   public static enum LogViewerAction {
      ADDTARGETLOGS;
   }
}
