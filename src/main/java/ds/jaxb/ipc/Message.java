package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"cmd", "req", "res", "info"}
)
@XmlRootElement(
   name = "Message"
)
public class Message {
   @XmlElement(
      name = "Cmd"
   )
   protected CommandType cmd;
   @XmlElement(
      name = "Req"
   )
   protected RequestType req;
   @XmlElement(
      name = "Res"
   )
   protected ResponseType res;
   @XmlElement(
      name = "Info"
   )
   protected InfoType info;

   public CommandType getCmd() {
      return this.cmd;
   }

   public void setCmd(CommandType var1) {
      this.cmd = var1;
   }

   public RequestType getReq() {
      return this.req;
   }

   public void setReq(RequestType var1) {
      this.req = var1;
   }

   public ResponseType getRes() {
      return this.res;
   }

   public void setRes(ResponseType var1) {
      this.res = var1;
   }

   public InfoType getInfo() {
      return this.info;
   }

   public void setInfo(InfoType var1) {
      this.info = var1;
   }
}
