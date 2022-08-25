package ddb.dsz.plugin.filemanager.ver3;

import ddb.detach.Tabbable;
import ddb.detach.TabbablePopupMenu;
import ddb.detach.Workbench;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.multitarget.MultipleTargetPopupListener;

public class FileManagerPopupListener extends MultipleTargetPopupListener {
   public FileManagerPopupListener(Workbench var1, FileManager var2, CoreController var3) {
      super(var1, var2, var3);
   }

   public FileManagerPopupListener(Workbench var1) {
      super(var1);
   }

   protected TabbablePopupMenu getPopupMenu(Tabbable var1) {
      return new FileManagerTabPopupMenu((FileManagerWorkBench)FileManagerWorkBench.class.cast(this.workbench), (FileManager)FileManager.class.cast(this.manager), (FileManagerHost)FileManagerHost.class.cast(var1), this.core);
   }
}
