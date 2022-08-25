package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "IntegerValue",
   propOrder = {"value"}
)
public class IntegerValue {
   @XmlValue
   protected BigInteger value;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public BigInteger getValue() {
      return this.value;
   }

   public void setValue(BigInteger var1) {
      this.value = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
