package ddb.dsz.library.console.jaxb.consolecommands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Command",
   propOrder = {"name", "help", "command", "args"}
)
public class Command {
   @XmlElement(
      name = "Name",
      required = true
   )
   protected String name;
   @XmlElement(
      name = "Help",
      required = true
   )
   protected String help;
   @XmlElement(
      name = "Command",
      required = true
   )
   protected Statement command;
   @XmlElement(
      name = "Args",
      required = true
   )
   protected Statement args;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getHelp() {
      return this.help;
   }

   public void setHelp(String help) {
      this.help = help;
   }

   public Statement getCommand() {
      return this.command;
   }

   public void setCommand(Statement statement) {
      this.command = statement;
   }

   public Statement getArgs() {
      return this.args;
   }

   public void setArgs(Statement args) {
      this.args = args;
   }
}
