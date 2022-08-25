package ddb.dsz.plugin.mirror.jaxb.mirrorcomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MirrorFileStatus",
   propOrder = {"file", "length"}
)
public class MirrorFileStatus {
   @XmlElement(
      required = true
   )
   protected String file;
   @XmlElement(
      required = true
   )
   protected BigInteger length;

   public String getFile() {
      return this.file;
   }

   public void setFile(String var1) {
      this.file = var1;
   }

   public BigInteger getLength() {
      return this.length;
   }

   public void setLength(BigInteger var1) {
      this.length = var1;
   }
}
