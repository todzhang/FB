package ds.core.impl;

import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.host.MutableHostInfo;
import ddb.dsz.core.task.TaskId;
import java.util.Calendar;

public class HostInfoImpl implements MutableHostInfo {
   private boolean local;
   private boolean connected;
   private String id;
   private String hostname;
   private String version;
   private String platform;
   private String implantType;
   private String arch;
   private long modified;
   private TaskId task;

   public HostInfoImpl(String var1, boolean var2) {
      this(var1, "unknown", "unknown", "unknown", "unknown", "unknown", var2, false);
   }

   public HostInfoImpl(String var1, String var2, String var3, String var4, String var5, String var6, boolean var7, boolean var8) {
      this.hostname = var2;
      this.version = var3;
      this.platform = var5;
      this.implantType = var6;
      this.arch = var4;
      this.local = var7;
      this.id = var1;
      this.connected = var8;
      this.modified = Calendar.getInstance().getTimeInMillis();
   }

   public HostInfoImpl(String var1, String var2, String var3, String var4) {
      this(var1, "unknown", var2, (String)null, var3, var4, false, true);
   }

   @Override
   public TaskId getTask() {
      return this.task;
   }

   @Override
   public void setTask(TaskId taskId) {
      this.task = taskId;
   }

   @Override
   public String getImplantType() {
      return this.implantType;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public String getPlatform() {
      return this.platform;
   }

   @Override
   public String getVersion() {
      return this.version;
   }

   @Override
   public String getArch() {
      return this.arch;
   }

   @Override
   public boolean isConnected() {
      return this.connected;
   }

   @Override
   public boolean isLocal() {
      return this.local;
   }

   @Override
   public void copyFromHost(HostInfo hostInfo) {
      if (hostInfo != null) {
         this.arch = hostInfo.getArch();
         this.implantType = hostInfo.getImplantType();
         this.platform = hostInfo.getPlatform();
         this.version = hostInfo.getVersion();
         this.connected = hostInfo.isConnected();
         this.modified = hostInfo.getModifiedTime().getTimeInMillis();
      }
   }

   @Override
   public boolean sameHost(HostInfo hostInfo) {
      if (hostInfo == null) {
         return false;
      } else if (this.id == null && hostInfo.getId() == null) {
         return true;
      } else {
         return this.id != null && hostInfo.getId() != null ? this.id.equals(hostInfo.getId()) : false;
      }
   }

   public String toString() {
      return String.format("HostInfo[id=%s]", this.id);
   }

   @Override
   public Calendar getModifiedTime() {
      Calendar var1 = Calendar.getInstance();
      var1.setTimeInMillis(this.modified);
      return var1;
   }

   @Override
   public void setArch(String arch) {
      this.arch = arch;
   }

   @Override
   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   @Override
   public void setId(String id) {
      this.id = id;
   }

   @Override
   public void setImplantType(String implantType) {
      this.implantType = implantType;
   }

   @Override
   public void setLocal(boolean local) {
      this.local = local;
   }

   @Override
   public void setPlatform(String platform) {
      this.platform = platform;
   }

   @Override
   public void setModified(Calendar modified) {
      this.modified = modified.getTimeInMillis();
   }

   @Override
   public void setVersion(String version) {
      this.version = version;
   }

   @Override
   public void setHostname(String hostname) {
      this.hostname = hostname;
   }

   @Override
   public String getHostname() {
      return this.hostname;
   }

   public int compareTo(HostInfo var1) {
      return HostInfo.COMPARE.compare(this, var1);
   }
}
