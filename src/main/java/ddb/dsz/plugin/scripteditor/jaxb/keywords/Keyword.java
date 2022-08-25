package ddb.dsz.plugin.scripteditor.jaxb.keywords;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Keyword",
   propOrder = {"value"}
)
public class Keyword {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "type"
   )
   protected BigInteger type;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public BigInteger getType() {
      return this.type;
   }

   public void setType(BigInteger var1) {
      this.type = var1;
   }
}
