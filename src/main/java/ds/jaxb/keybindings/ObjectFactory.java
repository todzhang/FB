package ds.jaxb.keybindings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _KeyBindings_QNAME = new QName("", "KeyBindings");

   public KeyBindingSet createKeyBindingSet() {
      return new KeyBindingSet();
   }

   public KeyBindingType createKeyBindingType() {
      return new KeyBindingType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "KeyBindings"
   )
   public JAXBElement<KeyBindingSet> createKeyBindings(KeyBindingSet var1) {
      return new JAXBElement(_KeyBindings_QNAME, KeyBindingSet.class, (Class)null, var1);
   }
}
