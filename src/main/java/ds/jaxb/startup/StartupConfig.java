package ds.jaxb.startup;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"pluginOrMacro"}
)
@XmlRootElement(
   name = "StartupConfig"
)
public class StartupConfig {
   @XmlElements({@XmlElement(
   name = "Macro",
   type = LoadMacro.class
), @XmlElement(
   name = "Plugin",
   type = LoadPlugin.class
)})
   protected List<Object> pluginOrMacro;

   public List<Object> getPluginOrMacro() {
      if (this.pluginOrMacro == null) {
         this.pluginOrMacro = new ArrayList();
      }

      return this.pluginOrMacro;
   }
}
