package ddb.dsz.plugin.scripteditor.jaxb.filelist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "FileEntry",
   propOrder = {"name", "topLine", "selectionStart", "selectionStop"}
)
public class FileEntry {
   @XmlElement(
      name = "Name",
      required = true
   )
   protected String name;
   @XmlElement(
      name = "TopLine"
   )
   protected int topLine;
   @XmlElement(
      name = "SelectionStart"
   )
   protected int selectionStart;
   @XmlElement(
      name = "SelectionStop"
   )
   protected int selectionStop;

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public int getTopLine() {
      return this.topLine;
   }

   public void setTopLine(int var1) {
      this.topLine = var1;
   }

   public int getSelectionStart() {
      return this.selectionStart;
   }

   public void setSelectionStart(int var1) {
      this.selectionStart = var1;
   }

   public int getSelectionStop() {
      return this.selectionStop;
   }

   public void setSelectionStop(int var1) {
      this.selectionStop = var1;
   }
}
