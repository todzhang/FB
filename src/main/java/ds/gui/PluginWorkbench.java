package ds.gui;

import ddb.detach.Alignment;
import ddb.detach.Tabbable;
import ddb.detach.TabbableFrame;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Tabbable.TabState;
import ddb.detach.Workbench.DirectTabTask;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;
import ddb.imagemanager.ImageManager;
import ddb.util.JaxbCache;
import ddb.util.proxy.DszProxyHandler;
import ds.core.DSConstants;
import ds.jaxb.module.Module;
import ds.jaxb.module.ObjectFactory;
import ds.plugin.PluginContainer;
import ds.plugin.options.DszOptions;
import ds.plugin.pluginmanagement.PluginManager;
import ds.proxy.PluginProxyHandler;
import ds.proxy.QueuedInvocationHandler;
import ds.util.DszWorkbench;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

public class PluginWorkbench extends DszWorkbench implements ChangeListener {
   protected final Object lock = new Object();
   private PluginPopupListener pluginListener;
   private PluginManager manager;
   private List<Plugin> runningPlugins;
   private DefaultListModel localModel;
   private List<JMenuItem> menus;
   private List<PluginWorkbench.ModuleMetaData> plugins;
   private CoreController core;
   private PluginWorkbench parent = null;
   private Map<String, Integer> pluginNameCount = new Hashtable();

   public List<JMenuItem> getNewInstance() {
      return this.menus;
   }

   public PluginWorkbench(CoreController var1, PluginWorkbench var2) {
      super(var1.getSystemLogger(), DSConstants.MAIN_TAB_ALIGNMENT);
      this.initialize(var1, var2);
   }

   public PluginWorkbench(CoreController core, PluginWorkbench workbench, int tabPlacement) {
      super(core.getSystemLogger(), tabPlacement);
      this.initialize(core, workbench);
   }

   public PluginWorkbench(CoreController core, PluginWorkbench workbench, int tabPlacement, int tabLayoutPolicy) {
      super(core.getSystemLogger(), tabPlacement, tabLayoutPolicy);
      this.initialize(core, workbench);
   }

   @Override
   protected TabbableFrame generateTabFrame(Tabbable tabbable, Dimension dimension, Point point) {
      return new PluginFrame(tabbable, this, dimension, point, this.core);
   }

   private void initialize(CoreController core, PluginWorkbench workbench) {
      this.actionHandler.put(WorkbenchAction.HIDETAB, PluginWorkbench.HideTab.class);
      this.actionHandler.put(WorkbenchAction.CLOSETAB, PluginWorkbench.CloseTab.class);
      this.actionHandler.put(WorkbenchAction.UNHIDETAB, PluginWorkbench.UnhideTab.class);
      this.actionHandler.put(WorkbenchAction.DETACHTAB, PluginWorkbench.DetachTab.class);
      this.actionHandler.put(WorkbenchAction.PREDETACHTAB, PluginWorkbench.PredetachTab.class);
      this.actionHandler.put(WorkbenchAction.TABBIFYTAB, PluginWorkbench.TabbifyTab.class);
      this.actionHandler.put(WorkbenchAction.RENAMETAB, PluginWorkbench.RenameTab.class);
      this.actionHandler.put(WorkbenchAction.SETSELECTEDTAB, PluginWorkbench.SetSelectedTab.class);
      this.actionHandler.put(WorkbenchAction.STEALFOCUS, PluginWorkbench.StealFocus.class);
      this.actionHandler.put(WorkbenchAction.SETSTATE, PluginWorkbench.SetState.class);
      this.actionHandler.put(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, PluginWorkbench.StopPlugin.class);
      this.actionHandler.put(PluginWorkbench.PluginWorkbenchAction.DETACHPLUGIN, PluginWorkbench.DetachPlugin.class);
      this.actionHandler.put(PluginWorkbench.PluginWorkbenchAction.PREDETACHPLUGIN, PluginWorkbench.PredetachPlugin.class);
      if (this.pluginListener == null) {
         this.pluginListener = new PluginPopupListener(this);
      }

      this.pluginListener.setCoreController(core);
      this.core = core;
      this.parent = workbench;
      this.localModel = new DefaultListModel();
      this.runningPlugins = new Vector();
      this.menus = new Vector();
      this.plugins = new Vector();
      this.setupMenu();
      this.addChangeListener(this);
   }

   private List<PluginWorkbench.ModuleMetaData> getPluginsViaXml(String var1) {
      Vector var2 = new Vector();

      Unmarshaller var3;
      try {
         JAXBContext var4 = JaxbCache.getContext(ObjectFactory.class);
         var3 = var4.createUnmarshaller();
      } catch (Exception var15) {
         this.core.logEvent(Level.SEVERE, "Unable to find plugins!", var15);
         return var2;
      }

      Vector var16 = new Vector();
      var16.add(new File(this.core.getResourceDirectory()));
      var16.addAll(Arrays.asList((new File(this.core.getResourceDirectory())).listFiles()));
      Iterator var5 = var16.iterator();

      while(true) {
         File var6;
         File var7;
         do {
            do {
               do {
                  if (!var5.hasNext()) {
                     return var2;
                  }

                  var6 = (File)var5.next();
               } while(!var6.isDirectory());

               var7 = new File(var6, var1);
            } while(!var7.exists());
         } while(!var7.isDirectory());

         File[] var8 = var7.listFiles();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            File var11 = var8[var10];
            if (!var11.isDirectory()) {
               try {
                  Object var12 = var3.unmarshal(var11);
                  if (var12 instanceof Module) {
                     var2.add(new PluginWorkbench.ModuleMetaData((Module)var12, var6.getName()));
                  } else if (var12 instanceof JAXBElement) {
                     JAXBElement var13 = (JAXBElement)var12;
                     if (var13.getValue() instanceof Module) {
                        var2.add(new PluginWorkbench.ModuleMetaData((Module)var13.getValue(), var6.getName()));
                     }
                  } else {
                     System.err.println("Invalid: " + var11.getName());
                  }
               } catch (Exception var14) {
                  System.err.println("Error: " + var11.getName());
                  this.core.logEvent(Level.INFO, "Invalid plugin description: " + var11.getName(), var14);
               }
            }
         }
      }
   }

   private void setupMenu() {
      this.plugins.addAll(this.getPluginsViaXml("/Gui/Config/Modules"));
      this.plugins.addAll(this.getPluginsViaXml("/Gui/Config/Macros"));
      Collections.sort(this.plugins, new Comparator<PluginWorkbench.ModuleMetaData>() {
         @Override
         public int compare(PluginWorkbench.ModuleMetaData var1, PluginWorkbench.ModuleMetaData var2) {
            return var1.module.getName().compareTo(var2.module.getName());
         }
      });
      JMenu var1 = new JMenu("Plugins");
      this.iterateAndAdd(this.plugins.iterator(), var1);
      var1.setIcon(ImageManager.getIcon("images/new window.png", this.core.getLabelImageSize()));
      this.menus.add(var1);
   }

   private void iterateAndAdd(Iterator<PluginWorkbench.ModuleMetaData> iterator, JMenu jMenu) {
      while(iterator.hasNext()) {
         final PluginWorkbench.ModuleMetaData var3 = (PluginWorkbench.ModuleMetaData)iterator.next();
         final Module var4 = var3.module;
         if (var4.isUserStartable()) {
            if (this.core.isLiveOperation()) {
               if (!var4.isLive()) {
                  continue;
               }
            } else if (!var4.isReplay()) {
               continue;
            }

            final JMenuItem var5;
            if (!var3.project.equals("Dsz") && !var3.project.equals("Resources") && !var3.project.equals(".")) {
               var5 = new JMenuItem(String.format("%s (%s)", var4.getName(), var3.project));
            } else {
               var5 = new JMenuItem(var4.getName());
            }

            AbstractAction var6 = new AbstractAction() {
               public void actionPerformed(ActionEvent var1) {
                  PluginWorkbench.this.core.submit(new Runnable() {
                     public void run() {
                        try {
                           Plugin var1 = PluginWorkbench.this.startPlugin(Class.forName(var4.getClazz(), false, DSConstants.getClassLoader()), var4.getName(), var4.getInitArgs(), (Alignment)Alignment.CENTER, (String)null);
                           var1.setLogo(var4.getLogo());
                           PluginWorkbench.this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var1});
                           PluginWorkbench.this.setIconAt(PluginWorkbench.this.indexOfComponent(var1.getDisplay()), ImageManager.getIcon(var4.getLogo(), PluginWorkbench.this.core.getTabImageSize()));
                        } catch (Exception var2) {
                           PluginWorkbench.this.core.logEvent(Level.WARNING, "Unable to create new plugin", var2);
                        }

                     }
                  });
               }
            };
            var5.addActionListener(var6);
            jMenu.addMenuListener(new MenuListener() {
               final Class<?> clazz;

               {
                  Class var4 = null;

                  try {
                     var4 = Class.forName(var3.module.getClazz());
                  } catch (Throwable var6) {
                     System.err.println("Failed to load " + var3.module.getClazz());
                     var6.printStackTrace();
                  }

                  this.clazz = var4;
               }

               public void menuSelected(MenuEvent var1) {
                  var5.setVisible(PluginWorkbench.this.core.allowNewInstance(this.clazz));
               }

               public void menuDeselected(MenuEvent var1) {
               }

               public void menuCanceled(MenuEvent var1) {
               }
            });
            String var7 = var4.getLogo();
            if (var7 != null) {
               var5.setIcon(ImageManager.getIcon(var7, this.core.getLabelImageSize()));
            }

            String var8 = var4.getDescription();
            if (var8 != null) {
               var5.setToolTipText(var8);
            }

            jMenu.add(var5);
         }
      }

   }

   protected final void configuredStartedPlugin(Class<?> var1, Plugin plugin) {
      Module var3 = null;
      Iterator var4 = this.plugins.iterator();

      while(var4.hasNext()) {
         PluginWorkbench.ModuleMetaData var5 = (PluginWorkbench.ModuleMetaData)var4.next();
         if (var5.module.getClazz().equals(plugin.getClazz()) && !var5.module.isMacro()) {
            var3 = var5.module;
         }
      }

      if (var3 != null) {
         plugin.setName(var3.getName());
         plugin.setShortDescription(var3.getDescription());
         plugin.setLogo(var3.getLogo(), this.getTabImageSize());
         plugin.setHideable(var3.isHide());
         plugin.setUnhideable(var3.isUnhide());
         plugin.setDetachable(var3.isDetach());
         plugin.setUserClosable(var3.isUserClose());
         plugin.setCanClose(var3.isCanClose());
         plugin.setVerifyClose(var3.isVerifyClose());
      }
   }

   public Plugin startPlugin(Class<?> var1, String var2, List<String> var3, Alignment var4, String var5) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException {
      return this.startPlugin(var1, var2, var3, var4, var5, false);
   }

   public Plugin startPlugin(Class<?> pluginClazz, String name, List<String> args, Alignment alignment, String icon, boolean withCount) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException {
      if (!this.core.allowNewInstance(pluginClazz)) {
         this.core.logEvent(Level.WARNING, "Request for multiple plugins of type " + pluginClazz.getName());
         throw new InstantiationException("Existing plugin of class " + pluginClazz.getName() + " forbids multiple instantiations");
      } else {
         this.core.logEvent(Level.FINEST, "Starting New Plugin - " + pluginClazz.getName());
         Plugin plugin = QueuedInvocationHandler.newInstance(PluginProxyHandler.newInstance((Plugin)pluginClazz.newInstance(), this.core));
         this.configuredStartedPlugin(pluginClazz, plugin);
         plugin.setAlignment(alignment);
         plugin.setWorkbench(this);
         Integer count = this.pluginNameCount.get(name);
         if(count == null) {
            count = 0;
         }
         count = count + 1;

         this.pluginNameCount.put(name, count);

         if (withCount && count > 1) {
            name = String.format("%s %d", name, count);
         }

         if (name != null) {
            plugin.setName(name);
         }

         if (icon != null) {
            plugin.setLogo(icon);
         }

         int ret;
         try {
            ret = plugin.init(this.core, this, args);
         } catch (Exception e) {
            e.printStackTrace();
            this.core.logEvent(Level.WARNING, "Could not init plugin", e);
            throw new InstantiationException("Exception during " + pluginClazz.getName() + " plugin init");
         }

         if (ret != 0) {
            this.core.logEvent(Level.WARNING, "Plugin " + pluginClazz.getName() + " indicated failure during init.  Discontinuing plugin load.");
            return null;
         } else {
            synchronized(this.lock) {
               this.runningPlugins.add(plugin);
               this.localModel.addElement(plugin);
               this.invokeAction(WorkbenchAction.ADDNEWTAB, new Object[]{plugin});
               this.core.addCommandEventListener(plugin);
               this.core.addConnectionChangeListener(plugin);
               this.core.addInternalCommandHandler(plugin);
            }

            this.core.pluginStarted(plugin);
            return plugin;
         }
      }
   }

   public Plugin startPlugin(Class<?> var1, String var2, List<String> var3, String var4, String var5) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException {
      return this.startPlugin(var1, var2, var3, Alignment.getAlignment(var4), var5);
   }

   public void stopAllPlugins() {
      if (!SwingUtilities.isEventDispatchThread()) {
         try {
            final CyclicBarrier var1 = new CyclicBarrier(2);
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  PluginWorkbench.this.stopAllPlugins();

                  try {
                     var1.await();
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

               }
            });
            var1.await();

            while(this.runningPlugins.size() > 0) {
               try {
                  TimeUnit.MILLISECONDS.sleep(50L);
               } catch (Exception var4) {
               }
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      } else {
         this.core.logEvent(Level.FINEST, "Stopping all plugins");
         synchronized(this.lock) {
            for(int var2 = this.runningPlugins.size() - 1; var2 >= 0; --var2) {
               this.invokeAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{this.runningPlugins.get(var2)});
            }

         }
      }
   }

   public boolean allowNewInstance(Class<?> var1) {
      synchronized(this.lock) {
         Iterator var3 = this.runningPlugins.iterator();

         Plugin var4;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            var4 = (Plugin)var3.next();
         } while(var4.allowNewInstance(var1));

         return false;
      }
   }

   @Override
   public JPopupMenu getMenu() {
      return new PluginWorkbenchPopupMenu(this);
   }

   public DefaultListModel getChildPlugins() {
      return this.localModel;
   }

   public void showRunningPluginsList() {
      try {
         if (this.manager == null) {
            Plugin var1 = this.startPlugin(PluginManager.class, "Running Plugins", new Vector(), (Alignment)Alignment.RIGHT, (String)null);
            if (var1 instanceof PluginManager) {
               this.manager = (PluginManager)var1;
               this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{this.manager});
            } else {
               this.enqueAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{var1});
            }
         } else {
            this.enqueAction(WorkbenchAction.UNHIDETAB, new Object[]{this.manager});
            this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{this.manager});
         }
      } catch (Exception var2) {
         this.core.logEvent(Level.SEVERE, "Unable to display running plugins", var2);
      }

   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      if (this.pluginListener == null) {
         this.pluginListener = new PluginPopupListener(this);
      }

      return this.pluginListener;
   }

   public PluginWorkbench getHighestWorkbench() {
      return this.parent == null ? this : this.parent.getHighestWorkbench();
   }

   public void showOptions(Plugin var1) {
      if (this.parent != null) {
         this.parent.showOptions(var1);
      } else {
         Tabbable var2 = super.getTabbableByClass(DszOptions.class);
         if (var2 instanceof DszOptions) {
            DszOptions var3 = (DszOptions)DszOptions.class.cast(var2);
            this.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var3});
            var3.setSelectedOption(var1);
         }
      }

   }

   @Override
   public String getTitleAt(int index) {
      Component var2 = this.getComponentAt(index);
      if (var2 != null) {
         Tabbable var3 = super.getTabbableForDisplay(var2);
         if (var3 != null) {
            return var3.getDockedTitle();
         }
      }

      return super.getTitleAt(index);
   }

   protected boolean compareForClass(Tabbable tab1, Class<? extends Tabbable> tab2) {
      return super.compareForClass(tab1, tab2);
   }

   public void setParent(PluginWorkbench var1) {
      this.parent = var1;
   }

   public boolean pluginIsVisible(Plugin var1) {
      return super.pluginIsVisible(var1);
   }

   public boolean pluginIsHidden(Plugin var1) {
      return super.pluginIsHidden(var1);
   }

   public void setFocusOnDefaultElement() {
      Tabbable var1 = this.getTabbableForDisplay(this.getSelectedComponent());
      if (var1 != null) {
         JComponent var2 = var1.getDefaultElement();
         if (var2 != null) {
            var2.requestFocusInWindow();
         }
      }

   }

   @Override
   public void titleChanged() {
      Vector var1 = new Vector();
      var1.addAll(this.runningPlugins);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Plugin var3 = (Plugin)var2.next();
         TabbableFrame var4 = var3.getFrame();
         if (var4 != null) {
            var4.setTitle(var3.getDetachedTitle());
         }

         if (var3 instanceof PluginContainer) {
            ((PluginContainer)PluginContainer.class.cast(var3)).getWorkbench().titleChanged();
         }
      }

   }

   @Override
   public Dimension getLabelImageSize() {
      return this.core.getLabelImageSize();
   }

   @Override
   public Dimension getTabImageSize() {
      return this.core.getTabImageSize();
   }

   protected class PredetachPlugin extends PluginWorkbench.PredetachTab {
      public PredetachPlugin(Tabbable var2, Dimension var3, Point var4) {
         super(var2, var3, var4);
      }

      public void runTask() {
         if (this.size != null && this.position != null) {
            super.runTask();
         }
      }
   }

   protected class DetachPlugin extends PluginWorkbench.DetachTab {
      public DetachPlugin(Tabbable var2, Dimension var3, Point var4) {
         super(var2, var3, var4);
      }

      public void runTask() {
         super.runTask();
         if (this.tab.getFrame() != null && this.size != null) {
            this.tab.getFrame().setSize(this.size);
         }

      }
   }

   protected class StopPlugin extends DirectTabTask {
      Thread th = null;
      boolean started = false;
      boolean killed = false;

      public StopPlugin(Plugin var2) {
         super(var2);
      }

      public void runTask() {
         if (!this.started) {
            this.started = true;
            this.th = new Thread(new Runnable() {
               public void run() {
                  Plugin var1 = (Plugin)StopPlugin.this.tab;
                  PluginWorkbench.this.core.logEvent(Level.FINEST, "Stopping plugin: " + var1.toString());
                  var1.fini();
                  StopPlugin.this.killed = true;
               }
            }, "Stopping " + this.tab.toString());
            this.th.setDaemon(true);
            this.th.setPriority(1);
            this.th.start();
            EventQueue.invokeLater(this);
         } else if (!this.killed) {
            try {
               TimeUnit.MILLISECONDS.sleep(50L);
            } catch (Exception var7) {
            }

            EventQueue.invokeLater(this);
         } else {
            Plugin var1 = (Plugin)this.tab;
            if (var1.getFrame() != null) {
               TabbableFrame var2 = var1.getFrame();
               var1.setFrame((TabbableFrame)null);
               var2.setVisible(false);
               var2.dispose();
            }

            synchronized(PluginWorkbench.this.lock) {
               Object var3 = var1;

               while(true) {
                  if (var3 != null) {
                     Object var4 = DszProxyHandler.GetHandler(var3);
                     if (var4 instanceof DszProxyHandler) {
                        ((DszProxyHandler)DszProxyHandler.class.cast(var4)).stop();
                     }

                     Object var5 = DszProxyHandler.Unwrap(var3, false);
                     if (var5 != var3) {
                        var3 = var5;
                        continue;
                     }
                  }

                  var1 = (Plugin)DszProxyHandler.Unwrap(var1);
                  PluginWorkbench.this.runningPlugins.remove(var1);
                  PluginWorkbench.this.localModel.removeElement(var1);
                  PluginWorkbench.this.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{var1});
                  PluginWorkbench.this.core.removeCommandEventListener(var1);
                  PluginWorkbench.this.core.removeConnectionChangeListener(var1);
                  PluginWorkbench.this.core.removeInternalCommandHandler(var1);
                  if (PluginWorkbench.this.manager == var1) {
                     PluginWorkbench.this.manager = null;
                  }
                  break;
               }
            }

            PluginWorkbench.this.core.pluginStopped(var1);
         }
      }
   }

   protected class SetState extends ddb.detach.Workbench.SetState {
      public SetState(Tabbable var2, TabState var3) {
         super( var2, var3);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class SetSelectedTab extends ddb.detach.Workbench.SetSelectedTab {
      public SetSelectedTab(Tabbable var2) {
         super(var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class RenameTab extends ddb.detach.Workbench.RenameTab {
      public RenameTab(Tabbable var2) {
         super(var2);
      }

      public RenameTab(Tabbable var2, String var3) {
         super( var2, var3);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class TabbifyTab extends ddb.detach.Workbench.TabbifyTab {
      public TabbifyTab(Tabbable var2) {
         super(var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class PredetachTab extends ddb.detach.Workbench.PredetachTab {
      public PredetachTab(Tabbable var2, Dimension var3, Point var4) {
         super(var2, var3, var4);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class CloseTab extends ddb.detach.Workbench.CloseTab {
      public CloseTab(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         PluginWorkbench.this.core.logEvent(Level.INFO, "Closing plugin: " + this.tab.toString());
         if (this.tab instanceof Plugin) {
            PluginWorkbench.this.invokeAction(PluginWorkbench.PluginWorkbenchAction.STOPPLUGIN, new Object[]{this.tab});
         }

         this.tab.hideFrame();
      }
   }

   protected class DetachTab extends ddb.detach.Workbench.DetachTab {
      public DetachTab(Tabbable var2, Dimension var3, Point var4) {
         super( var2, var3, var4);
      }

      public void runTask() {
         super.runTask();
         this.tab.setDetached();
      }
   }

   protected class UnhideTab extends ddb.detach.Workbench.UnhideTab {
      public UnhideTab(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class HideTab extends ddb.detach.Workbench.HideTab {
      public HideTab(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class StealFocus extends ddb.detach.Workbench.StealFocus {
      public StealFocus(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   public class ModuleMetaData {
      public Module module;
      public String project;

      ModuleMetaData(Module var2, String var3) {
         this.module = var2;
         this.project = var3;
      }
   }

   public static enum PluginWorkbenchAction {
      STOPPLUGIN,
      DETACHPLUGIN,
      PREDETACHPLUGIN;
   }
}
