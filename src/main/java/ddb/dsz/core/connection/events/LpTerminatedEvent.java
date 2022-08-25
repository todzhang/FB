package ddb.dsz.core.connection.events;

import ddb.dsz.core.connection.ConnectionChangeEvent;

public class LpTerminatedEvent extends ConnectionChangeEvent {
   public LpTerminatedEvent(Object source) {
      super(source);
   }
}
