package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "AutoApproveType",
   propOrder = {"identifier"}
)
public class AutoApproveType {
   @XmlElement(
      name = "Identifier"
   )
   protected List<String> identifier;

   public List<String> getIdentifier() {
      if (this.identifier == null) {
         this.identifier = new ArrayList();
      }

      return this.identifier;
   }
}
