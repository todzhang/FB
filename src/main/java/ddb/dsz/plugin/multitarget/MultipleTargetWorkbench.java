package ddb.dsz.plugin.multitarget;

import ddb.detach.Tabbable;
import ddb.detach.TabbableFrame;
import ddb.detach.TabbablePopupListener;
import ddb.detach.Workbench;
import ddb.detach.Workbench.DirectTabTask;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.Plugin;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class MultipleTargetWorkbench extends Workbench {
   protected final MultipleTargetPlugin owner;

   public MultipleTargetWorkbench(MultipleTargetPlugin var1) {
      this.owner = var1;
      this.init();
   }

   private void init() {
      this.actionHandler.put(MultipleTargetWorkbench.MultipleTargetWorkbenchAction.STOPPLUGIN, MultipleTargetWorkbench.StopPlugin.class);
      this.actionHandler.put(WorkbenchAction.ADDNEWTAB, MultipleTargetWorkbench.AddNewTab.class);
      this.actionHandler.put(WorkbenchAction.SETSELECTEDTAB, MultipleTargetWorkbench.SetSelectedTab.class);
   }

   @Override
   public String getTitleAt(int index) {
      Component var2 = this.getComponentAt(index);
      if (var2 != null) {
         Tabbable var3 = this.getTabbableForDisplay(var2);
         if (var3 != null) {
            return var3.getName();
         }
      }

      return super.getTitleAt(index);
   }

   @Override
   public JPopupMenu getMenu() {
      return new MultipleTargetPopupMenu(this, this.owner, this.owner.getCoreController());
   }

   @Override
   public TabbablePopupListener getTabPopupListener() {
      return new MultipleTargetPopupListener(this);
   }

   public JComponent getDefaultElement() {
      Component var1 = this.getSelectedComponent();
      if (var1 == null) {
         return null;
      } else {
         Tabbable var2 = this.getTabbableForDisplay(var1);
         return var2 != null ? var2.getDefaultElement() : null;
      }
   }

   @Override
   protected Dimension getDefaultSize() {
      Dimension var1 = new Dimension(640, 480);

      for(int var2 = 0; var2 < this.pluginsInOrder.size(); ++var2) {
         try {
            Tabbable var3 = (Tabbable)this.pluginsInOrder.get(var2);
            if (var3 != null) {
               var1.height = Math.max(var1.height, var3.getPreferredSize().height);
               var1.width = Math.max(var1.width, var3.getPreferredSize().width);
            }
         } catch (Exception var4) {
         }
      }

      return var1;
   }

   protected class SetSelectedTab extends ddb.detach.Workbench.SetSelectedTab {
      public SetSelectedTab(Tabbable var2) {
         super( var2);
      }

      public void runTask() {
         super.runTask();
      }
   }

   protected class AddNewTab extends ddb.detach.Workbench.AddNewTab {
      public AddNewTab(Tabbable var2) {
         super( var2);
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
         MultipleTargetWorkbench.this.owner.destroy((SingleTargetInterface)SingleTargetInterface.class.cast(this.tab));
         MultipleTargetWorkbench.this.invokeAction(WorkbenchAction.REMOVETAB, new Object[]{this.tab});
         MultipleTargetWorkbench.this.invokeAction(WorkbenchAction.HIDETAB, new Object[]{this.tab});
         MultipleTargetWorkbench.this.invokeAction(MultipleTargetWorkbench.MultipleTargetWorkbenchAction.STOPPLUGIN, new Object[]{this.tab});
         this.tab.hideFrame();
      }
   }

   protected class StopPlugin extends DirectTabTask {
      Thread th = null;
      boolean started = false;
      boolean killed = false;

      public StopPlugin(SingleTargetInterface var2) {
         super( var2);
      }

      public void runTask() {
         if (!this.started) {
            this.started = true;
            this.th = new Thread(new Runnable() {
               public void run() {
                  SingleTargetInterface var1 = (SingleTargetInterface)StopPlugin.this.tab;
                  MultipleTargetWorkbench.this.owner.getCoreController().logEvent(Level.FINEST, "Stopping plugin: " + var1.toString());
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
            } catch (Exception var3) {
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

            MultipleTargetWorkbench.this.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{var1});
         }
      }
   }

   public static enum MultipleTargetWorkbenchAction {
      STOPPLUGIN;
   }
}
