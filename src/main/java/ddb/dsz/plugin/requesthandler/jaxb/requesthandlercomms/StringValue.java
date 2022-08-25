package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "StringValue",
   propOrder = {"value"}
)
public class StringValue {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
