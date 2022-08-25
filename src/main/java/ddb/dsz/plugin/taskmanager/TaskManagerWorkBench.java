package ddb.dsz.plugin.taskmanager;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import java.util.List;
import java.util.Vector;
import javax.swing.JPopupMenu;

public class TaskManagerWorkBench extends MultipleTargetWorkbench {
   List<TargetProcessSpace> lists = new Vector();

   public TaskManagerWorkBench(TaskManager2 viewer) {
      super(viewer);
      this.init();
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, TaskManagerWorkBench.CloseTab.class);
   }

   @Override
   public JPopupMenu getMenu() {
      return new TaskManagerPopupMenu(this, (TaskManager2)TaskManager2.class.cast(super.owner));
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new TaskManagerPopupListener(this);
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable plugin) {
         super( plugin);
      }

      public void runTask() {
         TaskManagerWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         TaskManagerWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }
}
