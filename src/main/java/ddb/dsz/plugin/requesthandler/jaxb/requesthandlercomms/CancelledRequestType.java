package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CancelledRequestType",
   propOrder = {"value"}
)
public class CancelledRequestType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "reqId"
   )
   protected BigInteger reqId;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public BigInteger getReqId() {
      return this.reqId;
   }

   public void setReqId(BigInteger var1) {
      this.reqId = var1;
   }
}
