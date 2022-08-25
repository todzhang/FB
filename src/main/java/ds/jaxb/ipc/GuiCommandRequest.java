package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "GuiCommandRequest",
   propOrder = {"argument"}
)
public class GuiCommandRequest {
   @XmlElement(
      name = "Argument",
      required = true
   )
   protected String argument;
   @XmlAttribute(
      name = "cmdId"
   )
   protected Integer cmdId;

   public String getArgument() {
      return this.argument;
   }

   public void setArgument(String var1) {
      this.argument = var1;
   }

   public Integer getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(Integer var1) {
      this.cmdId = var1;
   }
}
