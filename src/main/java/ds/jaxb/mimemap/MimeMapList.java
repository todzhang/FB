package ds.jaxb.mimemap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MimeMapList",
   propOrder = {"map", "_default"}
)
public class MimeMapList {
   @XmlElement(
      name = "Map"
   )
   protected List<MimeMap> map;
   @XmlElement(
      name = "Default",
      required = true
   )
   protected String _default;

   public List<MimeMap> getMap() {
      if (this.map == null) {
         this.map = new ArrayList();
      }

      return this.map;
   }

   public String getDefault() {
      return this._default;
   }

   public void setDefault(String var1) {
      this._default = var1;
   }
}
