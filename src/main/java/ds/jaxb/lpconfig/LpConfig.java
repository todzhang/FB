package ds.jaxb.lpconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"port", "logDir", "resourceDir", "localAddress", "remoteAddress"}
)
@XmlRootElement(
   name = "LpConfig"
)
public class LpConfig {
   @XmlElement(
      name = "Port",
      required = true
   )
   protected String port;
   @XmlElement(
      name = "LogDir",
      required = true
   )
   protected String logDir;
   @XmlElement(
      name = "ResourceDir",
      required = true
   )
   protected String resourceDir;
   @XmlElement(
      name = "LocalAddress",
      required = true
   )
   protected String localAddress;
   @XmlElement(
      name = "RemoteAddress"
   )
   @XmlSchemaType(
      name = "unsignedInt"
   )
   protected Long remoteAddress;

   public String getPort() {
      return this.port;
   }

   public void setPort(String var1) {
      this.port = var1;
   }

   public String getLogDir() {
      return this.logDir;
   }

   public void setLogDir(String var1) {
      this.logDir = var1;
   }

   public String getResourceDir() {
      return this.resourceDir;
   }

   public void setResourceDir(String var1) {
      this.resourceDir = var1;
   }

   public String getLocalAddress() {
      return this.localAddress;
   }

   public void setLocalAddress(String var1) {
      this.localAddress = var1;
   }

   public Long getRemoteAddress() {
      return this.remoteAddress;
   }

   public void setRemoteAddress(Long var1) {
      this.remoteAddress = var1;
   }
}
