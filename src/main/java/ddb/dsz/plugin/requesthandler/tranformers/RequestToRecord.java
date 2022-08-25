package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.KeyValuePair;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.OperationOptionType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.util.Iterator;
import org.apache.commons.collections.Transformer;

public class RequestToRecord extends RequestTransformer {
   public static RequestToRecord INSTANCE = new RequestToRecord();
   Transformer worker;

   public static RequestToRecord getInstance() {
      return INSTANCE;
   }

   private RequestToRecord() {
   }

   @Override
   public RequestedOperation transform(Object input) {
      if (!(input instanceof OperationOptionType)) {
         return null;
      } else {
         OperationOptionType var2 = (OperationOptionType)OperationOptionType.class.cast(input);
         RequestedOperation var3 = new RequestedOperation();
         var3.setKey(var2.getKey());
         Iterator var4 = var2.getData().iterator();

         while(var4.hasNext()) {
            KeyValuePair var5 = (KeyValuePair)var4.next();
            var3.setData(var5.getKey(), var5.getValue());
         }

         return var3;
      }
   }
}
