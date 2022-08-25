package ds.plugin.pluginmanagement;

import ddb.dsz.plugin.Plugin;
import javax.swing.JFrame;

public class PluginManagementInfo {
   private Class<? extends Plugin> plugin;
   private JFrame pluginFrame;
   private int state;
   private int prevState;

   public PluginManagementInfo(Class<? extends Plugin> var1, int var2) {
      this.plugin = var1;
      this.state = var2;
      this.prevState = var2;
   }

   public JFrame getPluginFrame() {
      return this.pluginFrame;
   }

   public void setPluginFrame(JFrame var1) {
      this.pluginFrame = var1;
   }

   public int getState() {
      return this.state;
   }

   public void setState(int var1) {
      this.prevState = this.state;
      this.state = var1;
   }

   public int getPrevState() {
      return this.prevState;
   }

   public Class<? extends Plugin> getPlugin() {
      return this.plugin;
   }

   public static enum PluginState {
      TAB,
      HIDDEN,
      DETACHED;
   }
}
