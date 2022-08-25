package ddb.dsz.core.connection;

import java.util.EventObject;

public abstract class ConnectionChangeEvent extends EventObject {
   protected ConnectionChangeEvent(Object source) {
      super(source);
   }
}
