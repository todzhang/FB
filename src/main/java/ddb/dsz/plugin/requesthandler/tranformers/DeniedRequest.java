package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.DeniedRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;

public class DeniedRequest extends RequestTransformer {
   public static DeniedRequest INSTANCE = new DeniedRequest();

   public static DeniedRequest getInstance() {
      return INSTANCE;
   }

   private DeniedRequest() {
   }

   @Override
   public JAXBElement<ResponseType> transform(Object input) {
      if (input instanceof RequestedOperation) {
         input = ((RequestedOperation)RequestedOperation.class.cast(input)).getId();
      }

      if (!(input instanceof BigInteger)) {
         return null;
      } else {
         BigInteger var2 = (BigInteger)BigInteger.class.cast(input);
         if (var2.equals(RequestedOperation.NO_ID)) {
            return null;
         } else {
            DeniedRequestType var3 = new DeniedRequestType();
            var3.setReqId(var2);
            ResponseType var4 = new ResponseType();
            var4.setDeniedRequest(var3);
            return objFact.createResponse(var4);
         }
      }
   }
}
