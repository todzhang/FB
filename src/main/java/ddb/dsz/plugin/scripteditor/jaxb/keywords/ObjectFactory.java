package ddb.dsz.plugin.scripteditor.jaxb.keywords;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public Keyword createKeyword() {
      return new Keyword();
   }

   public Keywords createKeywords() {
      return new Keywords();
   }
}
