package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.netmapviewer.NetmapNodeType;
import ddb.dsz.plugin.netmapviewer.NetmapViewerHost;
import ddb.util.GeneralUtilities;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Netmap extends Data {
   private long level;
   private long osVersionMajor;
   private long osVersionMinor;
   private long timeOffset;
   private long timeZone = 0L;
   private String name;
   private String localName;
   private String provider;
   private NetmapNodeType type;
   private String parent;
   private String comment;
   private String osPlatform;
   private SortedSet<String> addresses;
   private SortedSet<String> software;
   private long reportedTime;

   public long getTimeOffset() {
      return this.timeOffset;
   }

   public long getTimeZone() {
      return this.timeZone;
   }

   public Collection<String> getAddresses() {
      return Collections.unmodifiableSortedSet(this.addresses);
   }

   public Collection<String> getSoftware() {
      return Collections.unmodifiableSortedSet(this.software);
   }

   public void setOsVersion(long var1, long var3) {
      this.osVersionMajor = var1;
      this.osVersionMinor = var3;
   }

   public long getMajorVersion() {
      return this.osVersionMajor;
   }

   public long getMinorVersion() {
      return this.osVersionMinor;
   }

   public void setTimeOffset(long var1) {
      this.timeOffset = var1;
   }

   public void setTimeZone(long var1) {
      this.timeZone = var1;
   }

   public void addAddresses(List<String> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.addresses.add(var3);
      }

   }

   public void addAddresses(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.addresses.add(var5);
      }

   }

   public void addSoftware(List<String> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.software.add(var3);
      }

   }

   public void addSoftware(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.software.add(var5);
      }

   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public long getLevel() {
      return this.level;
   }

   public void setLevel(long var1) {
      this.level = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      if (var1.startsWith("\\\\")) {
         var1 = var1.substring(2);
      }

      try {
         if (var1.matches("^[0-9]{1,3}\\..*$")) {
            this.addAddresses(var1);
            if (this.name != null) {
               return;
            }
         }
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

      this.name = var1;
   }

   public String getParent() {
      return this.parent;
   }

   public void setParent(String var1) {
      this.parent = var1;
   }

   public String getProvider() {
      return this.provider;
   }

   public void setProvider(String var1) {
      this.provider = var1;
   }

   public NetmapNodeType getNodeType() {
      return this.type;
   }

   public void setNodeType(NetmapNodeType var1) {
      this.type = var1;
   }

   public void setNodeType(String var1) {
      NetmapNodeType[] var2 = NetmapNodeType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         NetmapNodeType var5 = var2[var4];
         if (var5.match(var1)) {
            this.setNodeType(var5);
            return;
         }
      }

   }

   public void setOsPlatform(String var1) {
      this.osPlatform = var1;
   }

   public String getOsPlatform() {
      return this.osPlatform;
   }

   public String getLocalName() {
      return this.localName;
   }

   public void setLocalName(String var1) {
      this.localName = var1;
   }

   public long getReportedTime() {
      return this.reportedTime;
   }

   public void setReportedTime(long var1) {
      this.reportedTime = var1;
   }

   public Netmap(ObjectValue var1) {
      super(var1);
      this.addresses = new TreeSet(String.CASE_INSENSITIVE_ORDER);
      this.software = new TreeSet(String.CASE_INSENSITIVE_ORDER);
      this.setLevel(var1.getInteger(Netmap.NetmapConstants.LEVEL.text));
      this.setName(var1.getString(Netmap.NetmapConstants.REMOTE_NAME.text));
      this.setProvider(var1.getString(Netmap.NetmapConstants.PROVIDER.text));
      this.setNodeType(var1.getString(Netmap.NetmapConstants.TYPE.text));
      this.setComment(var1.getString(Netmap.NetmapConstants.COMMENT.text));
      this.setParent(var1.getString(Netmap.NetmapConstants.PARENT.text));
      this.setLocalName(var1.getString(Netmap.NetmapConstants.LOCAL_NAME.text));
      this.addSoftware(var1.getStrings(Netmap.NetmapConstants.SOFTWARE.text));
      this.setOsPlatform(var1.getString(Netmap.NetmapConstants.OS_PLATFORM.text));

      try {
         if (var1.getInteger(Netmap.NetmapConstants.OS_VERSION_MAJOR.text) != null && var1.getInteger(Netmap.NetmapConstants.OS_VERSION_MINOR.text) != null) {
            this.setOsVersion(var1.getInteger(Netmap.NetmapConstants.OS_VERSION_MAJOR.text), var1.getInteger(Netmap.NetmapConstants.OS_VERSION_MINOR.text));
         }
      } catch (NullPointerException var7) {
      }

      String var2 = var1.getString(Netmap.NetmapConstants.TIME.text);
      String var3 = var1.getString(Netmap.NetmapConstants.TIME_ZONE_OFFSET.text);
      if (var2 != null && var2.length() > 0) {
         Calendar var4 = GeneralUtilities.stringToCalendar(var2, (Calendar)null);
         long var5 = var4.getTimeInMillis() - super.getLpTimestamp();
         if (var5 == 0L) {
            var5 = -1L;
         }

         this.setTimeOffset(var5);
         this.setTimeZone(NetmapViewerHost.stringToMillisecondDuration(var3));
         this.setReportedTime(super.getLpTimestamp());
      }

      this.addAddresses(var1.getStrings(Netmap.NetmapConstants.IP.text));
   }

   public static enum NetmapConstants {
      LEVEL,
      TIME,
      OS_PLATFORM("osplatform"),
      REMOTE_NAME("remotename"),
      TIME_ZONE_OFFSET("timezoneoffset"),
      PROVIDER,
      PARENT("parentname"),
      TYPE,
      COMMENT,
      LOCAL_NAME("localname"),
      IP,
      OS_VERSION_MAJOR("osversionmajor"),
      OS_VERSION_MINOR("osversionminor"),
      SOFTWARE("software::description");

      public final String text;

      private NetmapConstants() {
         this.text = this.name();
      }

      private NetmapConstants(String var3) {
         this.text = var3;
      }
   }
}
