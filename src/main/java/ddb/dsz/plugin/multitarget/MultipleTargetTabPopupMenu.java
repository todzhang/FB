package ddb.dsz.plugin.multitarget;

import ddb.detach.TabbablePopupMenu;
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

public class MultipleTargetTabPopupMenu extends TabbablePopupMenu {
   public MultipleTargetTabPopupMenu(MultipleTargetWorkbench var1, final MultipleTargetPlugin var2, SingleTargetInterface var3, CoreController var4) {
      super(var3, var1);
      if (var4 != null) {
         List var5 = var4.getHosts();
         boolean var6 = false;
         JMenu var7 = new JMenu(var2.newItemName());
         var7.setIcon(ImageManager.getIcon(Icons.REMOTE_HOST.getIcon(), var4.getLabelImageSize()));
         Iterator var8 = var5.iterator();

         while(var8.hasNext()) {
            final HostInfo var9 = (HostInfo)var8.next();
            if (var9.isLocal()) {
            }

            var6 = true;
            JMenuItem var10 = new JMenuItem(var9.getId());
            var10.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent var1) {
                  var2.newItem(var9);
               }
            });
            var7.add(var10);
         }

         if (var6) {
            this.add(var7);
         }
      }

   }
}
