package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.CancelType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;

public class CancelRequest extends RequestTransformer {
   public static CancelRequest INSTANCE = new CancelRequest();

   public static CancelRequest getInstance() {
      return INSTANCE;
   }

   private CancelRequest() {
   }

   @Override
   public JAXBElement<CancelType> transform(Object input) {
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
            CancelType var3 = new CancelType();
            var3.setReqId(var2);
            return objFact.createCancel(var3);
         }
      }
   }
}
