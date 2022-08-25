package ds.jaxb.keybindings;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "KeyBindingSet",
   propOrder = {"keyBinding"}
)
public class KeyBindingSet {
   @XmlElement(
      name = "KeyBinding",
      required = true
   )
   protected List<KeyBindingType> keyBinding;

   public List<KeyBindingType> getKeyBinding() {
      if (this.keyBinding == null) {
         this.keyBinding = new ArrayList();
      }

      return this.keyBinding;
   }
}
