package ds.core.pluginevents;

import ddb.dsz.plugin.Plugin;

public class PluginEvent {
   private Plugin plugin;
   private PluginEvent.PluginEventState state;

   public Plugin getPlugin() {
      return this.plugin;
   }

   public PluginEvent.PluginEventState getState() {
      return this.state;
   }

   public PluginEvent(Plugin var1, PluginEvent.PluginEventState var2) {
      this.plugin = var1;
      this.state = var2;
   }

   public static enum PluginEventState {
      START,
      STOP;
   }
}
