package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "RequestType",
   propOrder = {"newRequest", "taskDataRequest"}
)
public class RequestType {
   @XmlElement(
      name = "NewRequest"
   )
   protected OperationOptionType newRequest;
   @XmlElement(
      name = "TaskDataRequest"
   )
   protected TaskDataRequestType taskDataRequest;

   public OperationOptionType getNewRequest() {
      return this.newRequest;
   }

   public void setNewRequest(OperationOptionType var1) {
      this.newRequest = var1;
   }

   public TaskDataRequestType getTaskDataRequest() {
      return this.taskDataRequest;
   }

   public void setTaskDataRequest(TaskDataRequestType var1) {
      this.taskDataRequest = var1;
   }
}
