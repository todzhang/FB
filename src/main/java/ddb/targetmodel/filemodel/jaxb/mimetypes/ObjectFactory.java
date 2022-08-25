package ddb.targetmodel.filemodel.jaxb.mimetypes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _MimeTypes_QNAME = new QName("", "MimeTypes");

   public MimeTypes.MimeType createMimeTypesMimeType() {
      return new MimeTypes.MimeType();
   }

   public MimeTypes createMimeTypes() {
      return new MimeTypes();
   }

   @XmlElementDecl(
      namespace = "",
      name = "MimeTypes"
   )
   public JAXBElement<MimeTypes> createMimeTypes(MimeTypes var1) {
      return new JAXBElement(_MimeTypes_QNAME, MimeTypes.class, (Class)null, var1);
   }
}
