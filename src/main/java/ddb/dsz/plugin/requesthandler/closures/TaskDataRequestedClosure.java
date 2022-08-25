package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.TaskDataRequestType;
import ddb.util.Guid;

public class TaskDataRequestedClosure extends RequestHandlerClosure {
   public TaskDataRequestedClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof RequestType) {
         var1 = ((RequestType)RequestType.class.cast(var2.getData())).getTaskDataRequest();
      }

      if (var1 instanceof TaskDataRequestType) {
         TaskDataRequestType var3 = (TaskDataRequestType)var1;
         Guid var4 = null;
         if (var3.getOperation() != null) {
            var4 = Guid.GenerateGuid(var3.getOperation());
         }
      }

   }
}
