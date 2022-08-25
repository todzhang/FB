package ddb.dsz.plugin.filemanager.ver3;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetTabPopupMenu;
import ddb.imagemanager.ImageManager;
import ddb.targetmodel.filemodel.DriveType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;

public class FileManagerTabPopupMenu extends MultipleTargetTabPopupMenu {
   public FileManagerTabPopupMenu(FileManagerWorkBench var1, final FileManager var2, FileManagerHost var3, CoreController var4) {
      super(var1, var2, var3, var4);
      List var5 = var4.getHosts();
      final HostInfo var6 = var3.getTarget();
      if (var6 != null) {
         JMenuItem var7 = new JMenuItem("Search");
         var7.setIcon(ImageManager.getIcon(DriveType.FILESEARCH.getIcon(), var4.getLabelImageSize()));
         var7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
               var2.newSearch(var6, 0L);
            }
         });
         this.add(var7);
      }

   }
}
