package ds.plugin.peer;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import ddb.util.ClientSocketHandler;
import ds.jaxb.external.ObjectFactory;
import ds.jaxb.external.RemoteIdentification;
import ds.jaxb.external.RemoteMessage;
import ds.jaxb.external.RemotePing;
import ds.jaxb.external.RemotePong;
import java.net.Socket;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;

public class PeerSocketHandler extends ClientSocketHandler implements Closure, PeerTag {
   Peer peer;
   CoreController core;
   ObjectFactory of = new ObjectFactory();
   String peerName;
   boolean sentPause;

   public PeerSocketHandler(CoreController var1, Socket var2, Peer var3) throws JAXBException {
      super(var1, var2, ObjectFactory.class, ClosureUtils.nopClosure());
      this.core = var1;
      super.setClosure(this);
      this.peer = var3;
      this.peerName = "Unknown";
   }

   public String getName() {
      return "PeerSocketHandler";
   }

   public void begin(String var1) {
      super.start();
      RemoteIdentification var2 = new RemoteIdentification();
      var2.setValue(var1);
      super.publish(this.of.createRemoteIdentification(var2));
   }

   public String getPeerName() {
      return this.peerName;
   }

   public String toString() {
      return String.format("%s (%s)", this.peerName, super.getSocket().getInetAddress().getHostAddress());
   }

   public void execute(Object var1) {
      try {
         if (var1 instanceof JAXBElement) {
            var1 = ((JAXBElement)JAXBElement.class.cast(var1)).getValue();
         }

         if (var1 instanceof RemoteMessage) {
            this.peer.receivedMessage((RemoteMessage)RemoteMessage.class.cast(var1), this);
         }

         if (var1 instanceof RemotePing) {
            this.sendPong();
         }

         if (var1 instanceof RemoteIdentification) {
            this.peerName = ((RemoteIdentification)RemoteIdentification.class.cast(var1)).getValue();
            this.peer.connectionUpdated(this);
         }
      } catch (Exception var3) {
         this.core.logEvent(Level.SEVERE, var3.getMessage(), var3);
      }

   }

   public PeerTransferStatus sendMessage(String var1) {
      this.core.logEvent(Level.FINEST, "PeerSocketHandler", var1);

      try {
         RemoteMessage var2 = new RemoteMessage();
         var2.setMessage(var1);
         super.publish(this.of.createRemoteMessage(var2));
         return PeerTransferStatus.SENT;
      } catch (Exception var3) {
         this.core.logEvent(Level.SEVERE, var3.getMessage(), var3);
         return PeerTransferStatus.DISCONNECTED;
      }
   }

   public PeerTransferStatus sendPing() {
      if (this.isConnected()) {
         this.publish(this.of.createRemotePing(new RemotePing()));
         return PeerTransferStatus.SENT;
      } else {
         return PeerTransferStatus.DISCONNECTED;
      }
   }

   public PeerTransferStatus sendPong() {
      if (this.isConnected()) {
         this.publish(this.of.createRemotePong(new RemotePong()));
         return PeerTransferStatus.SENT;
      } else {
         return PeerTransferStatus.DISCONNECTED;
      }
   }

   public void stop() {
      this.peer.connectionStopping(this);
      super.stop();
   }

   public int compareTo(PeerTag var1) {
      return var1 == null ? -1 : String.CASE_INSENSITIVE_ORDER.compare(this.peerName, var1.getPeerName());
   }
}
