package ds.gui;

import ddb.detach.Alignment;
import ddb.dsz.plugin.Plugin;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

public class NewPluginTabAction extends AbstractAction {
   private Class<?> pluginClass;
   private PluginWorkbench workbench;
   private Logger logger;
   private Alignment alignment;

   public NewPluginTabAction(Class<?> var1, PluginWorkbench var2, Logger var3, Alignment var4) {
      this.pluginClass = var1;
      this.workbench = var2;
      this.logger = var3;
      this.alignment = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      try {
         Plugin var2 = this.workbench.startPlugin(this.pluginClass, (String)null, (List)null, (Alignment)this.alignment, (String)null);
         if (var2 == null) {
            this.logger.logp(Level.WARNING, this.getClass().getName(), "actionPerformed", "Failed to add plugin of class" + this.pluginClass.getName());
         }
      } catch (Exception var3) {
         this.logger.log(Level.WARNING, "Exception while starting new plugin", var3);
      }

   }
}
