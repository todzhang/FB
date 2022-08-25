package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "InfoType",
   propOrder = {"setFlags", "output", "idMap", "commandStarted", "commandResult", "commandInfo", "dataInfo", "connectionInfo", "setTitle", "throttleInfo"}
)
public class InfoType {
   @XmlElement(
      name = "SetFlags"
   )
   protected SetFlagsType setFlags;
   @XmlElement(
      name = "Output"
   )
   protected OutputType output;
   @XmlElement(
      name = "IdMap"
   )
   protected IdMapType idMap;
   @XmlElement(
      name = "CommandStarted"
   )
   protected CommandStartedType commandStarted;
   @XmlElement(
      name = "CommandResult"
   )
   protected CommandResultType commandResult;
   @XmlElement(
      name = "CommandInfo"
   )
   protected CommandInfoType commandInfo;
   @XmlElement(
      name = "DataInfo"
   )
   protected DataInfoType dataInfo;
   @XmlElement(
      name = "ConnectionInfo"
   )
   protected ConnectionInfoType connectionInfo;
   @XmlElement(
      name = "SetTitle"
   )
   protected SetTitleType setTitle;
   @XmlElement(
      name = "ThrottleInfo"
   )
   protected ThrottleInfoType throttleInfo;
   @XmlAttribute(
      name = "cmdId",
      required = true
   )
   protected int cmdId;

   public SetFlagsType getSetFlags() {
      return this.setFlags;
   }

   public void setSetFlags(SetFlagsType var1) {
      this.setFlags = var1;
   }

   public OutputType getOutput() {
      return this.output;
   }

   public void setOutput(OutputType var1) {
      this.output = var1;
   }

   public IdMapType getIdMap() {
      return this.idMap;
   }

   public void setIdMap(IdMapType var1) {
      this.idMap = var1;
   }

   public CommandStartedType getCommandStarted() {
      return this.commandStarted;
   }

   public void setCommandStarted(CommandStartedType var1) {
      this.commandStarted = var1;
   }

   public CommandResultType getCommandResult() {
      return this.commandResult;
   }

   public void setCommandResult(CommandResultType var1) {
      this.commandResult = var1;
   }

   public CommandInfoType getCommandInfo() {
      return this.commandInfo;
   }

   public void setCommandInfo(CommandInfoType var1) {
      this.commandInfo = var1;
   }

   public DataInfoType getDataInfo() {
      return this.dataInfo;
   }

   public void setDataInfo(DataInfoType var1) {
      this.dataInfo = var1;
   }

   public ConnectionInfoType getConnectionInfo() {
      return this.connectionInfo;
   }

   public void setConnectionInfo(ConnectionInfoType var1) {
      this.connectionInfo = var1;
   }

   public SetTitleType getSetTitle() {
      return this.setTitle;
   }

   public void setSetTitle(SetTitleType var1) {
      this.setTitle = var1;
   }

   public ThrottleInfoType getThrottleInfo() {
      return this.throttleInfo;
   }

   public void setThrottleInfo(ThrottleInfoType var1) {
      this.throttleInfo = var1;
   }

   public int getCmdId() {
      return this.cmdId;
   }

   public void setCmdId(int var1) {
      this.cmdId = var1;
   }
}
