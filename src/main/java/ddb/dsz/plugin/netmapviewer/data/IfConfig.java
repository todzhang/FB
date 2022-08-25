package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.TaskId;
import ddb.util.GeneralUtilities;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class IfConfig {
   TaskId taskid;
   String hostName;
   String domainName;
   List<IfConfig.DnsServer> DnsServers = new Vector();
   List<IfConfig.Interface> interfaces = new Vector();

   public IfConfig(ObjectValue var1, TaskId var2) {
      this.taskid = var2;
      this.hostName = var1.getString("FixedDataItem::HostName");
      this.domainName = var1.getString("FixedDataItem::DomainName");
      Iterator var3 = var1.getObjects("FixedDataItem::DnsServers::DnsServer").iterator();

      ObjectValue var4;
      while(var3.hasNext()) {
         var4 = (ObjectValue)var3.next();
         this.DnsServers.add(new IfConfig.DnsServer(var4.getString("broadcast"), var4.getString("mask"), var4.getString("ip")));
      }

      var3 = var1.getObjects("InterfaceItem").iterator();

      while(var3.hasNext()) {
         var4 = (ObjectValue)var3.next();
         this.interfaces.add(new IfConfig.Interface(var4));
      }

   }

   public List<IfConfig.DnsServer> getDnsServers() {
      return Collections.unmodifiableList(this.DnsServers);
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getHostName() {
      return this.hostName;
   }

   public List<IfConfig.Interface> getInterfaces() {
      return this.interfaces;
   }

   public TaskId getTaskid() {
      return this.taskid;
   }

   public List<String> getAddresses() {
      Vector var1 = new Vector();
      var1.add(this.hostName.toLowerCase());
      Iterator var2 = this.interfaces.iterator();

      while(var2.hasNext()) {
         IfConfig.Interface var3 = (IfConfig.Interface)var2.next();
         var1.addAll(var3.getIpAddresses());
         var1.add(var3.getPhysicalAddress());
      }

      return var1;
   }

   public class Interface {
      private final String description;
      private final String physicalAddress;
      private final boolean dhcpEnabled;
      List<String> ipAddresses = new Vector();
      private final String dhcpServer;
      private final Calendar leaseObtained;
      private final Calendar leaseExpires;

      public Interface(ObjectValue var2) {
         this.description = var2.getString("Description");
         this.physicalAddress = var2.getString("address");
         this.dhcpEnabled = var2.getBoolean("dhcpEnabled");
         this.dhcpServer = var2.getString("dhcp::ip");
         this.leaseObtained = GeneralUtilities.stringToCalendar(String.format("%sT%s", var2.getString("lease::obtained::date"), var2.getString("lease::obtained::time")), (Calendar)null);
         this.leaseExpires = GeneralUtilities.stringToCalendar(String.format("%sT%s", var2.getString("lease::expires::date"), var2.getString("lease::expires::time")), (Calendar)null);
         Iterator var3 = var2.getObjects("ipaddress").iterator();

         while(var3.hasNext()) {
            ObjectValue var4 = (ObjectValue)var3.next();
            this.ipAddresses.add(var4.getString("ip"));
         }

      }

      public String getDescription() {
         return this.description;
      }

      public boolean isDhcpEnabled() {
         return this.dhcpEnabled;
      }

      public String getDhcpServer() {
         return this.dhcpServer;
      }

      public List<String> getIpAddresses() {
         return Collections.unmodifiableList(this.ipAddresses);
      }

      public Calendar getLeaseExpires() {
         return this.leaseExpires;
      }

      public Calendar getLeaseObtained() {
         return this.leaseObtained;
      }

      public String getPhysicalAddress() {
         return this.physicalAddress;
      }
   }

   public class DnsServer {
      private final String broadcast;
      private final String mask;
      private final String ip;

      public DnsServer(String var2, String var3, String var4) {
         this.broadcast = var2;
         this.mask = var3;
         this.ip = var4;
      }

      public String getBroadcast() {
         return this.broadcast;
      }

      public String getIp() {
         return this.ip;
      }

      public String getMask() {
         return this.mask;
      }
   }
}
