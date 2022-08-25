package ddb.util;

import ds.jaxb.keybindings.KeyBindingSet;
import ds.jaxb.keybindings.KeyBindingType;
import ds.jaxb.keybindings.ObjectFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class KeybindingConfigParser {
   public static Hashtable<KeyStroke, String> parse(InputStream inputStream) throws XMLException {
      Hashtable var1 = new Hashtable();
      KeyBindingSet var2 = null;

      try {
         JAXBContext var3 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var4 = var3.createUnmarshaller();
         Object var5 = var4.unmarshal(inputStream);
         if (var5 instanceof JAXBElement) {
            JAXBElement var6 = (JAXBElement)var5;
            if (var6.getValue() instanceof KeyBindingSet) {
               var2 = (KeyBindingSet)var6.getValue();
            }
         }
      } catch (JAXBException var7) {
         throw new XMLException(var7.getMessage(), var7.getCause());
      }

      if (var2 == null) {
         return var1;
      } else {
         Iterator var8 = var2.getKeyBinding().iterator();

         while(var8.hasNext()) {
            KeyBindingType var9 = (KeyBindingType)var8.next();
            KeyStroke var10 = KeyStroke.getKeyStroke(var9.getKeyStroke());
            String var11 = var9.getActionName();
            if (var10 != null) {
               var1.put(var10, var11);
            }
         }

         return var1;
      }
   }

   public static Hashtable<KeyStroke, String> parse(String s) throws FileNotFoundException, XMLException {
      return parse(new FileInputStream(s));
   }
}
