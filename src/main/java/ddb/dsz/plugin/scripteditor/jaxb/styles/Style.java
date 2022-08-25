package ddb.dsz.plugin.scripteditor.jaxb.styles;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Style",
   propOrder = {"bold", "italics", "foreground"}
)
public class Style {
   @XmlElement(
      name = "Bold"
   )
   protected boolean bold;
   @XmlElement(
      name = "Italics"
   )
   protected boolean italics;
   @XmlElement(
      name = "Foreground",
      required = true
   )
   protected String foreground;
   @XmlAttribute(
      name = "id"
   )
   protected BigInteger id;

   public boolean isBold() {
      return this.bold;
   }

   public void setBold(boolean var1) {
      this.bold = var1;
   }

   public boolean isItalics() {
      return this.italics;
   }

   public void setItalics(boolean var1) {
      this.italics = var1;
   }

   public String getForeground() {
      return this.foreground;
   }

   public void setForeground(String var1) {
      this.foreground = var1;
   }

   public BigInteger getId() {
      return this.id;
   }

   public void setId(BigInteger var1) {
      this.id = var1;
   }
}
