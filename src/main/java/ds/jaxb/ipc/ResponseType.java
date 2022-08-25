package ds.jaxb.ipc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ResponseType",
   propOrder = {"commandList", "pong", "userEntry", "promptStopped", "help", "statistics", "guiCommand"}
)
public class ResponseType {
   @XmlElement(
      name = "CommandList"
   )
   protected CommandListType commandList;
   @XmlElement(
      name = "Pong"
   )
   protected PongType pong;
   @XmlElement(
      name = "UserEntry"
   )
   protected UserEntryType userEntry;
   @XmlElement(
      name = "PromptStopped"
   )
   protected PromptStoppedType promptStopped;
   @XmlElement(
      name = "Help"
   )
   protected HelpType help;
   @XmlElement(
      name = "Statistics"
   )
   protected StatisticsType statistics;
   @XmlElement(
      name = "GuiCommand"
   )
   protected GuiCommandResponse guiCommand;
   @XmlAttribute(
      name = "reqId",
      required = true
   )
   protected int reqId;

   public CommandListType getCommandList() {
      return this.commandList;
   }

   public void setCommandList(CommandListType var1) {
      this.commandList = var1;
   }

   public PongType getPong() {
      return this.pong;
   }

   public void setPong(PongType var1) {
      this.pong = var1;
   }

   public UserEntryType getUserEntry() {
      return this.userEntry;
   }

   public void setUserEntry(UserEntryType var1) {
      this.userEntry = var1;
   }

   public PromptStoppedType getPromptStopped() {
      return this.promptStopped;
   }

   public void setPromptStopped(PromptStoppedType var1) {
      this.promptStopped = var1;
   }

   public HelpType getHelp() {
      return this.help;
   }

   public void setHelp(HelpType var1) {
      this.help = var1;
   }

   public StatisticsType getStatistics() {
      return this.statistics;
   }

   public void setStatistics(StatisticsType var1) {
      this.statistics = var1;
   }

   public GuiCommandResponse getGuiCommand() {
      return this.guiCommand;
   }

   public void setGuiCommand(GuiCommandResponse var1) {
      this.guiCommand = var1;
   }

   public int getReqId() {
      return this.reqId;
   }

   public void setReqId(int var1) {
      this.reqId = var1;
   }
}
