package ddb.util;

import ds.jaxb.lpconfig.LpConfig;
import ds.jaxb.lpconfig.ObjectFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class LpConfigWriter {
   public static long UNSET = -1L;

   public static void writeConfig(int var0, String var1, String var2, File var3, String var4) throws XMLException, IOException {
      writeConfig(var0, var1, var2, UNSET, var3, var4);
   }

   public static void writeConfig(int port, String logDir, String resourceDir, long remoteAddress, File file, String localAddress) throws IOException, XMLException {
      try {
         LpConfig lpConfig = new LpConfig();
         lpConfig.setPort(Integer.toString(port));
         lpConfig.setLogDir(logDir);
         lpConfig.setResourceDir(resourceDir);
         lpConfig.setLocalAddress(localAddress);
         if (remoteAddress > UNSET) {
            lpConfig.setRemoteAddress(remoteAddress);
         }

         JAXBContext context = JaxbCache.getContext(ObjectFactory.class);
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
         FileWriter fileWriter = new FileWriter(file);
         marshaller.marshal(lpConfig, fileWriter);
         fileWriter.close();
      } catch (JAXBException e) {
         throw new XMLException(e.getMessage(), e.getCause());
      }
   }
}
