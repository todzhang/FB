package ds.jaxb.guiconfig;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "DszTabAlignment"
)
@XmlEnum
public enum DszTabAlignment {
   @XmlEnumValue("Top")
   TOP("Top"),
   @XmlEnumValue("Bottom")
   BOTTOM("Bottom"),
   @XmlEnumValue("Left")
   LEFT("Left"),
   @XmlEnumValue("Right")
   RIGHT("Right");

   private final String value;

   private DszTabAlignment(String var3) {
      this.value = var3;
   }

   public String value() {
      return this.value;
   }

   public static DszTabAlignment fromValue(String var0) {
      DszTabAlignment[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DszTabAlignment var4 = var1[var3];
         if (var4.value.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException(var0);
   }
}
