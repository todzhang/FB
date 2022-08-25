package ddb.targetmodel.filemodel.jaxb.mimetypes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MimeTypes",
   propOrder = {"mimeType"}
)
public class MimeTypes {
   @XmlElement(
      name = "MimeType"
   )
   protected List<MimeTypes.MimeType> mimeType;

   public List<MimeTypes.MimeType> getMimeType() {
      if (this.mimeType == null) {
         this.mimeType = new ArrayList();
      }

      return this.mimeType;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(
      name = "",
      propOrder = {"name", "icon", "extension"}
   )
   public static class MimeType {
      @XmlElement(
         name = "Name",
         required = true
      )
      protected String name;
      @XmlElement(
         name = "Icon",
         required = true
      )
      protected String icon;
      @XmlElement(
         name = "Extension",
         required = true
      )
      protected List<String> extension;

      public String getName() {
         return this.name;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public String getIcon() {
         return this.icon;
      }

      public void setIcon(String var1) {
         this.icon = var1;
      }

      public List<String> getExtension() {
         if (this.extension == null) {
            this.extension = new ArrayList();
         }

         return this.extension;
      }
   }
}
