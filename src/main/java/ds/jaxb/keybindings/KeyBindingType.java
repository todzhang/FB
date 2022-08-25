package ds.jaxb.keybindings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "KeyBindingType",
   propOrder = {"keyStroke", "actionName"}
)
public class KeyBindingType {
   @XmlElement(
      name = "KeyStroke",
      required = true
   )
   protected String keyStroke;
   @XmlElement(
      name = "ActionName",
      required = true
   )
   protected String actionName;

   public String getKeyStroke() {
      return this.keyStroke;
   }

   public void setKeyStroke(String var1) {
      this.keyStroke = var1;
   }

   public String getActionName() {
      return this.actionName;
   }

   public void setActionName(String var1) {
      this.actionName = var1;
   }
}
