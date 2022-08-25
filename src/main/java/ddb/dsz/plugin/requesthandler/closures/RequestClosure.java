package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.TaskDataRequestType;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.RequestToRecord;
import ddb.util.Guid;

public class RequestClosure extends RequestHandlerClosure {
   public static RequestHandlerClosure getInstance(RequestHandler var0) {
      return new RequestClosure(var0);
   }

   public RequestClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof RequestType) {
         RequestType var3 = (RequestType)RequestType.class.cast(var2.getData());
         RequestedOperation var4 = RequestToRecord.getInstance().transform(var3.getNewRequest());
         if (var4 != null) {
            var4.setTag(var2.getTag());
            var4.setSource(var2.getTag().toString());
            this.handler.handleRequest(var4, var2.getTag());
         }

         if (var3.getTaskDataRequest() != null) {
            TaskDataRequestType var5 = var3.getTaskDataRequest();
            Guid var6 = null;
            if (var5.getOperation() != null) {
               var6 = Guid.GenerateGuid(var5.getOperation());
            }

            this.handler.sendData(var2.getTag(), var6, var5.getTaskId(), var5.isIncludeChildren());
         }
      }

   }
}
