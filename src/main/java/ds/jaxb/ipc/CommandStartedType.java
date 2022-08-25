package ds.jaxb.ipc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "CommandStartedType",
   propOrder = {"parent", "targetAddress", "prefix", "command", "arg"}
)
public class CommandStartedType {
   @XmlElement(
      name = "Parent"
   )
   protected Integer parent;
   @XmlElement(
      name = "TargetAddress",
      required = true
   )
   protected String targetAddress;
   @XmlElement(
      name = "Prefix"
   )
   protected List<String> prefix;
   @XmlElement(
      name = "Command",
      required = true
   )
   protected String command;
   @XmlElement(
      name = "Arg"
   )
   protected List<String> arg;

   public Integer getParent() {
      return this.parent;
   }

   public void setParent(Integer var1) {
      this.parent = var1;
   }

   public String getTargetAddress() {
      return this.targetAddress;
   }

   public void setTargetAddress(String var1) {
      this.targetAddress = var1;
   }

   public List<String> getPrefix() {
      if (this.prefix == null) {
         this.prefix = new ArrayList();
      }

      return this.prefix;
   }

   public String getCommand() {
      return this.command;
   }

   public void setCommand(String var1) {
      this.command = var1;
   }

   public List<String> getArg() {
      if (this.arg == null) {
         this.arg = new ArrayList();
      }

      return this.arg;
   }
}
