package ds.core;

import ddb.dsz.core.controller.CoreController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class ConnectionEstablisher implements Runnable {
   private ServerSocket serverSocket;
   private Socket connection;
   private CoreController core;

   public ConnectionEstablisher(ServerSocket serverSocket, CoreController core) {
      this.serverSocket = serverSocket;
      this.core = core;
   }

   @Override
   public void run() {
      try {
         this.connection = this.serverSocket.accept();
      } catch (IOException e) {
         this.core.logEvent(Level.INFO, e.getMessage(), e);
      }

   }

   public Socket getConnection() {
      return this.connection;
   }
}
