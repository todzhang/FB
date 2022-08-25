package ddb.dsz.core.internalcommand;

import java.util.List;

public interface InternalCommandCallback {
   void taskingRecieved(List<String> var1, Object var2);

   void taskingExecuted(Object var1, Object var2);

   void taskingRejected(Object var1, Object var2);
}
