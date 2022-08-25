package ddb.dsz.plugin.monitor;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.util.Iterator;
import javax.swing.JPopupMenu;

public class MonitorWorkBench extends MultipleTargetWorkbench {
   private boolean showingAll = false;

   public MonitorWorkBench(MonitorWindow2 var1) {
      super(var1);
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, MonitorWorkBench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return new MonitorPopupMenu(this);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new MonitorPopupListener(this);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   public synchronized void showAll() {
      if (!this.showingAll) {
         this.showingAll = true;
         MonitorHost var1 = ((MonitorWindow2)MonitorWindow2.class.cast(this.owner)).newHost((HostInfo)null);
         var1.setName("All");
         ((MonitorWindow2)MonitorWindow2.class.cast(this.owner)).getDemultiplexor().addCommandEventListenerAll(var1);
         this.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var1});
         this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var1});
         CoreController var2 = ((MonitorWindow2)MonitorWindow2.class.cast(this.owner)).getCore();
         Iterator var3 = var2.getTaskList().iterator();

         while(var3.hasNext()) {
            Task var4 = (Task)var3.next();
            var1.addTask(var4);
         }

      }
   }

   public boolean isShowingAll() {
      return this.showingAll;
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         MonitorWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         MonitorWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }
}
