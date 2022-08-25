package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "OutputType",
   propOrder = {"xml", "text"}
)
public class OutputType {
   @XmlElement(
      name = "Xml"
   )
   protected XmlOutputType xml;
   @XmlElement(
      name = "Text"
   )
   protected String text;
   @XmlAttribute(
      name = "color"
   )
   protected String color;

   public XmlOutputType getXml() {
      return this.xml;
   }

   public void setXml(XmlOutputType var1) {
      this.xml = var1;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      this.text = var1;
   }

   public String getColor() {
      return this.color == null ? "black" : this.color;
   }

   public void setColor(String var1) {
      this.color = var1;
   }
}
