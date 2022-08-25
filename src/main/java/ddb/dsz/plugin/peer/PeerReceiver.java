package ddb.dsz.plugin.peer;

import java.util.EventListener;

public interface PeerReceiver extends EventListener {
   void receivedMessage(String message, PeerTag peerTag);

   void newConnection(PeerTag peerTag);

   void closedConnection(PeerTag peerTag);
}
