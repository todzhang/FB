package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.CloseType;

public class CloseClosure extends RequestHandlerClosure {
   public CloseClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof CloseType) {
      }

   }
}
