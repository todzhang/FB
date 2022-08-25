package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.RequestToRecord;

public class NewRequestClosure extends RequestHandlerClosure {
   public NewRequestClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof ResponseType) {
         ResponseType var3 = (ResponseType)ResponseType.class.cast(var2.getData());
         RequestedOperation var4 = RequestToRecord.getInstance().transform(var3.getNewRequest());
         if (var4 != null && var3.getNewRequest() != null) {
            var4.setId(var3.getNewRequest().getReqId());
            var4.setTag(var2.getTag());
            if (var3.getNewRequest().getSource() != null) {
               var4.setSource(var3.getNewRequest().getSource());
            }

            this.handler.handleNewRequest(var4, var2.getTag());
         }
      }

   }
}
