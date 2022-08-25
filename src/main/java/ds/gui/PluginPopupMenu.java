package ds.gui;

import ddb.detach.TabbablePopupMenu;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.Icons;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.Plugin;
import ddb.imagemanager.ImageManager;
import ds.proxy.PluginProxyHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class PluginPopupMenu extends TabbablePopupMenu {
   public PluginPopupMenu(final Plugin var1, final PluginWorkbench var2, final CoreController var3) {
      super(var1, var2);
      if (var1.isHideable()) {
         JMenuItem var4 = this.add("Hide " + var1.getName());
         var4.setIcon(ImageManager.getIcon("images/folder_sent_mail.png", var3.getLabelImageSize()));
         var4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1x) {
               var2.enqueAction(WorkbenchAction.HIDETAB, new Object[]{var1});
            }
         });
      }

      List var9;
      if (var3 != null) {
         var9 = var3.getHosts();
         if (var1.canSetTarget() && var1.getTarget() != null && var9.size() > 0) {
            JMenu var5 = new JMenu("Set Host");
            var5.setIcon(ImageManager.getIcon(Icons.REMOTE_HOST.getIcon(), var3.getLabelImageSize()));
            this.add(var5);

            JRadioButtonMenuItem var8;
            for(Iterator var6 = var9.iterator(); var6.hasNext(); var5.add(var8)) {
               final HostInfo var7 = (HostInfo)var6.next();
               var8 = new JRadioButtonMenuItem(var7.getId());
               if (var7.isLocal()) {
                  var8.setIcon(ImageManager.getIcon(Icons.LOCAL_HOST.getIcon(), var3.getLabelImageSize()));
               } else {
                  var8.setIcon(ImageManager.getIcon(Icons.REMOTE_HOST.getIcon(), var3.getLabelImageSize()));
               }

               if (var1.getTarget().equals(var7)) {
                  var8.setSelected(true);
               } else {
                  var8.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent var1x) {
                        var1.setTarget(var7);
                     }
                  });
               }
            }
         }
      }

      var9 = var2.getNewInstance();
      Iterator var10 = var9.iterator();

      JMenuItem var12;
      while(var10.hasNext()) {
         var12 = (JMenuItem)var10.next();
         this.add(var12);
      }

      JMenuItem var11 = new JMenuItem("Running plugins");
      var11.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            var2.showRunningPluginsList();
         }
      });
      this.add(var11);
      var12 = new JMenuItem("New Instance of " + var1.getName());
      var12.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1x) {
            try {
               Class var2x = ((Plugin)Plugin.class.cast(PluginProxyHandler.Unwrap(var1))).getClass();
               var2.startPlugin(var2x, var1.getName(), (List)null, var1.getAlignment(), (String)null, true);
            } catch (Exception var3x) {
               var3.logEvent(Level.WARNING, "Unable to duplicate plugin", var3x);
            }

         }
      });
      var12.setIcon(ImageManager.getIcon("images/edit_add.png", var3.getLabelImageSize()));
      Class var13 = var1.getClass();
      if (var1.allowNewInstance(var13)) {
         this.add(var12);
      }

      JMenuItem var14 = new JMenuItem("Options");
      var14.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1x) {
            var2.showOptions(var1);
         }
      });
      var14.setIcon(ImageManager.getIcon("images/utilities.png", var3.getLabelImageSize()));
      if (var1.getStaticOptions() != null || var1.getRegularOptions() != null) {
         this.add(var14);
      }

   }
}
