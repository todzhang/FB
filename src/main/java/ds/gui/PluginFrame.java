package ds.gui;

import ddb.detach.Tabbable;
import ddb.detach.TabbableFrame;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;
import ds.core.StatusBar;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;

public class PluginFrame extends TabbableFrame implements ConnectionChangeListener {
   CoreController core;
   StatusBar statusBar;

   public PluginFrame(Tabbable var1, PluginWorkbench var2, Dimension var3, Point var4, CoreController var5) {
      super(var1, var2, var3, var4);
      this.core = var5;
      if (this.statusBar == null) {
         this.getStatusBar();
      }

      if (var1 instanceof Plugin) {
         this.statusBar.setVisible(((Plugin)Plugin.class.cast(var1)).isShowStatus());
      }

      var5.addConnectionChangeListener(this);
      this.updateTitleBar();
   }

   protected synchronized JComponent getStatusBar() {
      if (this.statusBar == null) {
         this.statusBar = new StatusBar();
         this.statusBar.setStatus(this.tab.getStatus(), this.tab);
      }

      return this.statusBar;
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      this.updateTitleBar();
   }

   private void updateTitleBar() {
      super.setTitle(this.tab.getDetachedTitle());
   }

   public void setTitle(String var1) {
      this.updateTitleBar();
   }
}
