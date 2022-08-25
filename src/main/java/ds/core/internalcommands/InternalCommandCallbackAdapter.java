package ds.core.internalcommands;

import ddb.dsz.core.internalcommand.InternalCommandCallback;
import java.util.List;

public class InternalCommandCallbackAdapter implements InternalCommandCallback {
   public static final InternalCommandCallbackAdapter INSTANCE = new InternalCommandCallbackAdapter();

   @Override
   public void taskingRecieved(List<String> var1, Object var2) {
   }

   @Override
   public void taskingExecuted(Object var1, Object var2) {
   }

   @Override
   public void taskingRejected(Object var1, Object var2) {
   }
}
