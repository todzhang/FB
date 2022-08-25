package ddb.dsz.library.console;

import ddb.dsz.library.console.jaxb.history.CommandHistory;
import ddb.dsz.library.console.jaxb.history.ObjectFactory;
import ddb.util.JaxbCache;
import ddb.util.XMLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class HistoryFileParserWriter {
   private HistoryFileParserWriter() {
   }

   public static List<String> parse(InputStream inputStream) throws XMLException {
      Vector vector = new Vector();
      CommandHistory commandHistory = null;

      try {
         JAXBContext var3 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var4 = var3.createUnmarshaller();
         Object var5 = var4.unmarshal(inputStream);
         if (!(var5 instanceof JAXBElement)) {
            throw new XMLException("Bad file format");
         }

         JAXBElement var6 = (JAXBElement)var5;
         if (var6.getValue() instanceof CommandHistory) {
            commandHistory = (CommandHistory)var6.getValue();
         }
      } catch (JAXBException var7) {
         throw new XMLException(var7.getMessage(), var7.getCause());
      }

      List var8 = commandHistory.getCommand();
      ListIterator var9 = var8.listIterator();

      while(var9.hasNext()) {
         vector.add(var9.next());
      }

      return vector;
   }

   public static List<String> parse(String var0) throws FileNotFoundException, XMLException {
      FileInputStream fileInputStream = new FileInputStream(var0);
      return parse(fileInputStream);
   }

   public static void save(List<String> commands, String filepath) throws XMLException, IOException {
      try {
         ObjectFactory objectFactory = new ObjectFactory();
         CommandHistory commandHistory = objectFactory.createCommandHistory();
         Iterator var4 = commands.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            commandHistory.getCommand().add(var5);
         }

         JAXBContext var8 = JaxbCache.getContext(ObjectFactory.class);
         Marshaller var6 = var8.createMarshaller();
         var6.setProperty("jaxb.formatted.output", Boolean.TRUE);
         var6.marshal(objectFactory.createHistory(commandHistory), new FileWriter(filepath));
      } catch (JAXBException var7) {
         throw new XMLException(var7.getMessage(), var7.getCause());
      }
   }
}
