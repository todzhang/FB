package ds.jaxb.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "EntryType",
   propOrder = {"icon", "verify", "label", "command", "action", "result"}
)
public class EntryType extends MenuItemBase {
   @XmlElement(
      name = "Icon"
   )
   protected String icon;
   @XmlElement(
      name = "Verify"
   )
   protected boolean verify;
   @XmlElement(
      name = "Label",
      required = true
   )
   protected String label;
   @XmlElement(
      name = "Command"
   )
   protected String command;
   @XmlElement(
      name = "Action"
   )
   protected ActionType action;
   @XmlElement(
      name = "Result"
   )
   protected boolean result;

   public String getIcon() {
      return this.icon;
   }

   public void setIcon(String var1) {
      this.icon = var1;
   }

   public boolean isVerify() {
      return this.verify;
   }

   public void setVerify(boolean var1) {
      this.verify = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      this.label = var1;
   }

   public String getCommand() {
      return this.command;
   }

   public void setCommand(String var1) {
      this.command = var1;
   }

   public ActionType getAction() {
      return this.action;
   }

   public void setAction(ActionType var1) {
      this.action = var1;
   }

   public boolean isResult() {
      return this.result;
   }

   public void setResult(boolean var1) {
      this.result = var1;
   }
}
