package ddb.dsz.library.console.jaxb.consolecommands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Argument",
   propOrder = {"split", "format"}
)
public class Argument {
   @XmlElement(
      name = "Split"
   )
   protected Split split;
   @XmlElement(
      name = "Format"
   )
   protected String format;

   public Split getSplit() {
      return this.split;
   }

   public void setSplit(Split var1) {
      this.split = var1;
   }

   public String getFormat() {
      return this.format;
   }

   public void setFormat(String var1) {
      this.format = var1;
   }
}
