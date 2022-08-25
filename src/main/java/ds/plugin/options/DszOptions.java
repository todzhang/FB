package ds.plugin.options;

import ddb.detach.TabbableOption;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.dsz.plugin.Plugin;
import ddb.util.proxy.DszProxyHandler;
import ds.core.controller.MutableCoreController;
import ds.core.pluginevents.PluginEvent;
import ds.core.pluginevents.PluginEventListener;
import ds.gui.PluginWorkbench;
import ds.plugin.PluginContainer;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@DszLive(
   live = false,
   replay = false
)
@DszLogo("images/advanced-directory.png")
@DszName("Options")
@DszDescription("Configures DanderSpritz Options")
@DszUserStartable(false)
public class DszOptions extends NoHostAbstractPlugin implements Plugin, PluginEventListener {
   MutableCoreController client;
   OptionWorkbench pages;
   List<Plugin> plugins;
   private Map<Class<? extends Plugin>, TabbableOption> staticPluginToComponent;
   private Map<Plugin, TabbableOption> regularPluginToComponent;

   public DszOptions() {
      super.setCanClose(false);
      super.setName("Options");
      super.setShowButtons(false);
   }

   @Override
   protected final int init2() {
      this.plugins = new Vector();
      this.staticPluginToComponent = new Hashtable();
      this.regularPluginToComponent = new Hashtable();
      this.pages = new OptionWorkbench(this.core);
      super.setDisplay(this.pages);
      if (this.core instanceof MutableCoreController) {
         this.client = (MutableCoreController)this.core;
         this.client.addPluginEventListener(this);
      }

      this.setClient(this.core);
      return this.init3();
   }

   protected int init3() {
      return 0;
   }

   @Override
   protected final void fini2() {
      this.fini3();
      if (this.client != null) {
         this.client.removePluginEventListener(this);
      }

   }

   protected void fini3() {
   }

   public void setClient(CoreController var1) {
      if (var1 instanceof MutableCoreController) {
         this.client = (MutableCoreController)var1;
         this.subscribe(this.client.getWorkbench());
         this.client.addPluginEventListener(this);
      }

   }

   private void subscribe(PluginWorkbench var1) {
      for(int var2 = 0; var2 < var1.getChildPlugins().size(); ++var2) {
         this.addPlugin((Plugin)var1.getChildPlugins().get(var2));
      }

   }

   private void addPlugin(Plugin var1) {
      var1 = (Plugin)DszProxyHandler.Unwrap(var1);
      DszName var2 = (DszName)var1.getClass().getAnnotation(DszName.class);
      if (var1.getStaticOptions() != null && this.staticPluginToComponent.get(var1.getClass()) == null && var2 != null) {
         this.staticPluginToComponent.put(var1.getClass(), var1.getStaticOptions());
         this.pages.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var1.getStaticOptions()});
      }

      if (var1.getRegularOptions() != null && this.regularPluginToComponent.get(var1) == null && var1.getName() != null) {
         this.regularPluginToComponent.put(var1, var1.getRegularOptions());
         this.pages.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var1.getRegularOptions()});
      }

      if (var1 instanceof PluginContainer) {
         PluginContainer var3 = (PluginContainer)var1;
         this.subscribe(var3.getChildWorkbench());
      }

   }

   private void removePlugin(Plugin var1) {
      TabbableOption var2 = (TabbableOption)this.regularPluginToComponent.get(var1);
      if (var2 != null) {
         this.pages.enqueAction(WorkbenchAction.CLOSETAB, new Object[]{var2});
         this.pages.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{var2});
         this.regularPluginToComponent.remove(var1);
      }
   }

   public void pluginEvent(PluginEvent var1) {
      switch(var1.getState()) {
      case START:
         this.addPlugin(var1.getPlugin());
         break;
      case STOP:
         this.removePlugin(var1.getPlugin());
      }

   }

   public void setSelectedOption(Plugin var1) {
      if (var1.getStaticOptions() != null) {
         this.pages.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var1.getStaticOptions()});
      }

      if (var1.getRegularOptions() != null) {
         this.pages.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var1.getRegularOptions()});
      }

   }

   @Override
   public void setShowStatus(boolean showStatus) {
   }

   @Override
   public boolean isShowStatus() {
      return true;
   }
}
