package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CommandInfoType",
   propOrder = {"log", "screenLog", "resourceDirectory", "displayTransform", "storageTransform", "targetAddress", "taskId"}
)
public class CommandInfoType {
   @XmlElement(
      name = "Log",
      required = true
   )
   protected String log;
   @XmlElement(
      name = "ScreenLog"
   )
   protected String screenLog;
   @XmlElement(
      name = "ResourceDirectory",
      required = true
   )
   protected String resourceDirectory;
   @XmlElement(
      name = "DisplayTransform",
      required = true
   )
   protected String displayTransform;
   @XmlElement(
      name = "StorageTransform",
      required = true
   )
   protected String storageTransform;
   @XmlElement(
      name = "TargetAddress"
   )
   protected String targetAddress;
   @XmlElement(
      name = "TaskId",
      required = true
   )
   protected String taskId;

   public String getLog() {
      return this.log;
   }

   public void setLog(String var1) {
      this.log = var1;
   }

   public String getScreenLog() {
      return this.screenLog;
   }

   public void setScreenLog(String var1) {
      this.screenLog = var1;
   }

   public String getResourceDirectory() {
      return this.resourceDirectory;
   }

   public void setResourceDirectory(String var1) {
      this.resourceDirectory = var1;
   }

   public String getDisplayTransform() {
      return this.displayTransform;
   }

   public void setDisplayTransform(String var1) {
      this.displayTransform = var1;
   }

   public String getStorageTransform() {
      return this.storageTransform;
   }

   public void setStorageTransform(String var1) {
      this.storageTransform = var1;
   }

   public String getTargetAddress() {
      return this.targetAddress;
   }

   public void setTargetAddress(String var1) {
      this.targetAddress = var1;
   }

   public String getTaskId() {
      return this.taskId;
   }

   public void setTaskId(String var1) {
      this.taskId = var1;
   }
}
