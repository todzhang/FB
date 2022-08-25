package ds.jaxb.external;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "RemoteMessage",
   propOrder = {"message"}
)
public class RemoteMessage {
   @XmlElement(
      name = "Message",
      required = true
   )
   protected String message;

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }
}
