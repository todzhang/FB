package ddb.dsz.core.connection.events;

import ddb.dsz.core.connection.ConnectionChangeEvent;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class StatisticsEvent extends ConnectionChangeEvent {
   int requestId;
   List<StatisticsEvent.Host> hosts = new Vector();

   public StatisticsEvent(Object source, int requestId) {
      super(source);
      this.requestId = requestId;
   }

   public void addHost(String id, BigInteger sent, BigInteger received) {
      this.hosts.add(new StatisticsEvent.Host(id, sent, received));
   }

   public int getRequestId() {
      return this.requestId;
   }

   public List<StatisticsEvent.Host> getHosts() {
      return Collections.unmodifiableList(this.hosts);
   }

   public class Host {
      public String id;
      BigInteger sent;
      BigInteger received;

      public Host(String id, BigInteger sent, BigInteger received) {
         this.id = id;
         this.sent = sent;
         this.received = received;
      }

      public String getId() {
         return this.id;
      }

      public BigInteger getReceived() {
         return this.received;
      }

      public BigInteger getSent() {
         return this.sent;
      }
   }
}
