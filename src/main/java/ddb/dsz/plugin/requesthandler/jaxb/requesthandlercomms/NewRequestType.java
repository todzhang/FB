package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "NewRequestType",
   propOrder = {"source"}
)
public class NewRequestType extends OperationOptionType {
   @XmlElement(
      name = "Source"
   )
   protected String source;
   @XmlAttribute(
      name = "reqId"
   )
   protected BigInteger reqId;

   public String getSource() {
      return this.source;
   }

   public void setSource(String var1) {
      this.source = var1;
   }

   public BigInteger getReqId() {
      return this.reqId;
   }

   public void setReqId(BigInteger var1) {
      this.reqId = var1;
   }
}
