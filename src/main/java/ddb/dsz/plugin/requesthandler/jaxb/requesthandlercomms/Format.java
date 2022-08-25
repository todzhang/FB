package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Format",
   propOrder = {"prefix", "argument", "postfix"}
)
public class Format {
   @XmlElement(
      name = "Prefix",
      required = true
   )
   protected String prefix;
   @XmlElement(
      name = "Argument"
   )
   protected List<Argument> argument;
   @XmlElement(
      name = "Postfix"
   )
   protected String postfix;

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String var1) {
      this.prefix = var1;
   }

   public List<Argument> getArgument() {
      if (this.argument == null) {
         this.argument = new ArrayList();
      }

      return this.argument;
   }

   public String getPostfix() {
      return this.postfix;
   }

   public void setPostfix(String var1) {
      this.postfix = var1;
   }
}
