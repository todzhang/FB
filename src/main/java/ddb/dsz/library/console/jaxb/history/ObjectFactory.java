package ddb.dsz.library.console.jaxb.history;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _History_QNAME = new QName("", "History");

   public CommandHistory createCommandHistory() {
      return new CommandHistory();
   }

   @XmlElementDecl(
      namespace = "",
      name = "History"
   )
   public JAXBElement<CommandHistory> createHistory(CommandHistory commandHistory) {
      return new JAXBElement(_History_QNAME, CommandHistory.class, null, commandHistory);
   }
}
