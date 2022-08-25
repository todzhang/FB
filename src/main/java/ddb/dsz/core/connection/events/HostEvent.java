package ddb.dsz.core.connection.events;

import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.host.HostInfo;

public abstract class HostEvent extends ConnectionChangeEvent {
   HostInfo host;

   public HostEvent(Object source, HostInfo host) {
      super(source);
      this.host = host;
   }

   public HostInfo getHost() {
      return this.host;
   }
}
