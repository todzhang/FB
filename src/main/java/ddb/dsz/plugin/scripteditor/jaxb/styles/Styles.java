package ddb.dsz.plugin.scripteditor.jaxb.styles;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"style"}
)
@XmlRootElement(
   name = "Styles"
)
public class Styles {
   @XmlElement(
      name = "Style",
      required = true
   )
   protected List<Style> style;

   public List<Style> getStyle() {
      if (this.style == null) {
         this.style = new ArrayList();
      }

      return this.style;
   }
}
