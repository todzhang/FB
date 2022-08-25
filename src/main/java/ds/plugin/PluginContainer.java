package ds.plugin;

import ddb.detach.Alignment;
import ddb.detach.Tabbable;
import ddb.detach.TabbableStatus;
import ddb.detach.Workbench;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.dsz.plugin.Plugin;
import ddb.util.PluginInitInfo;
import ddb.util.StartupConfigParser;
import ds.core.controller.MutableCoreController;
import ds.gui.PluginWorkbench;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

public abstract class PluginContainer extends NoHostAbstractPlugin implements InternalCommandHandler, InvocationHandler {
   static final Map<Method, Method> redirectAction = new HashMap();
   private MutableCoreController mcc = null;
   private PluginWorkbench parentBench;
   EventListenerList listeners = new EventListenerList();

   private static final void addRedirect(String var0, Class<?>[] var1, Class<?> var2) {
      try {
         Method var3 = var2.getDeclaredMethod(var0, var1);
         Method var6 = PluginContainer.class.getDeclaredMethod(var0, var1);
         if (var3 != null && var6 != null) {
            redirectAction.put(var3, var6);
         } else {
            System.out.printf("Couldn't find %s in %s\n", var0, var2.toString());
         }
      } catch (Throwable var5) {
         LogRecord var4 = new LogRecord(Level.SEVERE, var5.getMessage());
         var4.setSourceClassName("PluginContainer");
         var4.setSourceMethodName("addRedirect");
         var4.setThrown(var5);
         Logger.getLogger("dsz.core").log(var4);
      }

   }

   public abstract PluginWorkbench getChildWorkbench();

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         try {
            Method var4 = (Method)redirectAction.get(method);
            Object var5;
            if (var4 != null) {
               var5 = var4.invoke(this, args);
               return var5;
            }

            var5 = method.invoke(this.mcc, args);
            return var5;
         } catch (InvocationTargetException var10) {
            this.core.logEvent(Level.SEVERE, var10.getMessage(), var10.getTargetException());
         } catch (Exception var11) {
            this.core.logEvent(Level.SEVERE, var11.getMessage(), var11);
         }

         return null;
      } finally {
         ;
      }
   }

   @Override
   protected final int init2() {
      if (this.core instanceof MutableCoreController) {
         this.mcc = MutableCoreController.class.cast(this.core);
      }

      int var1 = this.init3();
      PluginWorkbench var2 = this.getChildWorkbench();
      if (var2 != null) {
         var2.addPropertyChangeListener("WORKBENCH_CONTENTS_CHANGED", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent var1) {
               PluginContainer.this.contentsChanged();
            }
         });
      }

      return var1;
   }

   protected abstract int init3();

   @Override
   protected final void fini2() {
      this.fini3();
   }

   protected void fini3() {
   }

   protected CoreController decorate(CoreController var1) {
      return (var1 instanceof MutableCoreController ? (MutableCoreController)Proxy.newProxyInstance(CoreController.class.getClassLoader(), new Class[]{MutableCoreController.class}, this) : (CoreController)Proxy.newProxyInstance(CoreController.class.getClassLoader(), new Class[]{CoreController.class}, this));
   }

   @Override
   protected final boolean parseArgument2(String var1, String var2) {
      if (var1.equals("-load")) {
         InputStream var3 = this.getClass().getClassLoader().getResourceAsStream(var2);
         if (var3 == null) {
            this.core.logEvent(Level.SEVERE, "Invalid configuration file");
            return false;
         } else {
            List var4 = null;

            try {
               var4 = StartupConfigParser.parse(var3, this.core.getLogDirectory(), String.format("%s/%s", this.core.getResourceDirectory(), this.core.getDefaultPackage()), this.core.getBuildType());
            } catch (Exception var7) {
               this.core.logEvent(Level.SEVERE, var7.getMessage(), var7);
               return false;
            }

            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               PluginInitInfo var6 = (PluginInitInfo)var5.next();
               this.addSubPlugin(var6.getClassName(), var6.getInitArgs(), var6.getInstanceName(), var6.getAlign());
            }

            if (this.getChildWorkbench().getTabCount() > 0) {
               this.getChildWorkbench().enqueAction(WorkbenchAction.SETSELECTEDINDEX, new Object[]{0});
            }

            return true;
         }
      } else {
         if (var1.equals("-align") && var2 != null) {
            if (var2.equalsIgnoreCase("top")) {
               this.getChildWorkbench().setTabPlacement(1);
            } else if (var2.equalsIgnoreCase("bottom")) {
               this.getChildWorkbench().setTabPlacement(3);
            } else if (var2.equalsIgnoreCase("left")) {
               this.getChildWorkbench().setTabPlacement(2);
            } else if (var2.equalsIgnoreCase("right")) {
               this.getChildWorkbench().setTabPlacement(4);
            } else {
               this.core.logEvent(Level.WARNING, String.format("%s is not valid for %s:  'top, bottom, left, right' are valid values", var2, var1));
            }
         } else if (var1.equals("-layout") && var2 != null) {
            if (var2.equalsIgnoreCase("scroll")) {
               this.getChildWorkbench().setTabLayoutPolicy(1);
            } else if (var2.equalsIgnoreCase("wrap")) {
               this.getChildWorkbench().setTabLayoutPolicy(0);
            } else {
               this.core.logEvent(Level.WARNING, String.format("%s is not valid for %s:  'scroll, wrap' are valid values", var2, var1));
            }
         }

         return this.parseArgument3(var1, var2);
      }
   }

   protected boolean parseArgument3(String var1, String var2) {
      return false;
   }

   public PluginWorkbench getParentWorkbench() {
      return this.parentBench;
   }

   @Override
   public TabbableStatus getStatus() {
      return this.getChildWorkbench().getStatus();
   }

   @Override
   public JComponent getDefaultElement() {
      Tabbable var1 = this.getChildWorkbench().getCurrentTab();
      if (var1 != null) {
         return var1.getDefaultElement();
      } else {
         Component var2 = this.getChildWorkbench().getSelectedComponent();
         if (var2 != null) {
            Tabbable var3 = this.getChildWorkbench().getTabbableForDisplay(var2);
            if (var3 != null) {
               return var3.getDefaultElement();
            }
         }

         return null;
      }
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      Iterator var2 = this.getChildWorkbench().getTabs().iterator();

      Tabbable var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Tabbable)var2.next();
      } while(var3.allowNewInstance(clazz));

      return false;
   }

   public Plugin getPluginForDisplay(Component var1) {
      return (Plugin)this.getChildWorkbench().getTabbableForDisplay(var1);
   }

   @Override
   public void receivedFocus() {
      super.receivedFocus();
      Plugin var1 = this.getPluginForDisplay(this.getChildWorkbench().getSelectedComponent());
      if (var1 != null) {
         var1.receivedFocus();
      }

   }

   private Plugin addSubPlugin(String var1, List<String> var2, String var3, String var4) {
      Class var5;
      try {
         var5 = Class.forName(var1, false, this.getClass().getClassLoader());
      } catch (Throwable var8) {
         System.err.println("Unable to instantiate " + var1);
         this.core.logEvent(Level.WARNING, "Unable to find plugin", var8);
         return null;
      }

      if (var5 == null) {
         return null;
      } else {
         try {
            Plugin var6 = this.getChildWorkbench().startPlugin(var5, var3, var2, Alignment.getAlignment(var4), (String)null, true);
            return var6;
         } catch (Throwable var7) {
            System.err.println("Unable to instantiate " + var1);
            this.core.logEvent(Level.WARNING, "Unable to find plugin", var7);
            return null;
         }
      }
   }

   @Override
   public void setSelected(boolean selected) {
      super.setSelected(selected);
      this.getChildWorkbench().setSelected(selected);
   }

   @Override
   public HostInfo getTarget() {
      Component var1 = this.getChildWorkbench().getSelectedComponent();
      if (var1 != null) {
         Tabbable var2 = this.getChildWorkbench().getTabbableForDisplay(var1);
         if (var2 instanceof Plugin) {
            return ((Plugin)Plugin.class.cast(var2)).getTarget();
         }
      }

      return null;
   }

   public void unhidePlugin(Plugin plugin) {
      this.core.unhidePlugin(this);
      this.getChildWorkbench().enqueAction(WorkbenchAction.UNHIDETAB, new Object[]{plugin});
   }

   public void hidePlugin(Plugin plugin) {
      this.getChildWorkbench().enqueAction(WorkbenchAction.HIDETAB, new Object[]{plugin});
   }

   public void detachPlugin(Plugin plugin, Dimension dimension, Point point) {
      this.getChildWorkbench().enqueAction(PluginWorkbench.PluginWorkbenchAction.DETACHPLUGIN, new Object[]{plugin, dimension, point});
   }

   @Override
   public boolean isUserClosable() {
      return this.isClosable();
   }

   @Override
   public boolean isClosable() {
      Iterator var1 = this.getChildWorkbench().getTabs().iterator();

      Tabbable var2;
      do {
         if (!var1.hasNext()) {
            return super.isClosable();
         }

         var2 = (Tabbable)var1.next();
      } while(var2.isClosable());

      return false;
   }

   public boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5) {
      return this.startNewPlugin(var1, var2, var3, this.detached, var5, Alignment.DEFAULT);
   }

   public boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5, Alignment var6) {
      if (var6 == Alignment.DEFAULT) {
         var6 = Alignment.CENTER;
      }

      Plugin var7 = null;

      try {
         var7 = this.getChildWorkbench().startPlugin(var1, var2, var3, var6, (String)null, var5);
      } catch (Exception var9) {
         this.core.logEvent(Level.SEVERE, var9.getMessage(), var9);
         return false;
      }

      if (var7 == null) {
         this.core.logEvent(Level.WARNING, "Failed to add plugin of class" + var1);
         return false;
      } else {
         if (var4) {
            this.getChildWorkbench().enqueAction(WorkbenchAction.DETACHTAB, new Object[]{var7, null, null});
         }

         return true;
      }
   }

   public void closePlugin(Plugin var1) {
      this.getChildWorkbench().enqueAction(WorkbenchAction.CLOSETAB, new Object[]{var1});
   }

   @Override
   public boolean isHideable() {
      Iterator var1 = this.getChildWorkbench().getTabs().iterator();

      Tabbable var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (Tabbable)var1.next();
      } while(var2.isHideable());

      return false;
   }

   @Override
   public void setWorkbench(Workbench workbench) {
      super.setWorkbench(workbench);
      if (workbench instanceof PluginWorkbench) {
         this.parentBench = (PluginWorkbench)PluginWorkbench.class.cast(workbench);
         PluginWorkbench var2 = this.getChildWorkbench();
         if (var2 != null) {
            var2.setParent((PluginWorkbench)PluginWorkbench.class.cast(workbench));
         }
      }

   }

   public void StealFocus(Plugin plugin) {
      this.core.StealFocus(this);
      this.getChildWorkbench().enqueAction(WorkbenchAction.STEALFOCUS, new Object[]{plugin});
   }

   public boolean awaitTermination(long var1, TimeUnit timeUnit) throws InterruptedException {
      return this.awaitTermination(var1, timeUnit);
   }

   public boolean isTerminated() {
      return this.isTerminated();
   }

   public boolean isShutdown() {
      return this.isShutdown();
   }

   static {
      addRedirect("unhidePlugin", new Class[]{Plugin.class}, CoreController.class);
      addRedirect("hidePlugin", new Class[]{Plugin.class}, CoreController.class);
      addRedirect("detachPlugin", new Class[]{Plugin.class, Dimension.class, Point.class}, CoreController.class);
      addRedirect("startNewPlugin", new Class[]{Class.class, String.class, List.class, Boolean.TYPE, Boolean.TYPE}, CoreController.class);
      addRedirect("startNewPlugin", new Class[]{Class.class, String.class, List.class, Boolean.TYPE, Boolean.TYPE, Alignment.class}, CoreController.class);
      addRedirect("closePlugin", new Class[]{Plugin.class}, CoreController.class);
      addRedirect("StealFocus", new Class[]{Plugin.class}, CoreController.class);
   }
}
