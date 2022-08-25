package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "BooleanValue",
   propOrder = {"value"}
)
public class BooleanValue {
   @XmlValue
   protected boolean value;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public boolean isValue() {
      return this.value;
   }

   public void setValue(boolean var1) {
      this.value = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
