package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "DataInfoType",
   propOrder = {"log", "targetAddress", "taskId"}
)
public class DataInfoType {
   @XmlElement(
      name = "Log",
      required = true
   )
   protected String log;
   @XmlElement(
      name = "TargetAddress",
      required = true
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
