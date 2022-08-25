package ds.jaxb.context;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "MenuItemBase",
   propOrder = {"required", "forbidden"}
)
@XmlSeeAlso({ContextMenuType.class, EntryType.class})
public class MenuItemBase {
   @XmlElement(
      name = "Required"
   )
   protected List<String> required;
   @XmlElement(
      name = "Forbidden"
   )
   protected List<String> forbidden;
   @XmlAttribute(
      name = "live"
   )
   protected Boolean live;
   @XmlAttribute(
      name = "replay"
   )
   protected Boolean replay;

   public List<String> getRequired() {
      if (this.required == null) {
         this.required = new ArrayList();
      }

      return this.required;
   }

   public List<String> getForbidden() {
      if (this.forbidden == null) {
         this.forbidden = new ArrayList();
      }

      return this.forbidden;
   }

   public boolean isLive() {
      return this.live == null ? true : this.live;
   }

   public void setLive(Boolean var1) {
      this.live = var1;
   }

   public boolean isReplay() {
      return this.replay == null ? true : this.replay;
   }

   public void setReplay(Boolean var1) {
      this.replay = var1;
   }
}
