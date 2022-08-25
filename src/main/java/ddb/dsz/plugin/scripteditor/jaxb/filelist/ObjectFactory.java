package ddb.dsz.plugin.scripteditor.jaxb.filelist;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _FileList_QNAME = new QName("", "FileList");

   public FileList createFileList() {
      return new FileList();
   }

   public FileEntry createFileEntry() {
      return new FileEntry();
   }

   @XmlElementDecl(
      namespace = "",
      name = "FileList"
   )
   public JAXBElement<FileList> createFileList(FileList var1) {
      return new JAXBElement(_FileList_QNAME, FileList.class, (Class)null, var1);
   }
}
