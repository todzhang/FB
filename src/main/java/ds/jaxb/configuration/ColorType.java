package ds.jaxb.configuration;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ColorType",
   propOrder = {"red", "green", "blue"}
)
public class ColorType {
   @XmlElement(
      name = "Red",
      required = true
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger red;
   @XmlElement(
      name = "Green",
      required = true
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger green;
   @XmlElement(
      name = "Blue",
      required = true
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger blue;

   public BigInteger getRed() {
      return this.red;
   }

   public void setRed(BigInteger var1) {
      this.red = var1;
   }

   public BigInteger getGreen() {
      return this.green;
   }

   public void setGreen(BigInteger var1) {
      this.green = var1;
   }

   public BigInteger getBlue() {
      return this.blue;
   }

   public void setBlue(BigInteger var1) {
      this.blue = var1;
   }
}
