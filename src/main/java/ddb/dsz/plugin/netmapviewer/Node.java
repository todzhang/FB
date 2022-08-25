package ddb.dsz.plugin.netmapviewer;

import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.netmapviewer.data.Arp;
import ddb.dsz.plugin.netmapviewer.data.Group;
import ddb.dsz.plugin.netmapviewer.data.IfConfig;
import ddb.dsz.plugin.netmapviewer.data.Netmap;
import ddb.dsz.plugin.netmapviewer.data.Ping;
import ddb.dsz.plugin.netmapviewer.data.Service;
import ddb.dsz.plugin.netmapviewer.data.Traceroute;
import ddb.dsz.plugin.netmapviewer.data.User;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class Node {
   Node.Order order;
   private HashSet<Arp> arps;
   private HashMap<Task, String> printingCommandsList;
   private Netmap netmapData;
   private IfConfig ifconfigData;
   private List<Ping> pings;
   private List<Resource> resources;
   private List<Traceroute> traceroutes;
   private Set<Group> groups;
   private List<Service> services;
   private Set<User> users;
   private Set<String> addresses;
   private boolean matchable;
   private String name;
   private boolean updated;

   public Node() {
      this.order = Node.Order.Default;
      this.arps = new HashSet();
      this.printingCommandsList = new HashMap();
      this.netmapData = null;
      this.ifconfigData = null;
      this.pings = new Vector();
      this.resources = new Vector();
      this.traceroutes = new Vector();
      this.groups = new TreeSet();
      this.services = new Vector();
      this.users = new TreeSet();
      this.addresses = new HashSet();
      this.matchable = true;
      this.name = "Unknown";
      this.updated = false;
   }

   public boolean isUpdated() {
      return this.updated;
   }

   public void viewed() {
      this.updated = false;
   }

   public void changed() {
      this.updated = true;
   }

   public void removeDataset(Node.DataTypes var1) {
      switch(var1) {
      case ArpType:
         this.arps.clear();
         break;
      case PingType:
         this.pings.clear();
         break;
      case ResourceType:
         this.resources.clear();
         break;
      case TracerouteType:
         this.traceroutes.clear();
      }

   }

   public void setMatchable(boolean var1) {
      this.matchable = var1;
   }

   public Collection<String> getAddresses() {
      if (!this.matchable) {
         return Collections.emptyList();
      } else {
         HashSet var1 = new HashSet();
         var1.addAll(this.addresses);
         Iterator var2 = this.getArps().iterator();

         while(var2.hasNext()) {
            Arp var3 = (Arp)var2.next();
            var1.add(var3.getInetAddress());
            var1.add(var3.getPhysAddress());
         }

         if (this.getNetmapData() != null) {
            var1.addAll(this.getNetmapData().getAddresses());
         }

         var2 = this.getPings().iterator();

         while(var2.hasNext()) {
            Ping var4 = (Ping)var2.next();
            var1.add(var4.getDestination());
         }

         var2 = this.getTraceroutes().iterator();

         while(var2.hasNext()) {
            Traceroute var5 = (Traceroute)var2.next();
            var1.add(var5.getLocation());
         }

         if (this.ifconfigData != null) {
            var1.addAll(this.ifconfigData.getAddresses());
         }

         return var1;
      }
   }

   public HashSet<Arp> getArps() {
      return this.arps;
   }

   public void addArp(Arp var1) {
      if (this.arps.add(var1)) {
         this.updated = true;
      }

   }

   public void setIfconfig(IfConfig var1) {
      this.ifconfigData = var1;
   }

   public IfConfig getIfConfig() {
      return this.ifconfigData;
   }

   public void addAddress(String var1) {
      this.addresses.add(var1);
   }

   public synchronized List<Task> getPrintedCommands() {
      Vector var1 = new Vector();
      var1.addAll(this.printingCommandsList.keySet());
      Collections.sort(var1, Task.TaskComparator);
      return var1;
   }

   public synchronized String getPrintedCommand(Task var1) {
      return (String)this.printingCommandsList.get(var1);
   }

   public synchronized void addPrintingCommandEntry(Task var1, String var2) {
      String var3 = (String)this.printingCommandsList.get(var1);
      if (var3 != null) {
         this.printingCommandsList.put(var1, String.format("%s%s", var3, var2));
      } else {
         this.printingCommandsList.put(var1, var2);
      }

      this.updated = true;
   }

   public Netmap getNetmapData() {
      return this.netmapData;
   }

   public void addService(Service var1) {
      this.services.add(var1);
      this.updated = true;
   }

   public List<Service> getServices() {
      return this.services;
   }

   public void addUser(User var1) {
      this.users.add(var1);
      this.updated = true;
   }

   public List<User> getUsers() {
      Vector var1 = new Vector();
      var1.addAll(this.users);
      return var1;
   }

   public List<Group> getGroups() {
      Vector var1 = new Vector();
      var1.addAll(this.groups);
      return var1;
   }

   public void addGroup(Group var1) {
      this.groups.add(var1);
      this.updated = true;
   }

   public void setNetmapData(Netmap var1) {
      this.netmapData = var1;
      this.updated = true;
   }

   public void addPing(Ping var1) {
      this.pings.add(var1);
      this.updated = true;
   }

   public List<Ping> getPings() {
      return this.pings;
   }

   public List<Resource> getResources() {
      return this.resources;
   }

   public void addResource(Resource var1) {
      this.resources.add(var1);
      this.updated = true;
   }

   public boolean doesNameMatch(String var1) {
      if (var1 == null) {
         if (this.name == null) {
            return true;
         }
      } else if (var1.equals(this.name)) {
         return true;
      }

      if (this.netmapData != null) {
         return this.netmapData.getName().equalsIgnoreCase(var1);
      } else {
         return this.name.equalsIgnoreCase(var1);
      }
   }

   public boolean doesAddressMatch(String var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.getAddresses().contains(var1.toLowerCase());
      }
   }

   public List<Traceroute> getTraceroutes() {
      return this.traceroutes;
   }

   public void addTraceroute(Traceroute var1) {
      if (!this.traceroutes.contains(var1)) {
         this.traceroutes.add(var1);
         this.updated = true;
      }
   }

   public String toString() {
      if (this.ifconfigData != null) {
         return this.ifconfigData.getHostName();
      } else if (this.netmapData != null) {
         return this.netmapData.getName();
      } else {
         return this.pings.size() > 0 ? ((Ping)this.pings.get(this.pings.size() - 1)).getDestination() : this.name;
      }
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setOrder(Node.Order var1) {
      this.order = var1;
   }

   public Node.Order getOrder() {
      return this.order;
   }

   public void CopyNode(Node var1) {
      if (this.netmapData == null) {
         this.netmapData = var1.netmapData;
      }

      try {
         Field[] var2 = this.getClass().getDeclaredFields();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field var5 = var2[var4];

            try {
               Object var6 = var5.get(this);
               if (var6 != null) {
                  Object var7 = var5.get(var1);
                  Iterator var10;
                  Object var11;
                  if (var6 instanceof List && var7 instanceof List) {
                     List var8 = (List)var6;
                     List var9 = (List)var7;
                     var10 = var9.iterator();

                     while(var10.hasNext()) {
                        var11 = var10.next();
                        var8.add(var11);
                     }
                  }

                  if (var6 instanceof Map && var7 instanceof Map) {
                     Map var14 = (Map)var6;
                     Map var16 = (Map)var7;
                     var10 = var16.keySet().iterator();

                     while(var10.hasNext()) {
                        var11 = var10.next();
                        var14.put(var11, var16.get(var11));
                     }
                  }

                  if (var6 instanceof Set && var7 instanceof Set) {
                     Set var15 = (Set)var6;
                     Set var17 = (Set)var7;
                     var10 = var17.iterator();

                     while(var10.hasNext()) {
                        var11 = var10.next();
                        var15.add(var11);
                     }
                  }
               }
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }
      } catch (Exception var13) {
      }

   }

   public static enum DataTypes {
      ArpType,
      GroupType,
      PingType,
      ResourceType,
      ServiceType,
      TracerouteType,
      UserType;
   }

   public static enum Order {
      Before,
      Default,
      After;
   }
}
