package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "XmlOutputEnum"
)
@XmlEnum
public enum XmlOutputEnum {
   @XmlEnumValue("Default")
   DEFAULT("Default"),
   @XmlEnumValue("Warning")
   WARNING("Warning"),
   @XmlEnumValue("Good")
   GOOD("Good"),
   @XmlEnumValue("Error")
   ERROR("Error");

   private final String value;

   private XmlOutputEnum(String var3) {
      this.value = var3;
   }

   public String value() {
      return this.value;
   }

   public static XmlOutputEnum fromValue(String var0) {
      XmlOutputEnum[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         XmlOutputEnum var4 = var1[var3];
         if (var4.value.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException(var0);
   }
}
