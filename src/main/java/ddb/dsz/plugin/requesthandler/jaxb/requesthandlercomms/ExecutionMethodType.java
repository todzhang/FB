package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "ExecutionMethodType",
   propOrder = {"terminalPath", "action"}
)
public class ExecutionMethodType {
   @XmlElement(
      name = "TerminalPath",
      required = true
   )
   protected String terminalPath;
   @XmlElement(
      name = "Action"
   )
   protected List<ActionType> action;

   public String getTerminalPath() {
      return this.terminalPath;
   }

   public void setTerminalPath(String var1) {
      this.terminalPath = var1;
   }

   public List<ActionType> getAction() {
      if (this.action == null) {
         this.action = new ArrayList();
      }

      return this.action;
   }
}
