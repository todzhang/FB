package ddb.dsz.library.console.jaxb.consolecommands;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _Commands_QNAME = new QName("", "Commands");

   public Argument createArgument() {
      return new Argument();
   }

   public Command createCommand() {
      return new Command();
   }

   public Statement createStatement() {
      return new Statement();
   }

   public Split createSplit() {
      return new Split();
   }

   public Commands createCommands() {
      return new Commands();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Commands"
   )
   public JAXBElement<Commands> createCommands(Commands commands) {
      return new JAXBElement(_Commands_QNAME, Commands.class, (Class)null, commands);
   }
}
