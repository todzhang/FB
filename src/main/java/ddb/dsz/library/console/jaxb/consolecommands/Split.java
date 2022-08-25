package ddb.dsz.library.console.jaxb.consolecommands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Split",
   propOrder = {"string", "regex", "limit", "part", "_default"}
)
public class Split {
   @XmlElement(
      name = "String",
      required = true
   )
   protected String string;
   @XmlElement(
      name = "Regex",
      required = true
   )
   protected String regex;
   @XmlElement(
      name = "Limit"
   )
   protected int limit;
   @XmlElement(
      name = "Part"
   )
   protected int part;
   @XmlElement(
      name = "Default"
   )
   protected String _default;

   public String getString() {
      return this.string;
   }

   public void setString(String string) {
      this.string = string;
   }

   public String getRegex() {
      return this.regex;
   }

   public void setRegex(String regex) {
      this.regex = regex;
   }

   public int getLimit() {
      return this.limit;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public int getPart() {
      return this.part;
   }

   public void setPart(int part) {
      this.part = part;
   }

   public String getDefault() {
      return this._default;
   }

   public void setDefault(String _default) {
      this._default = _default;
   }
}
