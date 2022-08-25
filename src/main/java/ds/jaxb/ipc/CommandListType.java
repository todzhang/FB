package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CommandListType",
   propOrder = {"prefix", "alias", "command"}
)
public class CommandListType {
   @XmlElement(
      name = "Prefix"
   )
   protected List<String> prefix;
   @XmlElement(
      name = "Alias"
   )
   protected List<AliasDefinitionType> alias;
   @XmlElement(
      name = "Command"
   )
   protected List<CommandDefinitionType> command;

   public List<String> getPrefix() {
      if (this.prefix == null) {
         this.prefix = new ArrayList();
      }

      return this.prefix;
   }

   public List<AliasDefinitionType> getAlias() {
      if (this.alias == null) {
         this.alias = new ArrayList();
      }

      return this.alias;
   }

   public List<CommandDefinitionType> getCommand() {
      if (this.command == null) {
         this.command = new ArrayList();
      }

      return this.command;
   }
}
