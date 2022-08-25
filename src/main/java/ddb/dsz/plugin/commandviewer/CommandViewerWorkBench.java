package ddb.dsz.plugin.commandviewer;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class CommandViewerWorkBench extends MultipleTargetWorkbench {
   private boolean showingAll = false;

   public CommandViewerWorkBench(CommandViewer var1) {
      super(var1);
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, CommandViewerWorkBench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return new CommandViewerPopupMenu(this);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new CommandViewerPopupListener(this);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   public JComponent getDefaultElement() {
      Component var1 = this.getSelectedComponent();
      if (var1 == null) {
         return null;
      } else {
         Tabbable var2 = this.getTabbableForDisplay(var1);
         return var2.getDefaultElement();
      }
   }

   public synchronized void showAll() {
      if (!this.showingAll) {
         this.showingAll = true;
         final CommandViewerHost var1 = ((CommandViewer)CommandViewer.class.cast(this.owner)).newHost((HostInfo)null);
         var1.setName("All");
         ((CommandViewer)CommandViewer.class.cast(this.owner)).getDemultiplexor().addCommandEventListenerAll(var1);
         this.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var1});
         this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var1});
         final CoreController var2 = ((CommandViewer)CommandViewer.class.cast(this.owner)).getCore();
         var2.execute(new Runnable() {
            public void run() {
               var1.addTasks(var2.getTaskList());
            }
         });
      }
   }

   public boolean isShowingAll() {
      return this.showingAll;
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         CommandViewerWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         CommandViewerWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }

   public static enum LogViewerAction {
      ADDTARGETLOGS;
   }
}
