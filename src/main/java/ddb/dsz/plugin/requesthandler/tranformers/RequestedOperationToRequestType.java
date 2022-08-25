package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.NewRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.OperationOptionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import javax.xml.bind.JAXBElement;

public class RequestedOperationToRequestType extends RecordToOperationOption {
   public static RequestedOperationToRequestType INSTANCE = new RequestedOperationToRequestType();

   public static RequestedOperationToRequestType getInstance() {
      return INSTANCE;
   }

   @Override
   public JAXBElement<RequestType> transform(Object input) {
      if (!(input instanceof RequestedOperation)) {
         return null;
      } else {
         RequestType var2 = RequestTransformer.objFact.createRequestType();
         var2.setNewRequest((OperationOptionType)NewRequestType.class.cast(this.generate((RequestedOperation)RequestedOperation.class.cast(input))));
         return RequestTransformer.objFact.createRequest(var2);
      }
   }

   @Override
   protected Class<? extends OperationOptionType> getType() {
      return NewRequestType.class;
   }
}
