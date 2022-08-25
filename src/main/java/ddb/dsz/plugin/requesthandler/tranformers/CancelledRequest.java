package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.CancelledRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;

public class CancelledRequest extends RequestTransformer {
   public static CancelledRequest INSTANCE = new CancelledRequest();

   public static CancelledRequest getInstance() {
      return INSTANCE;
   }

   private CancelledRequest() {
   }

   @Override
   public JAXBElement<ResponseType> transform(Object input) {
      if (input instanceof RequestedOperation) {
         input = ((RequestedOperation)(input)).getId();
      }

      if (!(input instanceof BigInteger)) {
         return null;
      } else {
         BigInteger var2 = (BigInteger)(input);
         if (var2.equals(RequestedOperation.NO_ID)) {
            return null;
         } else {
            CancelledRequestType var3 = new CancelledRequestType();
            var3.setReqId(var2);
            ResponseType var4 = new ResponseType();
            var4.setCancelledRequest(var3);
            return objFact.createResponse(var4);
         }
      }
   }
}
