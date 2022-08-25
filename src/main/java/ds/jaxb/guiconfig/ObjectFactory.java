package ds.jaxb.guiconfig;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _GuiConfig_QNAME = new QName("", "GuiConfig");

   public GuiConfig.TabAlignment createGuiConfigTabAlignment() {
      return new GuiConfig.TabAlignment();
   }

   public DszDimension createDszDimension() {
      return new DszDimension();
   }

   public GuiConfig createGuiConfig() {
      return new GuiConfig();
   }

   @XmlElementDecl(
      namespace = "",
      name = "GuiConfig"
   )
   public JAXBElement<GuiConfig> createGuiConfig(GuiConfig var1) {
      return new JAXBElement(_GuiConfig_QNAME, GuiConfig.class, (Class)null, var1);
   }
}
