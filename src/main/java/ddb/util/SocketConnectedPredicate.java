package ddb.util;

import java.net.Socket;
import org.apache.commons.collections.Predicate;

public class SocketConnectedPredicate implements Predicate {
   public static final Predicate INSTANCE = new SocketConnectedPredicate();

   @Override
   public boolean evaluate(Object obj) {
      if (obj == null) {
         return false;
      } else if (!(obj instanceof Socket)) {
         return false;
      } else {
         Socket s = (Socket)Socket.class.cast(obj);
         if (s.isClosed()) {
            return false;
         } else {
            return s.isConnected();
         }
      }
   }

   public static Predicate getInstance() {
      return INSTANCE;
   }
}
