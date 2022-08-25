package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "AddPrefixesType",
   propOrder = {"prefix"}
)
public class AddPrefixesType {
   @XmlElement(
      name = "Prefix",
      required = true
   )
   protected List<String> prefix;
   @XmlAttribute(
      name = "cmdId",
      required = true
   )
   protected int cmdId;

   public List<String> getPrefix() {
      if (this.prefix == null) {
         this.prefix = new ArrayList();
      }

      return this.prefix;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int var1) {
      this.cmdId = var1;
   }
}
