package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "SetFlagsType",
   propOrder = {"flag"}
)
public class SetFlagsType {
   @XmlElement(
      name = "Flag",
      required = true
   )
   protected List<String> flag;

   public List<String> getFlag() {
      if (this.flag == null) {
         this.flag = new ArrayList();
      }

      return this.flag;
   }
}
