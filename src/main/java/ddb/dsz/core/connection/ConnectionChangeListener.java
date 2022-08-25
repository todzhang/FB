package ddb.dsz.core.connection;

import ddb.dsz.annotations.DszQueuableMethod;
import java.util.EventListener;

public interface ConnectionChangeListener extends EventListener {
   @DszQueuableMethod
   void connectionChanged(ConnectionChangeEvent connectionChangeEvent);
}
