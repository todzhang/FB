package ddb.dsz.plugin.scripteditor.jaxb.filelist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "FileList",
   propOrder = {"fileEntry"}
)
public class FileList {
   @XmlElement(
      name = "FileEntry"
   )
   protected List<Object> fileEntry;

   public List<Object> getFileEntry() {
      if (this.fileEntry == null) {
         this.fileEntry = new ArrayList();
      }

      return this.fileEntry;
   }
}
