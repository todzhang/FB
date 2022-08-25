package ddb.dsz.plugin.shell.jaxb.shellcommands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "SystemType",
   propOrder = {"platformRegex", "commandLine", "initialCommand", "inputFormat", "outputFormat", "workingDirectory"}
)
public class SystemType {
   @XmlElement(
      name = "PlatformRegex",
      required = true
   )
   protected String platformRegex;
   @XmlElement(
      name = "CommandLine",
      required = true
   )
   protected String commandLine;
   @XmlElement(
      name = "InitialCommand"
   )
   protected String initialCommand;
   @XmlElement(
      name = "InputFormat"
   )
   protected String inputFormat;
   @XmlElement(
      name = "OutputFormat"
   )
   protected String outputFormat;
   @XmlElement(
      name = "WorkingDirectory"
   )
   protected String workingDirectory;
   @XmlAttribute(
      name = "adjustableFormat"
   )
   protected Boolean adjustableFormat;
   @XmlAttribute(
      name = "enableUser"
   )
   protected Boolean enableUser;

   public String getPlatformRegex() {
      return this.platformRegex;
   }

   public void setPlatformRegex(String var1) {
      this.platformRegex = var1;
   }

   public String getCommandLine() {
      return this.commandLine;
   }

   public void setCommandLine(String var1) {
      this.commandLine = var1;
   }

   public String getInitialCommand() {
      return this.initialCommand;
   }

   public void setInitialCommand(String var1) {
      this.initialCommand = var1;
   }

   public String getInputFormat() {
      return this.inputFormat;
   }

   public void setInputFormat(String var1) {
      this.inputFormat = var1;
   }

   public String getOutputFormat() {
      return this.outputFormat;
   }

   public void setOutputFormat(String var1) {
      this.outputFormat = var1;
   }

   public String getWorkingDirectory() {
      return this.workingDirectory;
   }

   public void setWorkingDirectory(String var1) {
      this.workingDirectory = var1;
   }

   public boolean isAdjustableFormat() {
      return this.adjustableFormat == null ? false : this.adjustableFormat;
   }

   public void setAdjustableFormat(Boolean var1) {
      this.adjustableFormat = var1;
   }

   public boolean isEnableUser() {
      return this.enableUser == null ? false : this.enableUser;
   }

   public void setEnableUser(Boolean var1) {
      this.enableUser = var1;
   }
}
