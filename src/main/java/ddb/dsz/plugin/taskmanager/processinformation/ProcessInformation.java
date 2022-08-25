package ddb.dsz.plugin.taskmanager.processinformation;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.BasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.generator.Generator;
import ddb.dsz.plugin.taskmanager.processinformation.generator.WindowsGenerator;
import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import ddb.util.GeneralUtilities;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.Closure;

public class ProcessInformation extends Observable implements Comparable<ProcessInformation> {
   private long id;
   private String procName;
   private String procPath;
   private String userName;
   private long _cpuTime;
   private long createTime;
   private String display;
   private long parent;
   private BasicInfo basicInfo;
   private Group[] groups;
   private final HostInfo host;
   private boolean processInfo;
   private boolean handleInfo;
   private CoreController core;
   private boolean highlight;
   private FileStatus fileStatus;
   private String comment;
   private boolean is64Bit;

   public FileStatus getType() {
      return this.fileStatus;
   }

   public String getComment() {
      return this.comment;
   }

   public void setType(FileStatus type) {
      this.fileStatus = type;
      this.setChanged();
      this.notifyObservers();
   }

   public void setComment(String comment) {
      this.comment = comment;
      this.setChanged();
      this.notifyObservers();
   }

   public ProcessInformation(ObjectValue process, CoreController cc, HostInfo host) {
      this(cc, host);
      this.load(process);
   }

   public ProcessInformation(CoreController cc, HostInfo host) {
      this.id = -1L;
      this.procName = null;
      this.procPath = null;
      this.userName = null;
      this._cpuTime = 0L;
      this.createTime = 0L;
      this.display = null;
      this.parent = -1L;
      this.groups = new Group[0];
      this.processInfo = false;
      this.handleInfo = false;
      this.highlight = false;
      this.fileStatus = FileStatus.NONE;
      this.comment = "";
      this.is64Bit = false;
      this.core = cc;
      this.host = host;
   }

   public void SetHighlight() {
      this.highlight = true;
      this.core.schedule(new Runnable() {
         public void run() {
            ProcessInformation.this.highlight = false;
            ProcessInformation.this.setChanged();
            ProcessInformation.this.notifyObservers();
         }
      }, 60L, TimeUnit.SECONDS);
   }

   public boolean isHighlight() {
      return this.highlight;
   }

   public String toString() {
      return "cpuTime: " + this._cpuTime + " createTime: " + this.createTime + " display: " + this.display + " id: " + this.id + " parent: " + this.parent + " userName: " + this.userName + " procName: " + this.procName;
   }

   public boolean equals(Object o) {
      if (!(o instanceof ProcessInformation)) {
         return false;
      } else {
         return this.compareTo((ProcessInformation)ProcessInformation.class.cast(o)) == 0;
      }
   }

   public int hashCode() {
      int hash = 5;
      hash = 17 * hash + (int)(this.id ^ this.id >>> 32);
      hash = 17 * hash + (this.procName != null ? this.procName.hashCode() : 0);
      hash = 17 * hash + (this.procPath != null ? this.procPath.hashCode() : 0);
      hash = 17 * hash + (int)(this.createTime ^ this.createTime >>> 32);
      hash = 17 * hash + (int)(this.parent ^ this.parent >>> 32);
      return hash;
   }

   public int compareTo(ProcessInformation other) {
      long r = this.id - other.id;
      if (r < 0L) {
         return -1;
      } else {
         return r == 0L ? 0 : 1;
      }
   }

   public Long getCpuTime() {
      return this._cpuTime;
   }

   public void setCpuTime(Long cpuTime) {
      this._cpuTime = cpuTime;
      this.setChanged();
      this.notifyObservers();
   }

   public Calendar getCreateTime() {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(this.createTime);
      return cal;
   }

   public void setCreateTime(Calendar createTime) {
      this.createTime = createTime.getTimeInMillis();
      this.setChanged();
      this.notifyObservers();
   }

   public String getDisplay() {
      return this.display;
   }

   public void setDisplay(String display) {
      this.display = display;
      this.setChanged();
      this.notifyObservers();
   }

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
      this.setChanged();
      this.notifyObservers();
   }

   public Long getParent() {
      return this.parent;
   }

   public void setParent(Long parent) {
      this.parent = parent;
      this.setChanged();
      this.notifyObservers();
   }

   public String getProcName() {
      return this.procName;
   }

   public void setProcName(String procName) {
      this.procName = procName;
      this.setChanged();
      this.notifyObservers();
   }

   public String getUserName() {
      return this.userName;
   }

   private void setUser(String user) {
      this.userName = user;
      this.setChanged();
      this.notifyObservers();
   }

   public void setAs(ProcessInformation p) {
      this.setCpuTime(p._cpuTime);
      this.createTime = p.createTime;
      if (p.display != null && p.display.length() > 0) {
         this.setDisplay(p.display);
      }

      this.setId(p.id);
      if (p.userName != null && p.userName.length() > 0) {
         this.setUser(p.userName);
      }

      if (p.procName != null && p.procName.length() > 0) {
         this.setProcName(p.procName);
      }

   }

   public void load(ObjectValue process) {
      this.setProcName(process.getString("name"));
      this.setProcPath(process.getString("path"));
      this.setParent(process.getInteger("parentId"));
      this.setDisplay(process.getString("display"));
      this.setUser(process.getString("user"));
      this.setId(process.getInteger("id"));
      Long cpuTime = 0L;
      if (process.getInteger("cputime::hours") != null) {
         cpuTime = cpuTime + process.getInteger("cputime::hours");
         cpuTime = cpuTime * 24L;
      }

      if (process.getInteger("cputime::minutes") != null) {
         cpuTime = cpuTime + process.getInteger("cputime::minutes");
         cpuTime = cpuTime * 60L;
      }

      if (process.getInteger("cputime::seconds") != null) {
         cpuTime = cpuTime + process.getInteger("cputime::seconds");
      }

      this.setCpuTime(cpuTime);
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(this.createTime);
      this.createTime = GeneralUtilities.stringToCalendar(String.format("%sT%s", process.getString("created::date"), process.getString("created::time")), cal).getTimeInMillis();
      this.set64Bit(process.getBoolean("is64bit"));
   }

   public String getProcPath() {
      return this.procPath;
   }

   public void setProcPath(String procPath) {
      this.procPath = procPath;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public BasicInfo getBasicInfo() {
      return this.basicInfo;
   }

   public synchronized List<Group> getGroups() {
      return Arrays.asList(this.groups);
   }

   public synchronized List<Module> getModules() {
      return this.getModules((Closure)null);
   }

   public synchronized List<Module> getModules(Closure eventQueueClosure) {
      return ProcessDatabase.GetInstance().getModules(this.host, this.id, eventQueueClosure);
   }

   public synchronized List<Privilege> getPrivileges() {
      return this.getPrivileges((Closure)null);
   }

   public synchronized List<Privilege> getPrivileges(Closure eventQueueClosure) {
      return ProcessDatabase.GetInstance().getPrivileges(this.host, this.id, eventQueueClosure);
   }

   public void setProcessInformation(Task task, ObjectValue procInfo) {
      if (task != null && procInfo != null) {
         Generator g = new WindowsGenerator(this.core);
         synchronized(this) {
            this.basicInfo = g.newBasicInfo(procInfo.getObject("basicinfo"));
            List<ObjectValue> list = procInfo.getObjects("groups::group");
            this.groups = new Group[list.size()];

            for(int i = 0; i < list.size(); ++i) {
               this.groups[i] = g.newGroup((ObjectValue)list.get(i));
            }

            list = procInfo.getObjects("modules::module");
            Module[] modules = new Module[list.size()];

            for(int i = 0; i < list.size(); ++i) {
               modules[i] = g.newModule((ObjectValue)list.get(i));
            }

            ProcessDatabase.GetInstance().addModules(this.host, this.id, Arrays.asList(modules));
            list = procInfo.getObjects("privileges::privilege");
            Privilege[] privileges = new Privilege[list.size()];

            for(int i = 0; i < list.size(); ++i) {
               privileges[i] = g.newPrivilege((ObjectValue)list.get(i));
            }

            ProcessDatabase.GetInstance().addPrivileges(this.host, this.id, Arrays.asList(privileges));
            this.processInfo = true;
         }
      }
   }

   public void addHandle(Handle handle) {
      if (handle != null) {
         ProcessDatabase.GetInstance().addHandle(this.host, this.id, handle);
         this.handleInfo = true;
      }
   }

   public synchronized List<Handle> getHandles(Closure eventQueueClosure) {
      return ProcessDatabase.GetInstance().getHandles(this.host, this.id, eventQueueClosure);
   }

   public boolean hasProcessInfo() {
      return this.processInfo;
   }

   public boolean hasHandleInfo() {
      return this.handleInfo;
   }

   public boolean hasModuleNamed(String file) {
      Iterator i$ = this.getModules().iterator();

      Module m;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         m = (Module)i$.next();
      } while(!m.getName().equals(file));

      return true;
   }

   public boolean is64Bit() {
      return this.is64Bit;
   }

   public void set64Bit(boolean b) {
      this.is64Bit = b;
   }
}
