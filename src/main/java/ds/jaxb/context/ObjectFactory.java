package ds.jaxb.context;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _ContextMenu_QNAME = new QName("", "ContextMenu");

   public MenuItemBase createMenuItemBase() {
      return new MenuItemBase();
   }

   public EntryType createEntryType() {
      return new EntryType();
   }

   public ActionType createActionType() {
      return new ActionType();
   }

   public ContextMenuType createContextMenuType() {
      return new ContextMenuType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "ContextMenu"
   )
   public JAXBElement<ContextMenuType> createContextMenu(ContextMenuType var1) {
      return new JAXBElement(_ContextMenu_QNAME, ContextMenuType.class, (Class)null, var1);
   }
}
