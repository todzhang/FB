package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.TaskDataType;

public class TaskDataArrivedClosure extends RequestHandlerClosure {
   public TaskDataArrivedClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof ResponseType) {
         var1 = ((ResponseType)ResponseType.class.cast(var1)).getTaskData();
      }

      if (var1 instanceof TaskDataType) {
         TaskDataType var3 = (TaskDataType)var1;
         System.out.println(var3);
      }

   }
}
