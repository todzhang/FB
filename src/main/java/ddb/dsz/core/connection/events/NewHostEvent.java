package ddb.dsz.core.connection.events;

import ddb.dsz.core.host.HostInfo;

public class NewHostEvent extends HostEvent {
   public NewHostEvent(Object source, HostInfo host) {
      super(source, host);
      this.host = host;
   }
}
