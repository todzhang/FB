package ds.jaxb.mimemap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _MimeMap_QNAME = new QName("", "Mime-map");

   public MimeMap createMimeMap() {
      return new MimeMap();
   }

   public MimeMapList createMimeMapList() {
      return new MimeMapList();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Mime-map"
   )
   public JAXBElement<MimeMapList> createMimeMap(MimeMapList var1) {
      return new JAXBElement(_MimeMap_QNAME, MimeMapList.class, (Class)null, var1);
   }
}
