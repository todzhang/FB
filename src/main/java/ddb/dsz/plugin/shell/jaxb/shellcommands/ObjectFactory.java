package ddb.dsz.plugin.shell.jaxb.shellcommands;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _ShellMap_QNAME = new QName("", "ShellMap");

   public SystemType createSystemType() {
      return new SystemType();
   }

   public ShellMapType createShellMapType() {
      return new ShellMapType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "ShellMap"
   )
   public JAXBElement<ShellMapType> createShellMap(ShellMapType var1) {
      return new JAXBElement(_ShellMap_QNAME, ShellMapType.class, (Class)null, var1);
   }
}
