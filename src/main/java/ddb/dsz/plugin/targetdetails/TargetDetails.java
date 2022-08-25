package ddb.dsz.plugin.targetdetails;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.MultipleTargetWorkbench;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin.LocalHostState;
import ddb.dsz.plugin.targetdetails.jaxb.pluginlist.ObjectFactory;
import ddb.dsz.plugin.targetdetails.jaxb.pluginlist.PluginListType;
import ddb.dsz.plugin.targetdetails.jaxb.pluginlist.PluginType;
import ddb.util.JaxbCache;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/anti_spam.png")
@DszName("TargetDetails")
@DszDescription("Target specific details")
@DszUserStartable(false)
public class TargetDetails extends MultipleTargetPlugin {
   public static String CONFIG_PATH = "TargetDetails/PluginList.xml";
   private Collection<TargetDetails.DisplayEntry> clazzes;

   protected MultipleTargetWorkbench generateWorkbench() {
      return new TargetDetailsWorkbench(this);
   }

   public TargetDetails() {
      super.setName("TargetDetails");
      super.setShowButtons(false);
   }

   protected int init3() {
      super.setDisplay(super.tabWorkbench);
      super.notifyOnNewHost = true;
      super.tabWorkbench.setTabPlacement(2);
      this.clazzes = new Vector();

      try {
         JAXBContext var1 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var2 = var1.createUnmarshaller();
         URL var3 = this.getClass().getClassLoader().getResource(CONFIG_PATH);
         if (var3 != null) {
            Object var4 = var2.unmarshal(var3);
            if (var4 instanceof JAXBElement) {
               JAXBElement var5 = (JAXBElement)var4;
               if (var5.getValue() instanceof PluginListType) {
                  PluginListType var6 = (PluginListType)var5.getValue();
                  Iterator var7 = var6.getPlugin().iterator();

                  while(var7.hasNext()) {
                     PluginType var8 = (PluginType)var7.next();

                     try {
                        this.clazzes.add(new TargetDetails.DisplayEntry(var8.getClassName(), var8.getName()));
                     } catch (Exception var10) {
                        var10.printStackTrace();
                     }
                  }
               }
            }
         }
      } catch (JAXBException var11) {
         this.core.logEvent(Level.WARNING, var11.getMessage(), var11);
      }

      return 0;
   }

   protected SingleTargetInterface newHost(HostInfo var1) {
      return new TargetDetailsHost(var1, this.core, this, this.clazzes);
   }

   protected LocalHostState getLocalHostState() {
      return LocalHostState.IGNORE;
   }

   public String newItemName() {
      return "New TargetDetails";
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Method var2 = var1.getMethod("main", String[].class);
      var2.invoke((Object)null, var0);
   }

   public static class DisplayEntry {
      Class<?> clazz;
      String name;

      public DisplayEntry(String var1, String var2) throws ClassNotFoundException {
         this.name = var2;
         this.clazz = Class.forName(var1);
      }
   }
}
