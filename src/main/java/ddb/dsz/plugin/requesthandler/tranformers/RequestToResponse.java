package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.NewRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.OperationOptionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import javax.xml.bind.JAXBElement;

public class RequestToResponse extends RecordToOperationOption {
   public static RequestToResponse INSTANCE = new RequestToResponse();

   public static RequestToResponse getInstance() {
      return INSTANCE;
   }

   @Override
   public JAXBElement<ResponseType> transform(Object input) {
      if (!(input instanceof RequestedOperation)) {
         return null;
      } else {
         RequestedOperation var2 = (RequestedOperation)RequestedOperation.class.cast(input);
         NewRequestType var3 = (NewRequestType)NewRequestType.class.cast(this.generate(var2));
         var3.setReqId(var2.getId());
         var3.setSource(var2.getSource());
         ResponseType var4 = new ResponseType();
         var4.setNewRequest(var3);
         return objFact.createResponse(var4);
      }
   }

   @Override
   protected Class<? extends OperationOptionType> getType() {
      return NewRequestType.class;
   }
}
