package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;

public class Ping extends Data {
   String destination;
   Long ttl;
   Long elapsed;
   Long length;

   public Ping(ObjectValue var1) {
      super(var1);
      this.elapsed = var1.getInteger(Ping.PingConstants.Elapsed.text);
      this.length = var1.getInteger(Ping.PingConstants.Length.text);
      this.ttl = var1.getInteger(Ping.PingConstants.Ttl.text);
      this.destination = var1.getString(Ping.PingConstants.Destination.text);
   }

   public String getDestination() {
      return this.destination;
   }

   public void setDestination(String var1) {
      this.destination = var1;
   }

   public Long getElapsed() {
      return this.elapsed;
   }

   public void setElapsed(long var1) {
      this.elapsed = var1;
   }

   public Long getLength() {
      return this.length;
   }

   public void setLength(long var1) {
      this.length = var1;
   }

   public Long getTtl() {
      return this.ttl;
   }

   public void setTtl(long var1) {
      this.ttl = var1;
   }

   public static enum PingConstants {
      Elapsed,
      Length,
      Ttl,
      Destination("fromaddr::addr");

      public final String text;

      private PingConstants() {
         this.text = this.name();
      }

      private PingConstants(String var3) {
         this.text = var3;
      }
   }
}
