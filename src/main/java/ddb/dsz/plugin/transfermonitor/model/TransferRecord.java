package ddb.dsz.plugin.transfermonitor.model;

import ddb.dsz.core.task.TaskId;
import java.util.Calendar;

public class TransferRecord {
   private String description = "";
   private int id = -1;
   private String local = "";
   private String subDir = null;
   private String remote = "";
   private Long size = 0L;
   private TransferState state;
   private Long transfered;
   private Calendar modified;
   private Calendar accessed;
   private Calendar created;
   private TransferDirection direction;
   int type;
   boolean resumable;
   TaskId taskId;

   public TransferRecord(int var1, TransferState var2, String var3, String var4, int var5, String var6, TaskId var7) {
      this.state = TransferState.STARTED;
      this.transfered = 0L;
      this.direction = TransferDirection.GET;
      this.type = -1;
      this.resumable = true;
      this.id = var1;
      this.state = var2;
      this.remote = var3;
      this.local = var4;
      this.type = var5;
      this.subDir = var6;
      this.taskId = var7;
   }

   public TransferRecord(int var1, TransferState var2, String var3, String var4, String var5, TaskId var6) {
      this.state = TransferState.STARTED;
      this.transfered = 0L;
      this.direction = TransferDirection.GET;
      this.type = -1;
      this.resumable = true;
      this.id = var1;
      this.state = var2;
      this.remote = var3;
      this.local = var4;
      this.subDir = var5;
      this.taskId = var6;
   }

   public TransferRecord(int var1, String var2, String var3, TaskId var4) {
      this.state = TransferState.STARTED;
      this.transfered = 0L;
      this.direction = TransferDirection.GET;
      this.type = -1;
      this.resumable = true;
      this.id = var1;
      this.remote = var2;
      this.subDir = var3;
      this.taskId = var4;
   }

   public void addTransfered(Long var1) {
      if (var1 == null) {
         System.err.println("Attempting to add a null");
      } else {
         synchronized(this) {
            this.transfered = this.transfered + var1;
         }
      }
   }

   public TransferDirection getDirection() {
      return this.direction;
   }

   public void setDirection(TransferDirection var1) {
      this.direction = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public int getId() {
      return this.id;
   }

   public String getLocal() {
      return this.local;
   }

   public String getSubDir() {
      return this.subDir;
   }

   public String getRemote() {
      return this.remote;
   }

   public Long getSize() {
      return this.size;
   }

   public TransferState getState() {
      switch(this.state) {
      case STARTED:
         if (TransferDirection.PUT.equals(this.direction) && this.size.compareTo(this.transfered) == 0) {
            return TransferState.SUCCESS;
         }

         return TransferState.STARTED;
      case DONE:
         if (this.size == this.transfered) {
            return TransferState.SUCCESS;
         }

         return TransferState.FAILURE;
      default:
         return this.state;
      }
   }

   public Long getTransfered() {
      return this.transfered;
   }

   public int getType() {
      return this.type;
   }

   public void resetTransfered() {
      synchronized(this) {
         this.transfered = 0L;
      }
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setId(int var1) {
      this.id = var1;
   }

   public void setLocal(String var1) {
      this.local = var1;
   }

   public void setSubDir(String var1) {
      this.subDir = var1;
   }

   public void setRemote(String var1) {
      this.remote = var1;
   }

   public void setSize(Long var1) {
      this.size = var1;
   }

   public void setState(TransferState var1) {
      this.state = var1;
   }

   public void setTransfered(Long var1) {
      this.transfered = var1;
   }

   public void setType(int var1) {
      this.type = var1;
   }

   public TaskId getTaskId() {
      return this.taskId;
   }

   public boolean isResumable() {
      return this.resumable && !this.state.equals(TransferState.STARTED) && this.transfered != this.size;
   }

   public void setResumable(boolean var1) {
      this.resumable = var1;
   }

   public Calendar getAccessed() {
      return this.accessed;
   }

   public void setAccessed(Calendar var1) {
      this.accessed = var1;
   }

   public Calendar getCreated() {
      return this.created;
   }

   public void setCreated(Calendar var1) {
      this.created = var1;
   }

   public Calendar getModified() {
      return this.modified;
   }

   public void setModified(Calendar var1) {
      this.modified = var1;
   }
}
