package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "PromptStoppedType",
   propOrder = {"value"}
)
public class PromptStoppedType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "cmdId",
      required = true
   )
   protected int cmdId;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int var1) {
      this.cmdId = var1;
   }
}
