package ds.jaxb.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ConfigurationType",
   propOrder = {"object"}
)
public class ConfigurationType {
   @XmlElement(
      name = "Object"
   )
   protected List<ObjectType> object;
   @XmlAttribute(
      name = "class"
   )
   protected String clazz;

   public List<ObjectType> getObject() {
      if (this.object == null) {
         this.object = new ArrayList();
      }

      return this.object;
   }

   public String getClazz() {
      return this.clazz;
   }

   public void setClazz(String var1) {
      this.clazz = var1;
   }
}
