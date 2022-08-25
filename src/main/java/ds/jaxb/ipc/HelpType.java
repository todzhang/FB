package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "HelpType",
   propOrder = {"command", "helpStatement"}
)
public class HelpType {
   @XmlElement(
      name = "Command",
      required = true
   )
   protected String command;
   @XmlElement(
      name = "HelpStatement",
      required = true
   )
   protected String helpStatement;

   public String getCommand() {
      return this.command;
   }

   public void setCommand(String var1) {
      this.command = var1;
   }

   public String getHelpStatement() {
      return this.helpStatement;
   }

   public void setHelpStatement(String var1) {
      this.helpStatement = var1;
   }
}
