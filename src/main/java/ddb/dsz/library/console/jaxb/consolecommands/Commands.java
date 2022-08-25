package ddb.dsz.library.console.jaxb.consolecommands;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Commands",
   propOrder = {"command"}
)
public class Commands {
   @XmlElement(
      name = "Command"
   )
   protected List<Command> command;

   public List<Command> getCommand() {
      if (this.command == null) {
         this.command = new ArrayList();
      }

      return this.command;
   }
}
