package ddb.dsz.plugin.about.jaxb.version;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _Version_QNAME = new QName("", "Version");

   public VersionType createVersionType() {
      return new VersionType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Version"
   )
   public JAXBElement<VersionType> createVersion(VersionType versionType) {
      return new JAXBElement(_Version_QNAME, VersionType.class, (Class)null, versionType);
   }
}
