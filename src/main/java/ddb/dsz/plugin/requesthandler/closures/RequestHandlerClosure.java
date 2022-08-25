package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.functors.ChainedClosure;

public abstract class RequestHandlerClosure implements Closure {
   RequestHandler handler;

   public static Closure getInstance(RequestHandler var0) {
      RequestHandlerClosure[] var1 = new RequestHandlerClosure[]{new CancelClosure(var0), new CloseClosure(var0), new RequestClosure(var0), new ResponseClosure(var0), new RequestCompletedClosure(var0), new TaskDataArrivedClosure(var0), new TaskDataRequestedClosure(var0)};
      return ChainedClosure.getInstance(var1);
   }

   public RequestHandlerClosure(RequestHandler var1) {
      this.handler = var1;
   }
}
