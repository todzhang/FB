package ds.plugin.uber;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ds.core.DSConstants;
import ds.gui.PluginWorkbench;
import ds.plugin.PluginContainer;
import java.util.List;
import javax.swing.JMenuBar;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/ksirtet.png")
@DszName("UberPlugin")
@DszDescription("A plugin to rule all other plugins")
public class UberPlugin extends PluginContainer {
   public static final int STARTUP_CONFIG_PARSER_ERROR = -100;
   private UberWorkbench myWorkbench;
   private JMenuBar superMenu;
   private boolean usingMenu;
   private boolean canStartPlugins = true;

   @Override
   public PluginWorkbench getChildWorkbench() {
      return this.myWorkbench;
   }

   public UberPlugin() {
      super.setName("UberPlugin");
      super.setCanClose(true);
      super.setUserClosable(true);
      super.setShowButtons(false);
      super.setCareAboutLocalEvents(true);
      this.superMenu = new JMenuBar();
      this.usingMenu = false;
   }

   @Override
   protected int init3() {
      this.myWorkbench = new UberWorkbench(this, this.decorate(this.core), DSConstants.SUB_TAB_ALIGNMENT);
      super.setDisplay(this.myWorkbench);
      return 0;
   }

   @Override
   protected final void fini3() {
      this.myWorkbench.stopAllPlugins();
   }

   @Override
   protected boolean parseArgument3(String arg, String logo) {
      if (arg.equalsIgnoreCase("-nostart")) {
         this.canStartPlugins = false;
         return true;
      } else if (arg.equalsIgnoreCase("-logo") && logo != null) {
         this.setLogo(logo);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5) {
      return this.canStartPlugins ? super.startNewPlugin(var1, var2, var3, var4, var5) : this.core.startNewPlugin(var1, var2, var3, var4, var5);
   }

   @Override
   public JMenuBar getMenuBar() {
      return this.usingMenu ? this.superMenu : null;
   }
}
