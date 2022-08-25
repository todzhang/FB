package ddb.dsz.plugin.shell.jaxb.shellcommands;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ShellMapType",
   propOrder = {"system"}
)
public class ShellMapType {
   @XmlElement(
      name = "System"
   )
   protected List<SystemType> system;

   public List<SystemType> getSystem() {
      if (this.system == null) {
         this.system = new ArrayList();
      }

      return this.system;
   }
}
