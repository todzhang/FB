package ds.jaxb.configuration;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _Configuration_QNAME = new QName("", "Configuration");

   public ColorType createColorType() {
      return new ColorType();
   }

   public ObjectType createObjectType() {
      return new ObjectType();
   }

   public ConfigurationType createConfigurationType() {
      return new ConfigurationType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Configuration"
   )
   public JAXBElement<ConfigurationType> createConfiguration(ConfigurationType var1) {
      return new JAXBElement(_Configuration_QNAME, ConfigurationType.class, (Class)null, var1);
   }
}
