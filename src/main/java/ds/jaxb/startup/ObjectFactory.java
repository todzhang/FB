package ds.jaxb.startup;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public StartupConfig createStartupConfig() {
      return new StartupConfig();
   }

   public LoadMacro createLoadMacro() {
      return new LoadMacro();
   }

   public LoadPlugin createLoadPlugin() {
      return new LoadPlugin();
   }

   public LoadPlugin.Detached createLoadPluginDetached() {
      return new LoadPlugin.Detached();
   }

   public LoadMacro.Detached createLoadMacroDetached() {
      return new LoadMacro.Detached();
   }
}
