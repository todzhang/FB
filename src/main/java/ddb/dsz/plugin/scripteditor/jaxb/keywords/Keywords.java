package ddb.dsz.plugin.scripteditor.jaxb.keywords;

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
   propOrder = {"keyword"}
)
@XmlRootElement(
   name = "Keywords"
)
public class Keywords {
   @XmlElement(
      name = "Keyword",
      required = true
   )
   protected List<Keyword> keyword;

   public List<Keyword> getKeyword() {
      if (this.keyword == null) {
         this.keyword = new ArrayList();
      }

      return this.keyword;
   }
}
