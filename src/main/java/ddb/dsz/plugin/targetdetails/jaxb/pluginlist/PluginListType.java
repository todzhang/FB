package ddb.dsz.plugin.targetdetails.jaxb.pluginlist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "PluginListType",
   propOrder = {"plugin"}
)
public class PluginListType {
   @XmlElement(
      name = "Plugin"
   )
   protected List<PluginType> plugin;

   public List<PluginType> getPlugin() {
      if (this.plugin == null) {
         this.plugin = new ArrayList();
      }

      return this.plugin;
   }
}
