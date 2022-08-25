package ds.core.commanddispatcher;

import ddb.dsz.core.operation.Operation;

public interface CommandDispatcher {
   Operation getOperation();

   void stop();
}
