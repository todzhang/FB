package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

public class InternalTranslator implements Transformer, Predicate {
   public static final String REQUEST = "request";
   public static final InternalTranslator INSTANCE = new InternalTranslator();

   public static final InternalTranslator getInstance() {
      return INSTANCE;
   }

   private InternalTranslator() {
   }

   public boolean evaluate(Object var1) {
      if (var1 instanceof List) {
         List var2 = (List)List.class.cast(var1);
         if (var2.size() < 2) {
            return false;
         } else {
            return "request".equals(var2.get(0));
         }
      } else {
         return false;
      }
   }

   @Override
   public RequestedOperation transform(Object input) {
      if (!(input instanceof List)) {
         return null;
      } else {
         List var2 = (List)input;
         if (var2.size() < 2) {
            return null;
         } else if (!((String)var2.get(0)).equals("request")) {
            return null;
         } else {
            RequestedOperation var3 = new RequestedOperation();
            var3.setKey((String)var2.get(1));
            Iterator var4 = var2.subList(2, var2.size()).iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               int var6 = var5.indexOf(61);
               if (var6 == -1) {
                  var3.setData(var5, "");
               } else if (var6 + 1 != var5.length()) {
                  var3.setData(var5.substring(0, var6), var5.substring(var6 + 1));
               }
            }

            return var3;
         }
      }
   }
}
