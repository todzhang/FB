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
   name = "CommandDefinitionType",
   propOrder = {"option"}
)
public class CommandDefinitionType {
   @XmlElement(
      name = "Option"
   )
   protected List<String> option;
   @XmlAttribute(
      name = "name",
      required = true
   )
   protected String name;
   @XmlAttribute(
      name = "optionPrefix",
      required = true
   )
   protected String optionPrefix;

   public List<String> getOption() {
      if (this.option == null) {
         this.option = new ArrayList();
      }

      return this.option;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getOptionPrefix() {
      return this.optionPrefix;
   }

   public void setOptionPrefix(String var1) {
      this.optionPrefix = var1;
   }
}
