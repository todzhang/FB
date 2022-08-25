package ddb.dsz.plugin.filemanager.ver3;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import javax.swing.JPopupMenu;

public class FileManagerWorkBench extends MultipleTargetWorkbench {
   CoreController core;
   FileManagerPopupListener popup;

   public FileManagerWorkBench(FileManager var1) {
      super(var1);
      this.init();
   }

   public void setCore(CoreController var1) {
      this.core = var1;
      this.popup.setCore(var1);
   }

   private void init() {
      this.actionHandler.put(WorkbenchAction.CLOSETAB, FileManagerWorkBench.CloseTab.class);
      if (this.popup != null) {
         this.popup.setCore(this.core);
         this.popup.setManager((MultipleTargetPlugin)FileManager.class.cast(this.owner));
      }

   }

   @Override
   public JPopupMenu getMenu() {
      return new FileManagerPopupMenu(this, (FileManager)FileManager.class.cast(this.owner), this.core);
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      if (this.popup == null) {
         this.popup = new FileManagerPopupListener(this);
      }

      return this.popup;
   }

   @Override
   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.compareTo(tab2);
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         FileManagerWorkBench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         FileManagerWorkBench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         this.tab.hideFrame();
         this.tab.close();
      }
   }
}
