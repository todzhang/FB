package ds.util.datatransforms.transformers;

import java.math.BigInteger;
import org.apache.commons.collections.Transformer;

public class LongTransformer implements Transformer {
   public Object transform(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            return parseLong(var1.toString());
         } catch (NumberFormatException var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   static Long parseLong(String var0) {
      BigInteger var1 = parseBigInteger(var0);
      return var1 == null ? null : var1.longValue();
   }

   private static BigInteger parseBigInteger(String var0) {
      var0 = var0.trim().toLowerCase();
      if (var0.length() == 0) {
         return BigInteger.ZERO;
      } else if (var0.length() == 1) {
         return new BigInteger(var0);
      } else if (var0.charAt(0) == '0') {
         boolean var1 = true;
         byte var7;
         if (var0.charAt(1) == 'x') {
            var0 = var0.substring(2);
            var7 = 16;
         } else {
            var0 = var0.substring(1);
            var7 = 8;
         }

         try {
            return new BigInteger(var0, var7);
         } catch (NumberFormatException var5) {
            try {
               return new BigInteger(var0, 16);
            } catch (NumberFormatException var4) {
               return null;
            }
         }
      } else {
         try {
            return new BigInteger(var0);
         } catch (NumberFormatException var6) {
            return null;
         }
      }
   }
}
