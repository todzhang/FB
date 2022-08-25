package ddb.dsz.plugin.mirror.jaxb.mirrorcomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MirrorTransfer",
   propOrder = {"file", "offset", "length", "data"}
)
public class MirrorTransfer {
   @XmlElement(
      required = true
   )
   protected String file;
   @XmlElement(
      required = true
   )
   protected BigInteger offset;
   @XmlElement(
      required = true
   )
   protected BigInteger length;
   @XmlElement(
      required = true
   )
   protected byte[] data;

   public String getFile() {
      return this.file;
   }

   public void setFile(String var1) {
      this.file = var1;
   }

   public BigInteger getOffset() {
      return this.offset;
   }

   public void setOffset(BigInteger var1) {
      this.offset = var1;
   }

   public BigInteger getLength() {
      return this.length;
   }

   public void setLength(BigInteger var1) {
      this.length = var1;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] var1) {
      this.data = (byte[])var1;
   }
}
