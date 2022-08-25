package ds.core;

import ddb.dsz.plugin.Plugin;
import ds.gui.PluginWorkbench;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ShutdownPluginAction extends AbstractAction {
   private Plugin plugin;
   private PluginWorkbench workbench;

   public ShutdownPluginAction(Plugin plugin, PluginWorkbench workbench) {
      this.plugin = plugin;
      this.workbench = workbench;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      this.workbench.enqueAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{this.plugin});
   }
}
