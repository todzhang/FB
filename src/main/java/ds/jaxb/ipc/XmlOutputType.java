package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "XmlOutputType",
   propOrder = {"node"}
)
public class XmlOutputType {
   @XmlElement(
      name = "Node"
   )
   protected List<XmlOutputType.Node> node;

   public List<XmlOutputType.Node> getNode() {
      if (this.node == null) {
         this.node = new ArrayList();
      }

      return this.node;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(
      name = "",
      propOrder = {"value"}
   )
   public static class Node {
      @XmlValue
      protected String value;
      @XmlAttribute(
         name = "type"
      )
      protected XmlOutputEnum type;

      public String getValue() {
         return this.value;
      }

      public void setValue(String var1) {
         this.value = var1;
      }

      public XmlOutputEnum getType() {
         return this.type;
      }

      public void setType(XmlOutputEnum var1) {
         this.type = var1;
      }
   }
}
