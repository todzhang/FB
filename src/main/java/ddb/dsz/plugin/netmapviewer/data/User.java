package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.TaskId;

public class User extends Data implements Comparable {
   private String userName;
   private String userId;
   private String comment;
   private String homeDir;
   private String userShell;
   private String groupId;
   private String numLogins;
   private String passwordExpiration;
   private String userPrivileges;
   private String acctExpiration;
   private String pwdChanged;
   private String target;
   private TaskId taskId;

   public User(ObjectValue var1, String var2, TaskId var3) {
      super(var1);
      this.userName = var1.getString("name");
      this.userId = var1.getString("userId");
      this.comment = var1.getString("comment");
      this.homeDir = var1.getString("homeDir");
      this.userShell = var1.getString("userShell");
      this.groupId = var1.getString("primaryGroupId");
      this.numLogins = var1.getInteger("numLogons").toString();
      this.passwordExpiration = var1.getString("passwordExpired");
      this.userPrivileges = var1.getString("privilege");
      this.acctExpiration = var1.getString("accountExpires");
      this.pwdChanged = var1.getString("passwordLastChanged");
      this.target = var2;
      this.taskId = var3;
   }

   public void setUserName(String var1) {
      this.userName = var1;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserId(String var1) {
      this.userId = var1;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public String getComment() {
      return this.comment;
   }

   public void setHomeDir(String var1) {
      this.homeDir = var1;
   }

   public String getHomeDir() {
      return this.homeDir;
   }

   public void setUserShell(String var1) {
      this.userShell = var1;
   }

   public String getUserShell() {
      return this.userShell;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public void setNumLogins(Long var1) {
      this.numLogins = var1.toString();
   }

   public String getNumLogins() {
      return this.numLogins;
   }

   public void setPasswordExpiration(String var1) {
      this.passwordExpiration = var1;
   }

   public String getPasswordExpiration() {
      return this.passwordExpiration;
   }

   public void setUserPrivileges(String var1) {
      this.userPrivileges = var1;
   }

   public String getUserPrivileges() {
      return this.userPrivileges;
   }

   public void setAccountExpiration(String var1) {
      this.acctExpiration = var1;
   }

   public String getAccountExpiration() {
      return this.acctExpiration;
   }

   public void setPasswordChanged(String var1) {
      this.pwdChanged = var1;
   }

   public String getPasswordChanged() {
      return this.pwdChanged;
   }

   public void setTarget(String var1) {
      this.target = var1;
   }

   public String getTarget() {
      return this.target;
   }

   public void setTaskId(TaskId var1) {
      this.taskId = var1;
   }

   public TaskId getTaskId() {
      return this.taskId;
   }

   public String toString() {
      return this.userName;
   }

   public int compareTo(Object var1) {
      User var2 = (User)var1;
      return this.userName.compareToIgnoreCase(var2.getUserName());
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         User var2 = (User)var1;
         if (this.userName == null) {
            if (var2.userName != null) {
               return false;
            }
         } else if (!this.userName.equals(var2.userName)) {
            return false;
         }

         if (this.userId == null) {
            if (var2.userId != null) {
               return false;
            }
         } else if (!this.userId.equals(var2.userId)) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      byte var1 = 7;
      int var2 = 37 * var1 + (this.userName != null ? this.userName.hashCode() : 0);
      var2 = 37 * var2 + (this.userId != null ? this.userId.hashCode() : 0);
      return var2;
   }
}
