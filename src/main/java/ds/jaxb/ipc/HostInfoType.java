package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "HostInfoType",
   propOrder = {"local", "connected", "address", "version", "platform", "arch", "implantType", "hostname", "guid"}
)
public class HostInfoType {
   @XmlElement(
      name = "Local"
   )
   protected boolean local;
   @XmlElement(
      name = "Connected"
   )
   protected boolean connected;
   @XmlElement(
      name = "Address",
      required = true
   )
   protected String address;
   @XmlElement(
      name = "Version",
      required = true
   )
   protected String version;
   @XmlElement(
      name = "Platform",
      required = true
   )
   protected String platform;
   @XmlElement(
      name = "Arch",
      required = true
   )
   protected String arch;
   @XmlElement(
      name = "ImplantType"
   )
   protected String implantType;
   @XmlElement(
      name = "Hostname"
   )
   protected String hostname;
   @XmlElement(
      name = "GUID"
   )
   protected String guid;

   public boolean isLocal() {
      return this.local;
   }

   public void setLocal(boolean var1) {
      this.local = var1;
   }

   public boolean isConnected() {
      return this.connected;
   }

   public void setConnected(boolean var1) {
      this.connected = var1;
   }

   public String getAddress() {
      return this.address;
   }

   public void setAddress(String var1) {
      this.address = var1;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(String var1) {
      this.platform = var1;
   }

   public String getArch() {
      return this.arch;
   }

   public void setArch(String var1) {
      this.arch = var1;
   }

   public String getImplantType() {
      return this.implantType;
   }

   public void setImplantType(String var1) {
      this.implantType = var1;
   }

   public String getHostname() {
      return this.hostname;
   }

   public void setHostname(String var1) {
      this.hostname = var1;
   }

   public String getGUID() {
      return this.guid;
   }

   public void setGUID(String var1) {
      this.guid = var1;
   }
}
