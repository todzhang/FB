package ds.jaxb.guiconfig;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "DszDimension",
   propOrder = {"width", "height"}
)
public class DszDimension {
   @XmlElement(
      name = "Width",
      required = true
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger width;
   @XmlElement(
      name = "Height",
      required = true
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger height;

   public BigInteger getWidth() {
      return this.width;
   }

   public void setWidth(BigInteger var1) {
      this.width = var1;
   }

   public BigInteger getHeight() {
      return this.height;
   }

   public void setHeight(BigInteger var1) {
      this.height = var1;
   }
}
