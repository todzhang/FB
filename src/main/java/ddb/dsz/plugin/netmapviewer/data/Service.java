package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.TaskId;

public class Service extends Data implements Comparable {
   private String serviceName;
   private String serviceDisplayName;
   private String serviceState;
   private String serviceType;
   private String serviceAcceptedCodes;
   private String target;
   private TaskId taskId;

   public Service(ObjectValue var1, String var2, TaskId var3) {
      super(var1);
      this.serviceName = var1.getString("serviceName");
      this.serviceDisplayName = var1.getString("displayName");
      this.serviceState = var1.getString("state");
      this.serviceType = var1.getString("serviceType");
      this.serviceAcceptedCodes = var1.getString("acceptedCodes");
      this.target = var2;
      this.taskId = var3;
   }

   public void setServiceName(String var1) {
      this.serviceName = var1;
   }

   public String getServiceName() {
      return this.serviceName;
   }

   public void setServiceDisplayName(String var1) {
      this.serviceDisplayName = var1;
   }

   public String getServiceDisplayName() {
      return this.serviceDisplayName;
   }

   public void setServiceState(String var1) {
      this.serviceState = var1;
   }

   public String getServiceState() {
      return this.serviceState;
   }

   public void setServiceType(String var1) {
      this.serviceType = var1;
   }

   public String getServiceType() {
      return this.serviceType;
   }

   public void setServiceAcceptedCodes(String var1) {
      this.serviceAcceptedCodes = var1;
   }

   public String getServiceAcceptedCodes() {
      return this.serviceAcceptedCodes;
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
      return this.serviceDisplayName;
   }

   public int compareTo(Object var1) {
      Service var2 = (Service)var1;
      return this.serviceName.compareToIgnoreCase(var2.getServiceName());
   }
}
