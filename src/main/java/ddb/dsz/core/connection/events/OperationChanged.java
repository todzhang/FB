package ddb.dsz.core.connection.events;

import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.operation.Operation;

public class OperationChanged extends ConnectionChangeEvent {
   Operation operation;

   public OperationChanged(Object source, Operation operation) {
      super(source);
      this.operation = operation;
   }

   public Operation getOperation() {
      return this.operation;
   }
}
