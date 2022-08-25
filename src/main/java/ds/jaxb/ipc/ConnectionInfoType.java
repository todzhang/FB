package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ConnectionInfoType",
   propOrder = {"host"}
)
public class ConnectionInfoType {
   @XmlElement(
      name = "Host"
   )
   protected List<HostInfoType> host;

   public List<HostInfoType> getHost() {
      if (this.host == null) {
         this.host = new ArrayList();
      }

      return this.host;
   }
}
