package ddb.dsz.library.console.jaxb.history;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CommandHistory",
   propOrder = {"command"}
)
public class CommandHistory {
   @XmlElement(
      name = "Command"
   )
   protected List<String> command;

   public List<String> getCommand() {
      if (this.command == null) {
         this.command = new ArrayList();
      }

      return this.command;
   }
}
