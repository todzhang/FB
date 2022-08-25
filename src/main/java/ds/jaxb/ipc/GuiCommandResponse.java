package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "GuiCommandResponse"
)
public class GuiCommandResponse {
   @XmlAttribute(
      name = "success"
   )
   protected Boolean success;

   public Boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(Boolean var1) {
      this.success = var1;
   }
}
