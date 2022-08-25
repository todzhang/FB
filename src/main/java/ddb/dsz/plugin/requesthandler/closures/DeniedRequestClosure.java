package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.requesthandler.RequestHandler;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.DeniedRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ResponseType;
import javax.xml.bind.JAXBElement;

public class DeniedRequestClosure extends RequestHandlerClosure {
   public DeniedRequestClosure(RequestHandler var1) {
      super(var1);
   }

   public void execute(Object var1) {
      ClosureData var2 = (ClosureData)var1;
      Object var3 = var2.getData();
      if (var3 instanceof JAXBElement) {
         var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
      }

      if (var3 instanceof ResponseType) {
         var3 = ((ResponseType)ResponseType.class.cast(var3)).getDeniedRequest();
      }

      if (var3 instanceof DeniedRequestType) {
         this.handler.deniedRequest(((DeniedRequestType)DeniedRequestType.class.cast(var3)).getReqId(), var2.getTag());
      }

   }
}
