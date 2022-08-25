package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ExecutedRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;

public class ExecutedRequestClosure extends RequestHandlerClosure {
   public ExecutedRequestClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      Object var3 = var2.getData();
      if (var3 instanceof ResponseType) {
         var3 = ((ResponseType)ResponseType.class.cast(var3)).getExecutedRequest();
      }

      if (var3 instanceof ExecutedRequestType) {
         ExecutedRequestType var4 = (ExecutedRequestType)ExecutedRequestType.class.cast(var3);
         this.handler.executedRequest(var4.getReqId(), var2.getTag());
      }

   }
}
