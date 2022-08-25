package ddb.dsz.plugin.targetdetails.jaxb.pluginlist;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _PluginList_QNAME = new QName("", "PluginList");

   public PluginType createPluginType() {
      return new PluginType();
   }

   public PluginListType createPluginListType() {
      return new PluginListType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "PluginList"
   )
   public JAXBElement<PluginListType> createPluginList(PluginListType var1) {
      return new JAXBElement(_PluginList_QNAME, PluginListType.class, (Class)null, var1);
   }
}
