package ds.jaxb.mimemap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MimeMap",
   propOrder = {"value"}
)
public class MimeMap {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "suffix"
   )
   protected String suffix;
   @XmlAttribute(
      name = "icon"
   )
   protected String icon;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public void setSuffix(String var1) {
      this.suffix = var1;
   }

   public String getIcon() {
      return this.icon;
   }

   public void setIcon(String var1) {
      this.icon = var1;
   }
}
