package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CloseType",
   propOrder = {"value"}
)
public class CloseType {
   @XmlValue
   protected String value;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }
}
