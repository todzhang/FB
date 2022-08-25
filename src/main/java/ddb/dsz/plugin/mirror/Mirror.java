package ddb.dsz.plugin.mirror;

import ddb.dsz.plugin.AbstractPlugin;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorNext;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorPing;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorPong;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.ObjectFactory;
import ddb.dsz.plugin.peer.PeerReceiver;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.util.JaxbCache;
import ddb.util.TableSorter;
import java.awt.BorderLayout;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

public abstract class Mirror extends AbstractPlugin implements PeerReceiver {
   public static final String LOCK = ".replay";
   public static final String TEMP = "Temp";
   protected TransferredFilesModel model = new TransferredFilesModel();
   protected JPanel mainPanel = new JPanel(new BorderLayout());
   private Unmarshaller unmarsh;
   private Marshaller marsh;
   private ObjectFactory objFact = new ObjectFactory();

   @Override
   protected int init2() {
      this.core.addPeerReceiver(this);

      try {
         JAXBContext var1 = JaxbCache.getContext(ObjectFactory.class);
         this.unmarsh = var1.createUnmarshaller();
         this.marsh = var1.createMarshaller();
      } catch (JAXBException var4) {
         this.core.logEvent(Level.SEVERE, var4.getMessage(), var4);
         return -1;
      }

      TableSorter var5 = new TableSorter(this.model);
      JTable var2 = new JTable(var5);
      var5.sortByColumn(TransferredFiles.FILENAME.ordinal());
      JScrollPane var3 = new JScrollPane(var2);
      this.mainPanel.add(var3, "Center");
      super.setDisplay(this.mainPanel);
      this.setCanClose(false);
      return this.init3();
   }

   protected int init3() {
      return 0;
   }

   @Override
   protected final boolean parseArgument2(String var1, String var2) {
      return this.parseArgument3(var1, var2);
   }

   protected boolean parseArgument3(String var1, String var2) {
      return false;
   }

   @Override
   protected final void fini2() {
      this.core.removePeerReceiver(this);
      this.fini3();
   }

   protected final void fini3() {
   }

   @Override
   public void receivedMessage(String message, PeerTag peerTag) {
      try {
         Object var3 = this.unmarshal(message);
         if (var3 instanceof JAXBElement) {
            var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
         }

         if (var3 instanceof MirrorPing) {
            this.core.sendMessageToPeer(this.marshall(this.objFact.createMirrorPong(new MirrorPong())), peerTag);
         }
      } catch (UnmarshalException var4) {
      } catch (JAXBException var5) {
      }

   }

   protected Object unmarshal(String var1) {
      try {
         return this.unmarsh.unmarshal(new StringReader(var1));
      } catch (UnmarshalException var3) {
      } catch (JAXBException var4) {
      }

      return null;
   }

   protected String marshall(JAXBElement<?> var1) throws JAXBException {
      StringWriter var2 = new StringWriter();
      this.marsh.marshal(var1, var2);
      return var2.toString();
   }

   protected void requestNext(PeerTag var1) {
      try {
         this.core.sendMessageToPeer(this.marshall(this.objFact.createMirrorNext(new MirrorNext())), var1);
      } catch (JAXBException var3) {
         var3.printStackTrace();
      }

   }

   @Override
   public void closedConnection(PeerTag peerTag) {
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      if (!super.allowNewInstance(clazz)) {
         return false;
      } else {
         return !Mirror.class.isAssignableFrom(clazz);
      }
   }
}
