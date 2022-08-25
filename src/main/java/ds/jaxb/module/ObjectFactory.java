package ds.jaxb.module;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _Plugin_QNAME = new QName("", "Plugin");

   public Module createModule() {
      return new Module();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Plugin"
   )
   public JAXBElement<Module> createPlugin(Module var1) {
      return new JAXBElement(_Plugin_QNAME, Module.class, (Class)null, var1);
   }
}
