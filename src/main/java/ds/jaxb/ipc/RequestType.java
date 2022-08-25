package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "RequestType",
   propOrder = {"listCommands", "ping", "startPrompt", "stopPrompt", "getHelp", "getStatistics", "guiCommand"}
)
public class RequestType {
   @XmlElement(
      name = "ListCommands"
   )
   protected ListCommandsType listCommands;
   @XmlElement(
      name = "Ping"
   )
   protected PingType ping;
   @XmlElement(
      name = "StartPrompt"
   )
   protected StartPromptType startPrompt;
   @XmlElement(
      name = "StopPrompt"
   )
   protected StopPromptType stopPrompt;
   @XmlElement(
      name = "GetHelp"
   )
   protected GetHelpType getHelp;
   @XmlElement(
      name = "GetStatistics"
   )
   protected GetStatisticsType getStatistics;
   @XmlElement(
      name = "GuiCommand"
   )
   protected GuiCommandRequest guiCommand;
   @XmlAttribute(
      name = "reqId",
      required = true
   )
   protected int reqId;

   public ListCommandsType getListCommands() {
      return this.listCommands;
   }

   public void setListCommands(ListCommandsType var1) {
      this.listCommands = var1;
   }

   public PingType getPing() {
      return this.ping;
   }

   public void setPing(PingType var1) {
      this.ping = var1;
   }

   public StartPromptType getStartPrompt() {
      return this.startPrompt;
   }

   public void setStartPrompt(StartPromptType var1) {
      this.startPrompt = var1;
   }

   public StopPromptType getStopPrompt() {
      return this.stopPrompt;
   }

   public void setStopPrompt(StopPromptType var1) {
      this.stopPrompt = var1;
   }

   public GetHelpType getGetHelp() {
      return this.getHelp;
   }

   public void setGetHelp(GetHelpType var1) {
      this.getHelp = var1;
   }

   public GetStatisticsType getGetStatistics() {
      return this.getStatistics;
   }

   public void setGetStatistics(GetStatisticsType var1) {
      this.getStatistics = var1;
   }

   public GuiCommandRequest getGuiCommand() {
      return this.guiCommand;
   }

   public void setGuiCommand(GuiCommandRequest var1) {
      this.guiCommand = var1;
   }

   public int getReqId() {
      return this.reqId;
   }

   public void setReqId(int var1) {
      this.reqId = var1;
   }
}
