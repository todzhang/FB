package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "IdMapType",
   propOrder = {"value"}
)
public class IdMapType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "tmpId",
      required = true
   )
   protected int tmpId;
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

   public int getTmpId() {
      return this.tmpId;
   }

   public void setTmpId(int var1) {
      this.tmpId = var1;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int var1) {
      this.cmdId = var1;
   }
}
