package ddb.dsz.plugin.targetdetails.jaxb.pluginlist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "PluginType"
)
public class PluginType {
   @XmlAttribute(
      name = "className"
   )
   protected String className;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public String getClassName() {
      return this.className;
   }

   public void setClassName(String var1) {
      this.className = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
