package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.KeyValuePair;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.OperationOptionType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.util.Iterator;

public abstract class RecordToOperationOption extends RequestTransformer {
   protected OperationOptionType generate(RequestedOperation var1) {
      OperationOptionType var2;
      try {
         var2 = (OperationOptionType)OperationOptionType.class.cast(this.getType().newInstance());
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }

      var2.setKey(var1.getKey());
      Iterator var3 = var1.getDataKeys().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         KeyValuePair var5 = new KeyValuePair();
         var5.setKey(var4);
         var5.setValue(var1.getData(var4));
         var2.getData().add(var5);
      }

      return var2;
   }

   protected abstract Class<? extends OperationOptionType> getType();
}
