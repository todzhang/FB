package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.NewRequestType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.OperationOptionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.RequestType;
import ddb.dsz.plugin.requesthandler.model.RequestTableModel;
import javax.xml.bind.JAXBElement;

public class RecordEntryToRequest extends RecordToOperationOption {
   public static final RecordEntryToRequest INSTANCE = new RecordEntryToRequest();

   public static final RecordEntryToRequest getInstance() {
      return INSTANCE;
   }

   private RecordEntryToRequest() {
   }

   @Override
   public JAXBElement<RequestType> transform(Object input) {
      if (!(input instanceof RequestTableModel.RecordEntry)) {
         return null;
      } else {
         RequestTableModel.RecordEntry var2 = (RequestTableModel.RecordEntry)input;
         return RequestedOperationToRequestType.getInstance().transform(var2.operation);
      }
   }

   @Override
   protected Class<? extends OperationOptionType> getType() {
      return NewRequestType.class;
   }
}
