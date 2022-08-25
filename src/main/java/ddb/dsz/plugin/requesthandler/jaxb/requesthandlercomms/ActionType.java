package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ActionType",
   propOrder = {"key", "command", "display", "scope"}
)
public class ActionType {
   @XmlElement(
      name = "Key",
      required = true
   )
   protected String key;
   @XmlElement(
      name = "Command",
      required = true
   )
   protected Format command;
   @XmlElement(
      name = "Display",
      required = true
   )
   protected Format display;
   @XmlElement(
      name = "Scope"
   )
   protected Format scope;
   @XmlAttribute(
      name = "spawnTerminal",
      required = true
   )
   protected boolean spawnTerminal;

   public String getKey() {
      return this.key;
   }

   public void setKey(String var1) {
      this.key = var1;
   }

   public Format getCommand() {
      return this.command;
   }

   public void setCommand(Format var1) {
      this.command = var1;
   }

   public Format getDisplay() {
      return this.display;
   }

   public void setDisplay(Format var1) {
      this.display = var1;
   }

   public Format getScope() {
      return this.scope;
   }

   public void setScope(Format var1) {
      this.scope = var1;
   }

   public boolean isSpawnTerminal() {
      return this.spawnTerminal;
   }

   public void setSpawnTerminal(boolean var1) {
      this.spawnTerminal = var1;
   }
}
