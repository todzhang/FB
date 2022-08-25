package ds.core.commanddispatcher;

import ddb.util.BlockingInputStream;
import ds.core.controller.MutableCoreController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ObserveOngoingOperationCommandDispatcher extends ReplayCommandDispatcher implements CommandDispatcher {
   public ObserveOngoingOperationCommandDispatcher(EventPublisher var1, MutableCoreController var2, File var3) {
      super(var1, var2, var3);
   }

   @Override
   protected InputStream getFileStream(String file) throws FileNotFoundException {
      return new BlockingInputStream(super.getFileStream(file));
   }

   @Override
   protected boolean noWaiting() {
      return true;
   }
}
