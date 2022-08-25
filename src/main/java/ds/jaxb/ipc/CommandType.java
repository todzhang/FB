package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CommandType",
   propOrder = {"startCommand", "stopCommand", "interruptCommand", "stopOutput", "restartOutput", "addPrefixes", "shutdown"}
)
public class CommandType {
   @XmlElement(
      name = "StartCommand"
   )
   protected StartCommandType startCommand;
   @XmlElement(
      name = "StopCommand"
   )
   protected StopCommandType stopCommand;
   @XmlElement(
      name = "InterruptCommand"
   )
   protected InterruptCommandType interruptCommand;
   @XmlElement(
      name = "StopOutput"
   )
   protected StopOutputType stopOutput;
   @XmlElement(
      name = "RestartOutput"
   )
   protected RestartOutputType restartOutput;
   @XmlElement(
      name = "AddPrefixes"
   )
   protected AddPrefixesType addPrefixes;
   @XmlElement(
      name = "Shutdown"
   )
   protected ShutdownType shutdown;
   @XmlAttribute(
      name = "cmdId",
      required = true
   )
   protected int cmdId;

   public StartCommandType getStartCommand() {
      return this.startCommand;
   }

   public void setStartCommand(StartCommandType var1) {
      this.startCommand = var1;
   }

   public StopCommandType getStopCommand() {
      return this.stopCommand;
   }

   public void setStopCommand(StopCommandType var1) {
      this.stopCommand = var1;
   }

   public InterruptCommandType getInterruptCommand() {
      return this.interruptCommand;
   }

   public void setInterruptCommand(InterruptCommandType var1) {
      this.interruptCommand = var1;
   }

   public StopOutputType getStopOutput() {
      return this.stopOutput;
   }

   public void setStopOutput(StopOutputType var1) {
      this.stopOutput = var1;
   }

   public RestartOutputType getRestartOutput() {
      return this.restartOutput;
   }

   public void setRestartOutput(RestartOutputType var1) {
      this.restartOutput = var1;
   }

   public AddPrefixesType getAddPrefixes() {
      return this.addPrefixes;
   }

   public void setAddPrefixes(AddPrefixesType var1) {
      this.addPrefixes = var1;
   }

   public ShutdownType getShutdown() {
      return this.shutdown;
   }

   public void setShutdown(ShutdownType var1) {
      this.shutdown = var1;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int var1) {
      this.cmdId = var1;
   }
}
