package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "GetHelpType",
   propOrder = {"value"}
)
public class GetHelpType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "target"
   )
   protected String target;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String getTarget() {
      return this.target;
   }

   public void setTarget(String var1) {
      this.target = var1;
   }
}
