package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.functors.ChainedClosure;

public class ResponseClosure extends RequestHandlerClosure {
   Closure closure;

   public static RequestHandlerClosure getInstance(RequestHandler var0) {
      return new ResponseClosure(var0);
   }

   public ResponseClosure(RequestHandler var1) {
      super(var1);
      this.closure = ChainedClosure.getInstance(new RequestHandlerClosure[]{new NewRequestClosure(var1), new ExecutedRequestClosure(var1), new CancelledRequestClosure(var1), new DeniedRequestClosure(var1)});
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      if (var2.getData() instanceof ResponseType) {
         this.closure.execute(var2);
      }

   }
}
