package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "StartCommandType",
   propOrder = {"value"}
)
public class StartCommandType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "tmpId",
      required = true
   )
   protected int tmpId;
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

   public int getTmpId() {
      return this.tmpId;
   }

   public void setTmpId(int var1) {
      this.tmpId = var1;
   }

   public String getTarget() {
      return this.target;
   }

   public void setTarget(String var1) {
      this.target = var1;
   }
}
