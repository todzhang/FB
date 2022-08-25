package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.CloseType;
import javax.xml.bind.JAXBElement;

public class CloseConnection extends RequestTransformer {
   public static CloseConnection INSTANCE = new CloseConnection();

   public static CloseConnection getInstance() {
      return INSTANCE;
   }

   private CloseConnection() {
   }

   @Override
   public JAXBElement<CloseType> transform(Object input) {
      return objFact.createClose(new CloseType());
   }
}
