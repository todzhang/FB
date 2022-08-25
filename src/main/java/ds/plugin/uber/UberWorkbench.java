package ds.plugin.uber;

import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;
import ds.gui.PluginWorkbench;
import java.util.List;

public class UberWorkbench extends PluginWorkbench {
   private final UberPlugin _plugin;

   public UberWorkbench(UberPlugin plugin, CoreController core, int var3, int var4) {
      super(core, plugin.getParentWorkbench(), var3, var4);
      this._plugin = plugin;
      this.init();
   }

   public UberWorkbench(UberPlugin var1, CoreController var2, int var3) {
      super(var2, var1.getParentWorkbench(), var3);
      this._plugin = var1;
      this.init();
   }

   public UberWorkbench(UberPlugin var1, CoreController var2) {
      super(var2, var1.getParentWorkbench());
      this._plugin = var1;
      this.init();
   }

   public void init() {
   }

   public Plugin startPlugin(Class<?> var1, String var2, List<String> var3, Alignment var4, String var5) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException {
      Plugin var6 = super.startPlugin(var1, var2, var3, var4, var5);
      if (var6 != null) {
      }

      return var6;
   }
}
