package ds.jaxb.context;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ContextMenuType",
   propOrder = {"entryOrSeperatorOrSubmenu"}
)
public class ContextMenuType extends MenuItemBase {
   @XmlElements({@XmlElement(
   name = "Entry",
   type = EntryType.class
), @XmlElement(
   name = "Seperator"
), @XmlElement(
   name = "Submenu",
   type = ContextMenuType.class
)})
   protected List<MenuItemBase> entryOrSeperatorOrSubmenu;
   @XmlAttribute(
      name = "name"
   )
   protected String name;

   public List<MenuItemBase> getEntryOrSeperatorOrSubmenu() {
      if (this.entryOrSeperatorOrSubmenu == null) {
         this.entryOrSeperatorOrSubmenu = new ArrayList();
      }

      return this.entryOrSeperatorOrSubmenu;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
