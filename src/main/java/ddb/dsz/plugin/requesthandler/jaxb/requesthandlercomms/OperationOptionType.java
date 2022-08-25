package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "OperationOptionType",
   propOrder = {"key", "data"}
)
@XmlSeeAlso({NewRequestType.class})
public class OperationOptionType {
   @XmlElement(
      name = "Key"
   )
   protected String key;
   @XmlElement(
      name = "Data"
   )
   protected List<KeyValuePair> data;

   public String getKey() {
      return this.key;
   }

   public void setKey(String var1) {
      this.key = var1;
   }

   public List<KeyValuePair> getData() {
      if (this.data == null) {
         this.data = new ArrayList();
      }

      return this.data;
   }
}
