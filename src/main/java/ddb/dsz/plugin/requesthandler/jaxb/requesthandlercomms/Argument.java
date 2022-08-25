package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Argument",
   propOrder = {"requires", "forbids", "string"}
)
public class Argument {
   @XmlElement(
      name = "Requires"
   )
   protected List<String> requires;
   @XmlElement(
      name = "Forbids"
   )
   protected List<String> forbids;
   @XmlElement(
      name = "String",
      required = true
   )
   protected String string;

   public List<String> getRequires() {
      if (this.requires == null) {
         this.requires = new ArrayList();
      }

      return this.requires;
   }

   public List<String> getForbids() {
      if (this.forbids == null) {
         this.forbids = new ArrayList();
      }

      return this.forbids;
   }

   public String getString() {
      return this.string;
   }

   public void setString(String var1) {
      this.string = var1;
   }
}
