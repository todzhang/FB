package ds.jaxb.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ObjectType",
   propOrder = {"string", "color", "object", "data"}
)
public class ObjectType {
   @XmlElement(
      name = "String"
   )
   protected String string;
   @XmlElement(
      name = "Color"
   )
   protected ColorType color;
   @XmlElement(
      name = "Object"
   )
   protected List<ObjectType> object;
   @XmlElement(
      name = "Data",
      type = String.class
   )
   @XmlJavaTypeAdapter(HexBinaryAdapter.class)
   @XmlSchemaType(
      name = "hexBinary"
   )
   protected byte[] data;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public String getString() {
      return this.string;
   }

   public void setString(String var1) {
      this.string = var1;
   }

   public ColorType getColor() {
      return this.color;
   }

   public void setColor(ColorType var1) {
      this.color = var1;
   }

   public List<ObjectType> getObject() {
      if (this.object == null) {
         this.object = new ArrayList();
      }

      return this.object;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] var1) {
      this.data = (byte[])var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
