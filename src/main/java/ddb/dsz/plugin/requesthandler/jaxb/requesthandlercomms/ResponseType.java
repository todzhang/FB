package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ResponseType",
   propOrder = {"newRequest", "executedRequest", "requestCompleted", "cancelledRequest", "deniedRequest", "taskData"}
)
public class ResponseType {
   @XmlElement(
      name = "NewRequest"
   )
   protected NewRequestType newRequest;
   @XmlElement(
      name = "ExecutedRequest"
   )
   protected ExecutedRequestType executedRequest;
   @XmlElement(
      name = "RequestCompleted"
   )
   protected RequestCompletedType requestCompleted;
   @XmlElement(
      name = "CancelledRequest"
   )
   protected CancelledRequestType cancelledRequest;
   @XmlElement(
      name = "DeniedRequest"
   )
   protected DeniedRequestType deniedRequest;
   @XmlElement(
      name = "TaskData"
   )
   protected TaskDataType taskData;

   public NewRequestType getNewRequest() {
      return this.newRequest;
   }

   public void setNewRequest(NewRequestType var1) {
      this.newRequest = var1;
   }

   public ExecutedRequestType getExecutedRequest() {
      return this.executedRequest;
   }

   public void setExecutedRequest(ExecutedRequestType var1) {
      this.executedRequest = var1;
   }

   public RequestCompletedType getRequestCompleted() {
      return this.requestCompleted;
   }

   public void setRequestCompleted(RequestCompletedType var1) {
      this.requestCompleted = var1;
   }

   public CancelledRequestType getCancelledRequest() {
      return this.cancelledRequest;
   }

   public void setCancelledRequest(CancelledRequestType var1) {
      this.cancelledRequest = var1;
   }

   public DeniedRequestType getDeniedRequest() {
      return this.deniedRequest;
   }

   public void setDeniedRequest(DeniedRequestType var1) {
      this.deniedRequest = var1;
   }

   public TaskDataType getTaskData() {
      return this.taskData;
   }

   public void setTaskData(TaskDataType var1) {
      this.taskData = var1;
   }
}
