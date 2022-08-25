package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import java.util.List;
import java.util.Vector;

public class Traceroute extends Data {
   String location;
   List<Traceroute.Hop> hops = new Vector();

   public Traceroute(ObjectValue var1) {
      super(var1);
      this.location = var1.getString(Traceroute.TracerouteConstants.Location.text);
   }

   public int addHop(ObjectValue var1) {
      int var2 = this.hops.size();
      this.hops.add(new Traceroute.Hop(var1));
      return var2;
   }

   public List<Traceroute.Hop> getHops() {
      return this.hops;
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String var1) {
      this.location = var1;
   }

   public class Hop extends Data implements Comparable<Traceroute.Hop> {
      String address;
      long time;
      int hop;

      public Hop(ObjectValue var2) {
         super(var2);
         this.address = var2.getString(Traceroute.TracerouteConstants.Host.text);
         this.time = var2.getInteger(Traceroute.TracerouteConstants.Time.text);
         this.hop = var2.getInteger(Traceroute.TracerouteConstants.Hop.text).intValue();
      }

      public String getAddress() {
         return this.address;
      }

      public void setAddress(String var1) {
         this.address = var1;
      }

      public int getHop() {
         return this.hop;
      }

      public void setHop(int var1) {
         this.hop = var1;
      }

      public long getTime() {
         return this.time;
      }

      public void setTime(long var1) {
         this.time = var1;
      }

      public int compareTo(Traceroute.Hop var1) {
         return var1 == null ? -1 : ((Integer)Integer.class.cast(this.hop)).compareTo(var1.hop);
      }
   }

   public static enum TracerouteConstants {
      Location,
      Time,
      Hop,
      Host;

      public final String text;

      private TracerouteConstants() {
         this.text = this.name();
      }

      private TracerouteConstants(String var3) {
         this.text = var3;
      }
   }
}
