package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "AliasDefinitionType"
)
public class AliasDefinitionType {
   @XmlAttribute(
      name = "original",
      required = true
   )
   protected String original;
   @XmlAttribute(
      name = "replace",
      required = true
   )
   protected String replace;

   public String getOriginal() {
      return this.original;
   }

   public void setOriginal(String var1) {
      this.original = var1;
   }

   public String getReplace() {
      return this.replace;
   }

   public void setReplace(String var1) {
      this.replace = var1;
   }
}
