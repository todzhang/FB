package ddb.dsz.plugin.mirror;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorFileStatus;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.ObjectFactory;
import ddb.util.JaxbCache;
import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class DirectoryStatus extends DirectoryScanner {
   Marshaller marsh;
   ObjectFactory of;
   TransferredFilesModel model;

   public DirectoryStatus(CoreController var1, TransferredFilesModel var2) throws JAXBException {
      super(var1);
      JAXBContext var3 = JaxbCache.getContext(ObjectFactory.class);
      this.marsh = var3.createMarshaller();
      this.of = new ObjectFactory();
      this.model = var2;
   }

   protected boolean handleFile(String var1, File var2) {
      MirrorFileStatus var3 = new MirrorFileStatus();
      var3.setFile(var1);
      var3.setLength(BigInteger.valueOf(var2.length()));
      StringWriter var4 = new StringWriter();

      try {
         this.marsh.marshal(this.of.createMirrorFileStatus(var3), var4);
         this.core.sendMessageToPeer(var4.toString());
         FileInformation var5 = this.model.getFileInformation(var1);
         var5.setSize(var2.length());
         var5.setSoFar(var2.length());
         this.model.addOrUpdateRecord(var5);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return true;
   }

   public void run() {
      try {
         this.scanDirectory(this.core.getLogDirectory(), "");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }
}
