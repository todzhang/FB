package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.CancelType;

public class CancelClosure extends RequestHandlerClosure {
   public CancelClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof CancelType) {
         this.handler.handleCancel(((CancelType)CancelType.class.cast(var2.getData())).getReqId(), var2.getTag());
      }

   }
}
