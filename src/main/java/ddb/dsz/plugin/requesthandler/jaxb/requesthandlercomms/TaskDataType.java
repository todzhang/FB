package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "TaskDataType",
   propOrder = {"parent", "data"}
)
public class TaskDataType {
   @XmlElement(
      name = "Parent"
   )
   protected ParentType parent;
   @XmlElement(
      name = "Data",
      required = true
   )
   protected ObjectValueType data;
   @XmlAttribute(
      name = "taskId"
   )
   protected BigInteger taskId;
   @XmlAttribute(
      name = "operation"
   )
   protected String operation;

   public ParentType getParent() {
      return this.parent;
   }

   public void setParent(ParentType var1) {
      this.parent = var1;
   }

   public ObjectValueType getData() {
      return this.data;
   }

   public void setData(ObjectValueType var1) {
      this.data = var1;
   }

   public BigInteger getTaskId() {
      return this.taskId;
   }

   public void setTaskId(BigInteger var1) {
      this.taskId = var1;
   }

   public String getOperation() {
      return this.operation;
   }

   public void setOperation(String var1) {
      this.operation = var1;
   }
}
