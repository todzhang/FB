package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ThrottleInfoType",
   propOrder = {"address", "bytesPerSecond"}
)
public class ThrottleInfoType {
   @XmlElement(
      name = "Address",
      required = true
   )
   protected String address;
   @XmlElement(
      name = "BytesPerSecond"
   )
   protected int bytesPerSecond;

   public String getAddress() {
      return this.address;
   }

   public void setAddress(String var1) {
      this.address = var1;
   }

   public int getBytesPerSecond() {
      return this.bytesPerSecond;
   }

   public void setBytesPerSecond(int var1) {
      this.bytesPerSecond = var1;
   }
}
