package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "StringElementWithCmdIdType",
   propOrder = {"value"}
)
@XmlSeeAlso({PromptType.class, UserEntryType.class})
public class StringElementWithCmdIdType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "cmdId",
      required = true
   )
   protected int cmdId;

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int cmdId) {
      this.cmdId = cmdId;
   }
}
