package ddb.dsz.core.connection.events;

import ddb.dsz.core.host.HostInfo;

public class DisconnectEvent extends HostEvent {
   public DisconnectEvent(Object source, HostInfo host) {
      super(source, host);
      this.host = host;
   }
}
