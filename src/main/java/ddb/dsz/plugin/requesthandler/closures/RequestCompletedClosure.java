package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestCompletedType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.util.Guid;

public class RequestCompletedClosure extends RequestHandlerClosure {
   public RequestCompletedClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof ResponseType) {
         var1 = ((ResponseType)ResponseType.class.cast(var2.getData())).getRequestCompleted();
      }

      if (var1 instanceof RequestCompletedType) {
         RequestCompletedType var3 = (RequestCompletedType)var1;
         if (var3.getTaskId() != null & var3.getOperation() != null) {
            this.handler.requestData(TaskId.GenerateTaskId(var3.getTaskId().intValue(), this.handler.getOperationById(Guid.GenerateGuid(var3.getOperation()))));
         }
      }

   }
}
