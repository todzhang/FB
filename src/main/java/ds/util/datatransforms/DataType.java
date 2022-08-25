package ds.util.datatransforms;

import ddb.dsz.core.data.ObjectValue;
import ds.util.datatransforms.transformers.BooleanTransformer;
import ds.util.datatransforms.transformers.LongTransformer;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NOPTransformer;

public enum DataType {
   OBJECT(ObjectValue.class, NOPTransformer.getInstance(), new String[]{"ObjectValue"}),
   STRING(String.class, NOPTransformer.getInstance(), new String[]{"StringValue"}),
   INTEGER(Long.class, new LongTransformer(), new String[]{"IntValue", "UIntValue"}),
   BOOLEAN(Boolean.class, new BooleanTransformer(), new String[]{"BoolValue"});

   Class<?> clazz;
   Transformer transformer;
   Predicate instanceOf;
   String[] elemName;

   private DataType(Class<?> var3, Transformer var4, String... var5) {
      this.clazz = var3;
      this.transformer = var4;
      this.instanceOf = PredicateUtils.instanceofPredicate(var3);
      this.elemName = var5;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public Transformer getTransformer() {
      return this.transformer;
   }

   public Predicate getPredicate() {
      return this.instanceOf;
   }

   public String[] getElementName() {
      return this.elemName;
   }

   public boolean isElement(String var1) {
      String[] var2 = this.elemName;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var5.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public static DataType getTypeForName(String var0) {
      DataType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DataType var4 = var1[var3];
         String[] var5 = var4.getElementName();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8.equals(var0)) {
               return var4;
            }
         }
      }

      return null;
   }
}
