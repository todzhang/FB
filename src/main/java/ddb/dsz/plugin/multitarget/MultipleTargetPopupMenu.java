package ddb.dsz.plugin.multitarget;

import ddb.detach.WorkbenchPopupMenu;
import ddb.dsz.Icons;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.imagemanager.ImageManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MultipleTargetPopupMenu extends WorkbenchPopupMenu {
   public MultipleTargetPopupMenu(MultipleTargetWorkbench var1, MultipleTargetPlugin var2) {
      super(var1);
      this.init(var2, var2.getCoreController());
   }

   public MultipleTargetPopupMenu(MultipleTargetWorkbench var1, MultipleTargetPlugin var2, CoreController var3) {
      super(var1);
      this.init(var2, var3);
   }

   private void init(final MultipleTargetPlugin var1, CoreController var2) {
      List var3 = var2.getHosts();
      boolean var4 = false;
      JMenu var5 = new JMenu(var1.newItemName());
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         final HostInfo var7 = (HostInfo)var6.next();
         if (var7.isLocal()) {
         }

         var4 = true;
         JMenuItem var8 = new JMenuItem(var7.getId());
         if (var7.isLocal()) {
            var8.setIcon(ImageManager.getIcon(Icons.LOCAL_HOST.getIcon(), var2.getLabelImageSize()));
         } else {
            var8.setIcon(ImageManager.getIcon(Icons.REMOTE_HOST.getIcon(), var2.getLabelImageSize()));
         }

         var8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1x) {
               var1.newItem(var7);
            }
         });
         var5.add(var8);
      }

      if (var4) {
         this.add(var5);
      }

   }
}
