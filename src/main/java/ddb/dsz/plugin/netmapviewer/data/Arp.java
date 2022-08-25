package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;

public class Arp extends Data {
   private String inetAddress;
   private String currState;
   private String physAddress;
   private String connInterface;

   public Arp(ObjectValue var1) {
      super(var1);
      this.inetAddress = var1.getString("ip");
      this.currState = var1.getString("state");
      this.physAddress = var1.getString("mac");
      this.connInterface = var1.getString("adapter");
   }

   public String getInetAddress() {
      return this.inetAddress;
   }

   public void setInetAddress(String var1) {
      this.inetAddress = var1;
   }

   public String getState() {
      return this.currState;
   }

   public void setState(String var1) {
      this.currState = var1;
   }

   public String getPhysAddress() {
      return this.physAddress;
   }

   public void setPhysAddress(String var1) {
      this.physAddress = var1;
   }

   public String getInterface() {
      return this.connInterface;
   }

   public void setInterface(String var1) {
      this.connInterface = var1;
   }

   public boolean equals(Object var1) {
      boolean var2 = false;
      if (var1 != null) {
         Arp var3 = (Arp)var1;
         if (this.inetAddress.equals(var3.getInetAddress()) && this.currState.equals(var3.getState()) && this.physAddress.equals(var3.getPhysAddress()) && this.connInterface.equals(var3.getInterface())) {
            var2 = true;
         }
      }

      return var2;
   }

   public int hashCode() {
      byte var1 = 5;
      int var2 = 31 * var1 + (this.inetAddress != null ? this.inetAddress.hashCode() : 0);
      var2 = 31 * var2 + (this.currState != null ? this.currState.hashCode() : 0);
      var2 = 31 * var2 + (this.physAddress != null ? this.physAddress.hashCode() : 0);
      var2 = 31 * var2 + (this.connInterface != null ? this.connInterface.hashCode() : 0);
      return var2;
   }
}
