package ddb.dsz.core.connection.events;

import ddb.dsz.core.connection.ConnectionChangeEvent;

public class ThrottleEvent extends ConnectionChangeEvent {
   String address;
   int bytes;

   public ThrottleEvent(Object source, String address, int bytes) {
      super(source);
      this.address = address;
      this.bytes = bytes;
   }

   public String getAddress() {
      return this.address;
   }

   public int getBytes() {
      return this.bytes;
   }
}
