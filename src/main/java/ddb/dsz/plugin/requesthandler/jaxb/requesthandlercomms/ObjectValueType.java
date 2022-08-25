package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ObjectValueType",
   propOrder = {"objectValue", "stringValue", "integerValue", "booleanValue"}
)
public class ObjectValueType {
   @XmlElement(
      name = "ObjectValue"
   )
   protected List<ObjectValueType> objectValue;
   @XmlElement(
      name = "StringValue"
   )
   protected List<StringValue> stringValue;
   @XmlElement(
      name = "IntegerValue"
   )
   protected List<IntegerValue> integerValue;
   @XmlElement(
      name = "BooleanValue"
   )
   protected List<BooleanValue> booleanValue;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public List<ObjectValueType> getObjectValue() {
      if (this.objectValue == null) {
         this.objectValue = new ArrayList();
      }

      return this.objectValue;
   }

   public List<StringValue> getStringValue() {
      if (this.stringValue == null) {
         this.stringValue = new ArrayList();
      }

      return this.stringValue;
   }

   public List<IntegerValue> getIntegerValue() {
      if (this.integerValue == null) {
         this.integerValue = new ArrayList();
      }

      return this.integerValue;
   }

   public List<BooleanValue> getBooleanValue() {
      if (this.booleanValue == null) {
         this.booleanValue = new ArrayList();
      }

      return this.booleanValue;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
