package ds.jaxb.ipc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "StatisticsType",
   propOrder = {"host"}
)
public class StatisticsType {
   @XmlElement(
      name = "Host"
   )
   protected List<StatisticsType.Host> host;

   public List<StatisticsType.Host> getHost() {
      if (this.host == null) {
         this.host = new ArrayList();
      }

      return this.host;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(
      name = "",
      propOrder = {"address", "sent", "received"}
   )
   public static class Host {
      @XmlElement(
         name = "Address",
         required = true
      )
      protected String address;
      @XmlElement(
         name = "Sent",
         required = true
      )
      protected BigInteger sent;
      @XmlElement(
         name = "Received",
         required = true
      )
      protected BigInteger received;

      public String getAddress() {
         return this.address;
      }

      public void setAddress(String var1) {
         this.address = var1;
      }

      public BigInteger getSent() {
         return this.sent;
      }

      public void setSent(BigInteger var1) {
         this.sent = var1;
      }

      public BigInteger getReceived() {
         return this.received;
      }

      public void setReceived(BigInteger var1) {
         this.received = var1;
      }
   }
}
