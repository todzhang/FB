package ddb.dsz.library.console.jaxb.consolecommands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "Statement",
   propOrder = {"value", "argument", "_null"}
)
public class Statement {
   @XmlElement(
      name = "Value"
   )
   protected String value;
   @XmlElement(
      name = "Argument"
   )
   protected Argument argument;
   @XmlElement(
      name = "Null"
   )
   protected String _null;

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public Argument getArgument() {
      return this.argument;
   }

   public void setArgument(Argument argument) {
      this.argument = argument;
   }

   public String getNull() {
      return this._null;
   }

   public void setNull(String _null) {
      this._null = _null;
   }
}
