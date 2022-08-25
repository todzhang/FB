package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.TaskId;

public class Group extends Data implements Comparable<Group> {
   private String groupName;
   private String groupComment;
   private String groupId;
   private String groupAttributes;
   private String target;
   private TaskId taskId;

   public Group(ObjectValue var1, String var2, TaskId var3) {
      super(var1);
      this.groupName = var1.getString("group");
      this.groupComment = var1.getString("comment");
      this.groupId = var1.getString("groupId");
      this.groupAttributes = var1.getString("attributes");
      this.target = var2;
      this.taskId = var3;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public void setGroupName(String var1) {
      this.groupName = var1;
   }

   public String getGroupComment() {
      return this.groupComment;
   }

   public void setGroupComment(String var1) {
      this.groupComment = var1;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public String getGroupAttributes() {
      return this.groupAttributes;
   }

   public void setGroupAttributes(String var1) {
      this.groupAttributes = var1;
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

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Group var2 = (Group)var1;
         if (this.groupName == null) {
            if (var2.groupName != null) {
               return false;
            }
         } else if (!this.groupName.equals(var2.groupName)) {
            return false;
         }

         if (this.groupId == null) {
            if (var2.groupId != null) {
               return false;
            }
         } else if (!this.groupId.equals(var2.groupId)) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      byte var1 = 7;
      int var2 = 37 * var1 + (this.groupName != null ? this.groupName.hashCode() : 0);
      var2 = 37 * var2 + (this.groupId != null ? this.groupId.hashCode() : 0);
      return var2;
   }

   public int compareTo(Group var1) {
      if (var1 == null) {
         return -1;
      } else {
         return this.groupName == null ? 1 : this.groupName.compareTo(var1.groupName);
      }
   }
}
