package ddb.detach;

import ddb.actions.tabnav.NavigationDirection;
import ddb.imagemanager.ImageManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections.Predicate;

public abstract class Workbench extends DnDTabbedPane implements Observer {
   public static final String WORKBENCH_CONTENTS_CHANGED = "WORKBENCH_CONTENTS_CHANGED";
   public static final String WORKBENCH_CONTENTS_CHANGED_REQUEST_FOCUS = "WORKBENCH_CONTENTS_CHANGED_REQUEST_FOCUS";
   public static final PropertyChangeListener UpdatedTab = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() instanceof AbstractTabbable) {
            AbstractTabbable var2 = (AbstractTabbable)var1.getSource();
            Workbench var3 = var2.getWorkbench();
            if (var2 != null && var3 != null) {
               if (var3.getSelectedComponent() == var2.getDisplay()) {
                  var2.setDisplayColor(Color.BLACK);
               } else {
                  var2.colorCycle = (var2.colorCycle + 1) % Workbench.colors.size();
                  var2.setDisplayColor((Color)Workbench.colors.get(var2.colorCycle));
               }

               if (var2.isHidden() && var2.wasTabbed()) {
                  var3.enqueAction(Workbench.WorkbenchAction.UNHIDETAB, var2);
               }

               var3.firePropertyChange("WORKBENCH_CONTENTS_CHANGED", false, true);
            }
         }

      }
   };
   public static final PropertyChangeListener UpdatedTabRequestFocus = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() instanceof AbstractTabbable) {
            Workbench.UpdatedTab.propertyChange(var1);
            AbstractTabbable var2 = (AbstractTabbable)var1.getSource();
            Workbench var3 = var2.getWorkbench();
            if (var2 != null && var3 != null) {
               var3.enqueAction(Workbench.WorkbenchAction.SETSELECTEDTAB, var2);
               var3.firePropertyChange("WORKBENCH_CONTENTS_CHANGED_REQUEST_FOCUS", false, true);
            }
         }

      }
   };
   public static final List<Color> colors = new Vector();
   private final Hashtable<JComponent, Tabbable> componentToPlugin = new Hashtable();
   protected Logger logger;
   public static final Runnable NULL;
   private StatusAlmalgum status = new StatusAlmalgum((TabbableStatus)null, (Tabbable)null);
   protected boolean selected;
   protected boolean allowWrap = false;
   protected Predicate isSelectedPred = o -> Workbench.this.isSelected();
   protected Map<Enum<?>, Class<? extends Workbench.TabTask>> actionHandler = new HashMap();
   protected final List<Tabbable> pluginsInOrder = new Vector();
   Tabbable currentTab = null;
   private static final Dimension DEFAULT_DIMENSION;

   public void invokeAction(Enum<?> var1, Object... var2) {
      this.create(var1, var2).run();
   }

   public void enqueAction(Enum<?> var1, Object... var2) {
      EventQueue.invokeLater(this.create(var1, var2));
   }

   protected final Runnable create(Enum<?> var1, Object... var2) {
      Class var3 = (Class)this.actionHandler.get(var1);
      if (var3 == null) {
         return NULL;
      } else {
         Object[] var4 = new Object[var2.length + 1];
         var4[0] = this;

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var4[var5 + 1] = var2[var5];
         }

         Constructor[] var12 = var3.getConstructors();
         int var6 = var12.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Constructor var8 = var12[var7];
            Class[] var9 = var8.getParameterTypes();
            if (var9.length == var4.length) {
               try {
                  return (Runnable)var8.newInstance(var4);
               } catch (Exception var11) {
               }
            }
         }

         return NULL;
      }
   }

   public Workbench(Logger logger) {
      this.logger = logger;
      this.initialize();
   }

   public Workbench(Logger logger, int tabPlacement) {
      super(tabPlacement);
      this.logger = logger;
      this.initialize();
   }

   public Workbench(Logger logger, int tabPlacement, int tabLayoutPolicy) {
      super(tabPlacement, tabLayoutPolicy);
      this.logger = logger;
      this.initialize();
   }

   public Workbench() {
      this.logger = Logger.getLogger("dsz.core");
      this.initialize();
   }

   public Workbench(int tabPlacement) {
      super(tabPlacement);
      this.logger = Logger.getLogger("dsz.core");
      this.initialize();
   }

   public Workbench(int tabPlacement, int tabLayoutPolicy) {
      super(tabPlacement, tabLayoutPolicy);
      this.logger = Logger.getLogger("dsz.core");
      this.initialize();
   }

   private void initialize() {
      this.addMouseListener(this.getTabPopupListener());
      this.actionHandler.put(Workbench.WorkbenchAction.DETACHTAB, Workbench.DetachTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.PREDETACHTAB, Workbench.PredetachTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.TABBIFYTAB, Workbench.TabbifyTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.HIDETAB, Workbench.HideTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.UNHIDETAB, Workbench.UnhideTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.RENAMETAB, Workbench.RenameTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.SETSELECTEDTAB, Workbench.SetSelectedTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.SETSELECTEDINDEX, Workbench.SetSelectedIndex.class);
      this.actionHandler.put(Workbench.WorkbenchAction.ADDNEWTAB, Workbench.AddNewTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.REMOVETAB, Workbench.RemoveTab.class);
      this.actionHandler.put(Workbench.WorkbenchAction.ADDTABTOWORKBENCH, Workbench.AddTabToWorkbench.class);
      this.actionHandler.put(Workbench.WorkbenchAction.STEALFOCUS, Workbench.StealFocus.class);
      this.actionHandler.put(Workbench.WorkbenchAction.SETSTATE, Workbench.SetState.class);
      this.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent var1) {
            TabbableStatus var2 = null;
            Tabbable var3 = Workbench.this.getTabbableForDisplay(Workbench.this.getSelectedComponent());
            if (var3 != null) {
               var2 = var3.getStatus();
               JComponent var4 = var3.getDefaultElement();
               if (var4 != null) {
                  var4.requestFocusInWindow();
               }

               var3.setDisplayColor(Color.BLACK);
            }

            Workbench.this.status.setDelegate(var2, var3);
         }
      });
   }

   protected Tabbable.TabState getState(Tabbable tabbable) {
      return tabbable == null ? Tabbable.TabState.UNKNOWN : tabbable.getCurrentState();
   }

   protected TabbableFrame getTabFrame(Tabbable tabbable, Dimension dimension, Point point) {
      if (tabbable == null) {
         return null;
      } else {
         if (dimension == null) {
            dimension = tabbable.getFrameSize();
         }

         if (dimension == null) {
            dimension = this.getDefaultSize();
         }

         if (point == null) {
            point = tabbable.getFrameLocation();
         }

         if (point == null) {
            point = new Point(0, 0);
         }

         TabbableFrame var4 = tabbable.getFrame();
         if (var4 == null) {
            var4 = this.generateTabFrame(tabbable, dimension, point);
            tabbable.setFrame(var4);
         } else {
            var4.setSize(dimension);
            var4.setLocation(point);
         }

         var4.setVisible(true);
         var4.requestFocus();
         return var4;
      }
   }

   protected Dimension getDefaultSize() {
      return DEFAULT_DIMENSION;
   }

   protected TabbableFrame generateTabFrame(Tabbable tabbable, Dimension dimension, Point point) {
      return new TabbableFrame(tabbable, this, dimension, point);
   }

   protected Tabbable.TabState getPrevState(Tabbable tabbable) {
      return tabbable.getPreviousState();
   }

   public List<Tabbable> getTabs() {
      Vector var1 = new Vector();
      synchronized(this.componentToPlugin) {
         var1.addAll(this.componentToPlugin.values());
      }

      Collections.sort(var1);
      return var1;
   }

   public Tabbable getTabbableForDisplay(Component component) {
      if (component == null) {
         return null;
      } else if (component instanceof JComponent) {
         synchronized(this.pluginsInOrder) {
            Iterator var3 = this.pluginsInOrder.iterator();

            while(var3.hasNext()) {
               Tabbable var4 = (Tabbable)var3.next();
               if (var4.getDisplay().equals(component)) {
                  return var4;
               }
            }
         }

         synchronized(this.componentToPlugin) {
            return this.componentToPlugin.get(JComponent.class.cast(component));
         }
      } else {
         return null;
      }
   }

   public TabbableStatus getStatus() {
      return this.status;
   }

   public JPopupMenu getMenu() {
      return null;
   }

   public TabbablePopupListener getTabPopupListener() {
      return new TabbablePopupListener(this);
   }

   public Dimension getTabImageSize() {
      return ImageManager.SIZE32;
   }

   public Dimension getLabelImageSize() {
      return ImageManager.SIZE16;
   }

   protected int compareTabs(Tabbable tab1, Tabbable tab2) {
      return tab1.getName().compareToIgnoreCase(tab2.getName());
   }

   public void contentsChanged() {
      this.firePropertyChange("WORKBENCH_CONTENTS_CHANGED", false, true);
   }

   protected Tabbable getTabbableByClass(Class<? extends Tabbable> tabbable) {
      synchronized(this.componentToPlugin) {
         Iterator var3 = this.componentToPlugin.values().iterator();

         Tabbable var4;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            var4 = (Tabbable)var3.next();
         } while(!this.compareForClass(var4, tabbable));

         return var4;
      }
   }

   protected boolean compareForClass(Tabbable tab1, Class<? extends Tabbable> tab2) {
      return tab1.getClass().equals(tab2);
   }

   public boolean pluginIsDetached(Tabbable tabbable) {
      return tabbable.isDetached() || tabbable.isHidden() && tabbable.wasDetached();
   }

   public boolean pluginIsVisible(Tabbable tabbable) {
      return !tabbable.isHidden();
   }

   public boolean pluginIsHidden(Tabbable tabbable) {
      return tabbable.isHidden();
   }

   @Override
   public void insertTab(String var1, Icon icon, Component component, String var4, int var5) {
      super.insertTab(String.format("<html><center>%s</center></html>", var1), icon, component, var4, var5);
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   protected boolean isSelected() {
      return this.selected;
   }

   @Override
   public void setSelectedComponent(Component c) {
      Tabbable var2 = this.getTabbableForDisplay(this.getSelectedComponent());
      if (var2 != null) {
         var2.setSelected(false);
      }

      super.setSelectedComponent(c);
      var2 = this.getTabbableForDisplay(this.getSelectedComponent());
      if (var2 != null) {
         var2.setSelected(true);
         this.currentTab = var2;
         if (c != null) {
            c.requestFocus();
         }

         var2.getDisplay().putClientProperty("windowModified", Boolean.FALSE);
      }

   }

   @Override
   public String getTitleAt(int index) {
      Component var2 = this.getComponentAt(index);
      if (var2 != null) {
         Tabbable var3 = this.getTabbableForDisplay(var2);
         if (var3 != null) {
            return var3.getDockedTitle();
         }
      }

      return super.getTitleAt(index);
   }

   @Override
   public void setSelectedIndex(int index) {
      Tabbable var2 = this.getTabbableForDisplay(this.getSelectedComponent());
      if (var2 != null) {
         var2.setSelected(false);
      }

      super.setSelectedIndex(index);
      var2 = this.getTabbableForDisplay(this.getSelectedComponent());
      if (var2 != null) {
         var2.setSelected(true);
         this.currentTab = var2;
         JComponent var3 = var2.getDefaultElement();
         if (var3 != null) {
            var3.requestFocus();
         }

         var2.getDisplay().putClientProperty("windowModified", Boolean.FALSE);
      }

   }

   protected boolean doesAllowWrap() {
      return this.allowWrap;
   }

   public void setAllowWrap(boolean var1) {
      this.allowWrap = var1;
   }

   public boolean navigate(NavigationDirection navigationDirection, boolean var2, boolean var3) {
      Tabbable var5 = null;
      int var6 = super.getSelectedIndex();
      switch(navigationDirection) {
      case NEXT:
         ++var6;
         break;
      case PREVIOUS:
         --var6;
      }

      if (var6 >= this.getTabCount() && var2) {
         var6 %= this.getTabCount();
      }

      if (var6 < 0 && var2) {
         var6 = this.getTabCount() - 1;
      }

      if (var6 >= 0 && var6 < this.getTabCount()) {
         Component var4 = super.getComponentAt(var6);
         var5 = this.getTabbableForDisplay(var4);
      }

      if (var5 == null) {
         return var3 ? this.punt(navigationDirection, var2) : false;
      } else {
         if (var5.isTabbed()) {
            this.setSelectedComponent(var5.getDisplay());
            this.currentTab = var5;
            JComponent var7 = this.currentTab.getDefaultElement();
            if (var7 == null) {
               var7 = this.currentTab.getDisplay();
            }

            this.ascend(var7);
            if (var7 != null) {
               var7.requestFocus();
            }
         } else if (var5.isDetached()) {
            this.ascend(var5.getDefaultElement());
            Object var9 = var5.getDefaultElement();
            if (var9 == null) {
               var9 = var5.getFrame().getRootPane();
            }

            Object var8;
            for(var8 = var9; var8 != null && !(var8 instanceof JFrame); var8 = ((Component)var8).getParent()) {
            }

            if (var8 instanceof JFrame) {
               ((JFrame)JFrame.class.cast(var8)).requestFocus();
            }

            if (var9 != null) {
               ((Component)var9).requestFocusInWindow();
            }
         }

         return true;
      }
   }

   protected boolean punt(NavigationDirection var1, boolean var2) {
      Container var3 = this.getParent();

      Container var4;
      for(var4 = null; var3 != null && !(var3 instanceof JFrame); var3 = var3.getParent()) {
         if (var3 instanceof Workbench) {
            var4 = var3;
            if (((Workbench)Workbench.class.cast(var3)).navigate(var1, var2, false)) {
               return true;
            }
         } else if (var3 instanceof JTabbedPane) {
         }
      }

      return var4 != null && var4 instanceof Workbench && ((Workbench)Workbench.class.cast(var4)).navigate(var1, true, false);
   }

   private void ascend(Component component) {
      this.ascend(component, new Stack());
   }

   private void ascend(Component var1, Stack<Component> var2) {
      if (var1 != null) {
         if (!(var1 instanceof JFrame)) {
            var2.push(var1);
            Container var3 = var1.getParent();
            if (var3 instanceof JTabbedPane) {
               JTabbedPane var4 = (JTabbedPane)JTabbedPane.class.cast(var3);

               while(!var2.isEmpty()) {
                  Component var5 = (Component)var2.pop();

                  try {
                     var4.setSelectedComponent(var5);
                     break;
                  } catch (IllegalArgumentException var7) {
                  }
               }
            } else if (var3 instanceof JFrame) {
               ((JFrame)JFrame.class.cast(var3)).requestFocus();
            }

            this.ascend(var3, var2);
         }
      }
   }

   @Override
   public void update(Observable o, Object arg) {
      if (!(arg instanceof StatusAlmalgum)) {
         if (o instanceof Tabbable) {
            arg = o;
         }

         if (arg instanceof Tabbable) {
            Tabbable var3 = (Tabbable)arg;
         }

      }
   }

   public void titleChanged() {
   }

   void setCurrentTab(Tabbable currentTab) {
      this.currentTab = currentTab;
   }

   public Tabbable getCurrentTab() {
      return this.currentTab;
   }

   public void stateChanged(ChangeEvent var1) {
   }

   @Override
   protected void convertTab(int oldIndex, int newIndex) {
      Component var3 = super.getComponentAt(oldIndex);
      if (var3 != null) {
         Tabbable var4 = this.getTabbableForDisplay(var3);
         synchronized(this.pluginsInOrder) {
            if (var4 != null) {
               this.pluginsInOrder.remove(var4);
               if (newIndex > this.getTabCount()) {
                  this.pluginsInOrder.add(var4);
               } else if (newIndex == 0) {
                  this.pluginsInOrder.add(newIndex, var4);
               } else {
                  int var6 = newIndex - 1 != oldIndex ? newIndex - 1 : newIndex - 2;
                  if (var6 < 0) {
                     this.pluginsInOrder.add(0, var4);
                  } else {
                     Component var7 = super.getComponentAt(var6);
                     if (var7 != null) {
                        Tabbable var8 = this.getTabbableForDisplay(var7);
                        if (var8 != null) {
                           var6 = this.pluginsInOrder.indexOf(var8);
                           if (var6 < this.pluginsInOrder.size()) {
                              this.pluginsInOrder.add(var6 + 1, var4);
                           } else {
                              this.pluginsInOrder.add(var4);
                           }
                        }
                     }
                  }
               }

               if (!this.pluginsInOrder.contains(var4)) {
                  this.pluginsInOrder.add(var4);
               }
            }
         }
      }

      super.convertTab(oldIndex, newIndex);
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }

   static {
      String[] var0 = new String[]{"0xff0000", "0x800000", "0x808000", "0x008000", "0x00ff00", "0x008000", "0x008080", "0x000080", "0x0000ff", "0x000080", "0x800080", "0x800000"};
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         String var3 = var0[var2];
         colors.add(Color.decode(var3));
      }

      colors.remove(Color.WHITE);
      colors.remove(Color.BLACK);
      NULL = new Runnable() {
         @Override
         public void run() {
         }
      };
      DEFAULT_DIMENSION = new Dimension(640, 480);
   }

   protected class SetState extends Workbench.DirectTabTask {
      Tabbable.TabState newState;

      public SetState(Tabbable var2, Tabbable.TabState var3) {
         super(var2);
         this.newState = var3;
      }

      @Override
      public void runTask() {
         if (this.tab != null) {
            if (this.newState == Tabbable.TabState.TABBED) {
               this.tab.generateDockedTitle();
            } else if (this.newState == Tabbable.TabState.DETACHED) {
               this.tab.generateFloatingTitle();
            }

            this.tab.setState(this.newState);
         }
      }
   }

   protected class StealFocus extends Workbench.DirectTabTask {
      int iterations = 10;

      public StealFocus(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         synchronized(Workbench.this.pluginsInOrder) {
            if (Workbench.this.pluginsInOrder.contains(this.tab)) {
               if (this.tab.isTabbed()) {
                  Workbench.this.invokeAction(Workbench.WorkbenchAction.SETSELECTEDTAB, this.tab);
               }
            } else if (this.iterations-- > 0) {
               EventQueue.invokeLater(this);
            }

         }
      }
   }

   protected abstract class CloseTab extends Workbench.DirectTabTask {
      public CloseTab(Tabbable var2) {
         super(var2);
      }
   }

   protected class AddTabToWorkbench extends Workbench.DirectTabTask {
      public AddTabToWorkbench(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         int var1 = -1;
         this.tab.addPropertyChangeListener("TABBABLE_CONTENT_CHANGED", Workbench.UpdatedTab);
         this.tab.addPropertyChangeListener("TABBABLE_CONTENT_CHANGED_REQUEST_FOCUS", Workbench.UpdatedTabRequestFocus);
         synchronized(Workbench.this.pluginsInOrder) {
            int var3;
            if (Workbench.this.pluginsInOrder.contains(this.tab)) {
               for(var3 = 0; var3 < Workbench.this.getTabCount(); ++var3) {
                  if (Workbench.this.getTabComponentAt(var3) == this.tab.getDisplay()) {
                     return;
                  }
               }
            } else {
               for(var3 = 0; var3 < Workbench.this.pluginsInOrder.size(); ++var3) {
                  Tabbable var4 = (Tabbable)Workbench.this.pluginsInOrder.get(var3);
                  if (var4.compareTo(this.tab) > 0) {
                     var1 = var3;
                     Workbench.this.pluginsInOrder.add(var3, this.tab);
                     break;
                  }
               }

               if (var1 == -1) {
                  var1 = Workbench.this.pluginsInOrder.size();
                  Workbench.this.pluginsInOrder.add(this.tab);
               }
            }
         }

         if (!this.tab.isDetached()) {
            int var2 = 0;
            synchronized(Workbench.this.pluginsInOrder) {
               Iterator var9 = Workbench.this.pluginsInOrder.iterator();

               while(var9.hasNext()) {
                  Tabbable var5 = (Tabbable)var9.next();
                  if (var5 == this.tab) {
                     break;
                  }

                  if (var5.isTabbed() && !var5.isHidden()) {
                     ++var2;
                  }
               }
            }

            ImageIcon var10 = null;
            if (this.tab.getLogo() != null) {
               var10 = ImageManager.getIcon(this.tab.getLogo(), Workbench.this.getTabImageSize());
            }

            if (var2 >= Workbench.this.getTabCount()) {
               var2 = Workbench.this.getTabCount();
            }

            Workbench.this.insertTab(this.tab.getName(), var10, this.tab.getDisplay(), this.tab.getShortDescription(), var2);
            Workbench.this.setTabComponentAt(var2, this.tab.getTabComponent());
            this.tab.setTabbed();
         }
      }

      @Override
      public boolean isRequiresDispatch() {
         return true;
      }
   }

   public class RemoveTab extends Workbench.DirectTabTask {
      public RemoveTab(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         this.tab.removePropertyChangeListener("TABBABLE_CONTENT_CHANGED", Workbench.UpdatedTab);
         this.tab.removePropertyChangeListener("TABBABLE_CONTENT_CHANGED_REQUEST_FOCUS", Workbench.UpdatedTabRequestFocus);
         synchronized(Workbench.this.pluginsInOrder) {
            Workbench.this.pluginsInOrder.remove(this.tab);
         }

         int var1 = Workbench.this.indexOfComponent(this.tab.getDisplay());
         if (var1 > -1) {
            Workbench.this.removeTabAt(var1);
         }

         synchronized(Workbench.this.componentToPlugin) {
            Workbench.this.componentToPlugin.remove(this.tab.getDisplay());
         }

         this.tab.getStatus().deleteObserver(Workbench.this);
         if (Workbench.this.currentTab == this.tab) {
            Workbench.this.currentTab = null;
         }

      }
   }

   protected class AddNewTab extends Workbench.DirectTabTask {
      public AddNewTab(Tabbable var2) {
         super(var2);
      }

      @Override
      public void runTask() {
         this.tab.setWorkbench(Workbench.this);
         synchronized(Workbench.this.componentToPlugin) {
            if (this.tab.getDisplay() != null) {
               Workbench.this.componentToPlugin.put(this.tab.getDisplay(), this.tab);
            }
         }

         Workbench.this.invokeAction(Workbench.WorkbenchAction.ADDTABTOWORKBENCH, this.tab);
         this.tab.generateDockedTitle();
         TabbableStatus var1 = this.tab.getStatus();
         var1.addObserver(Workbench.this);
      }
   }

   protected class SetSelectedIndex extends Workbench.IndirectTabTask {
      Integer index;

      public SetSelectedIndex(Integer var2) {
         super();
         this.index = var2;
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         if (Workbench.this.getTabCount() != 0) {
            if (this.index < 0) {
               this.index = this.index + Workbench.this.getTabCount();
            } else {
               this.index = this.index % Workbench.this.getTabCount();
            }

            Workbench.this.setSelectedIndex(this.index);
         }
      }
   }

   protected class SetSelectedTab extends Workbench.DirectTabTask {
      public SetSelectedTab(Tabbable var2) {
         super(var2);
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         switch(this.tab.getCurrentState()) {
         case TABBED:
            Workbench.this.setSelectedIndex(Workbench.this.indexOfComponent(this.tab.getDisplay()));
            Workbench.this.requestFocusInWindow();
            break;
         case DETACHED:
            Workbench.this.getTabFrame(this.tab, (Dimension)null, (Point)null).toFront();
         case UNKNOWN:
         default:
            break;
         case HIDDEN:
            Workbench.this.invokeAction(Workbench.WorkbenchAction.UNHIDETAB, this.tab);
            this.run();
         }

      }
   }

   protected class RenameTab extends Workbench.DirectTabTask {
      protected String name;

      public RenameTab(Tabbable tab) {
         super(tab);
      }

      public RenameTab(Tabbable tab, String name) {
         super(tab);
         this.name = name;
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         if (this.tab != null) {
            if (this.name == null) {
               this.name = this.tab.getName();
            }

            switch(this.tab.getCurrentState()) {
            case TABBED:
               this.tab.setName(this.name);
               int var1 = Workbench.this.indexOfComponent(this.tab.getDisplay());
               if (var1 == -1) {
                  System.err.println("out of order");
                  return;
               }

               Workbench.this.setTitleAt(var1, this.tab.getDockedTitle());
               break;
            case DETACHED:
               this.tab.setName(this.name);
               Workbench.this.getTabFrame(this.tab, (Dimension)null, (Point)null).setTitle(this.tab.getDetachedTitle());
               return;
            case UNKNOWN:
            default:
               break;
            case HIDDEN:
               this.tab.setName(this.name);
            }

         }
      }
   }

   protected class UnhideTab extends Workbench.DirectTabTask {
      public UnhideTab(Tabbable tab) {
         super(tab);
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         try {
            if (this.tab.getCurrentState() != Tabbable.TabState.HIDDEN) {
               return;
            }

            switch(this.tab.getPreviousState()) {
            case TABBED:
            case UNKNOWN:
               Workbench.this.invokeAction(Workbench.WorkbenchAction.ADDTABTOWORKBENCH, this.tab);
               break;
            case DETACHED:
               Workbench.this.invokeAction(Workbench.WorkbenchAction.DETACHTAB, this.tab, null, null);
               return;
            case HIDDEN:
               Workbench.this.invokeAction(Workbench.WorkbenchAction.ADDTABTOWORKBENCH, this.tab);
            }
         } catch (Exception var2) {
            Workbench.this.logger.log(Level.WARNING, var2.getMessage(), var2);
         }

      }
   }

   protected class HideTab extends Workbench.DirectTabTask {
      public HideTab(Tabbable tab) {
         super(tab);
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         switch(this.tab.getCurrentState()) {
         case TABBED:
            int var1 = Workbench.this.indexOfComponent(this.tab.getDisplay());
            if (var1 > -1) {
               Workbench.this.remove(var1);
            }

            this.tab.setHidden();
            break;
         case DETACHED:
            Workbench.this.getTabFrame(this.tab, (Dimension)null, (Point)null).setVisible(false);
            this.tab.setHidden();
            break;
         case UNKNOWN:
            this.tab.setHidden();
            break;
         case HIDDEN:
            return;
         }

      }
   }

   protected class TabbifyTab extends Workbench.DirectTabTask {
      public TabbifyTab(Tabbable tab) {
         super(tab);
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         switch(this.tab.getCurrentState()) {
         case TABBED:
            return;
         case DETACHED:
            this.tab.hideFrame();
            this.tab.setTabbed();
            break;
         case UNKNOWN:
         case HIDDEN:
            this.tab.setTabbed();
         }

         Workbench.this.invokeAction(Workbench.WorkbenchAction.ADDTABTOWORKBENCH, this.tab);
         Workbench.this.invokeAction(Workbench.WorkbenchAction.SETSELECTEDTAB, this.tab);
      }
   }

   protected class PredetachTab extends Workbench.DirectTabTask {
      protected Dimension size;
      protected Point position;

      public PredetachTab(Tabbable tab, Dimension size, Point position) {
         super(tab);
         this.size = size;
         this.position = position;
      }

      @Override
      public void runTask() {
         Workbench.this.getTabFrame(this.tab, this.size, this.position);
      }
   }

   protected class DetachTab extends Workbench.DirectTabTask {
      protected Dimension size;
      protected Point position;

      public DetachTab(Tabbable tab, Dimension size, Point position) {
         super(tab);
         this.size = size;
         this.position = position;
      }

      @Override
      protected boolean isRequiresDispatch() {
         return true;
      }

      @Override
      public void runTask() {
         switch(this.tab.getCurrentState()) {
         case TABBED:
            int var1 = Workbench.this.indexOfComponent(this.tab.getDisplay());
            this.tab.setDetached();
            if (var1 != -1) {
               Workbench.this.removeTabAt(var1);
            }
            break;
         case DETACHED:
            return;
         case UNKNOWN:
         case HIDDEN:
            this.tab.setDetached();
         }

         TabbableFrame var2 = Workbench.this.getTabFrame(this.tab, this.size, this.position);
         var2.setDisplay(this.tab.getDisplay());
         var2.setVisible(true);
         var2.repaint();
      }
   }

   public abstract class DirectTabTask extends Workbench.TabTask {
      protected Tabbable tab;

      protected DirectTabTask(Tabbable tab) {
         super();
         this.tab = tab;
      }
   }

   protected abstract class IndirectTabTask extends Workbench.TabTask {
      protected IndirectTabTask() {
         super();
      }
   }

   protected abstract class TabTask implements Runnable {
      @Override
      public final void run() {
         if (EventQueue.isDispatchThread()) {
            try {
               this.runTask();
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         } else if (this.isRequiresDispatch()) {
            try {
               EventQueue.invokeAndWait(this);
            } catch (InvocationTargetException var2) {
               var2.printStackTrace();
               Logger.getLogger("dsz").log(Level.SEVERE, (String)null, var2);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }
         } else {
            this.runTask();
         }

      }

      public abstract void runTask();

      protected boolean isRequiresDispatch() {
         return false;
      }
   }

   public enum WorkbenchAction {
      DETACHTAB,
      PREDETACHTAB,
      TABBIFYTAB,
      HIDETAB,
      UNHIDETAB,
      RENAMETAB,
      SETSELECTEDTAB,
      SETSELECTEDINDEX,
      ADDNEWTAB,
      REMOVETAB,
      ADDTABTOWORKBENCH,
      CLOSETAB,
      STEALFOCUS,
      SETSTATE;
   }
}
