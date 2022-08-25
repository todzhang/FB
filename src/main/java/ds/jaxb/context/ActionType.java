package ds.jaxb.context;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ActionType",
   propOrder = {"line"}
)
public class ActionType {
   @XmlElement(
      name = "Line",
      required = true
   )
   protected List<String> line;

   public List<String> getLine() {
      if (this.line == null) {
         this.line = new ArrayList();
      }

      return this.line;
   }
}
