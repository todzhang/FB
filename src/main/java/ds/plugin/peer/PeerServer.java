package ds.plugin.peer;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;

@DszLogo("images/kwallet2.png")
@DszName("Peer")
@DszDescription("Handles communication between this computer and a Peer")
public class PeerServer extends Peer {
   ServerSocket server;
   List<PeerSocketHandler> remoteConnections = new Vector();
   int serverPort = 0;
   JPanel listeningOn = new JPanel(new GridLayout(2, 3));
   JLabel connectionInfo = new JLabel("");
   Closure closure = ClosureUtils.nopClosure();
   final Object connectionLock = new Object();
   DefaultListModel dlm = new DefaultListModel();
   JList connections;
   boolean sentPort;
   Runnable sendPing;

   public PeerServer() {
      this.connections = new JList(this.dlm);
      this.sentPort = false;
      this.sendPing = new Runnable() {
         public void run() {
            synchronized(PeerServer.this.connectionLock) {
               int var2 = 0;

               while(true) {
                  if (var2 >= PeerServer.this.remoteConnections.size()) {
                     break;
                  }

                  if (!PeerTransferStatus.SENT.equals(((PeerSocketHandler)PeerServer.this.remoteConnections.get(var2)).sendPing())) {
                     PeerServer.this.remoteConnections.remove(var2);
                     --var2;
                  }

                  ++var2;
               }
            }

            PeerServer.this.core.schedule(this, 15L, TimeUnit.SECONDS);
         }
      };
   }

   public int init4() {
      this.connections.setCellRenderer(new DefaultListCellRenderer() {
         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
            if (var6 instanceof JLabel && var2 instanceof PeerSocketHandler) {
               ((JLabel)JLabel.class.cast(var6)).setText(((PeerSocketHandler)PeerSocketHandler.class.cast(var2)).toString());
            }

            return var6;
         }
      });
      new JScrollPane(this.connections);
      this.core.submit(this.sendPing);

      try {
         this.server = new ServerSocket();
         this.server.bind((SocketAddress)null, 5);
         this.serverPort = this.server.getLocalPort();
         Enumeration var2 = NetworkInterface.getNetworkInterfaces();

         while(var2.hasMoreElements()) {
            NetworkInterface var3 = (NetworkInterface)var2.nextElement();
            Enumeration var4 = var3.getInetAddresses();

            while(var4.hasMoreElements()) {
               InetAddress var5 = (InetAddress)var4.nextElement();
               if (var5.getHostAddress().matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")) {
                  JLabel var6 = new JLabel(String.format("%s:%s", var5.getHostAddress(), this.serverPort));
                  this.listeningOn.add(var6);
               }
            }
         }

         this.core.newThread(new Runnable() {
            public void run() {
               while(true) {
                  try {
                     Socket var1 = PeerServer.this.server.accept();
                     synchronized(PeerServer.this.connectionLock) {
                        final PeerSocketHandler var3 = new PeerSocketHandler(PeerServer.this.core, var1, PeerServer.this);
                        var3.begin("Live Peer");
                        PeerServer.this.mcc.firePeerConnected(var3);
                        PeerServer.this.remoteConnections.add(var3);
                        EventQueue.invokeLater(new Runnable() {
                           public void run() {
                              PeerServer.this.dlm.addElement(var3);
                           }
                        });
                     }
                  } catch (Exception var6) {
                     PeerServer.this.core.logEvent(Level.SEVERE, var6.getMessage(), var6);
                  }
               }
            }
         }).start();
         return 0;
      } catch (Exception var7) {
         this.core.logEvent(Level.SEVERE, PeerServer.class.getSimpleName(), "Unable to start server", var7);
         return -1;
      }
   }

   protected JComponent designTop() {
      JPanel var1 = new JPanel(new BorderLayout());
      this.listeningOn.setBorder(BorderFactory.createTitledBorder("Listening On:"));
      var1.add(this.listeningOn, "Center");
      var1.add(this.connectionInfo, "East");
      return var1;
   }

   public PeerTransferStatus sendMessage(String var1, PeerTag var2) {
      if (var2 == null) {
         synchronized(this.connectionLock) {
            int var4 = 0;

            for(int var5 = 0; var5 < this.remoteConnections.size(); ++var5) {
               if (PeerTransferStatus.SENT.equals(((PeerSocketHandler)this.remoteConnections.get(var5)).sendMessage(var1))) {
                  ++var4;
               } else {
                  this.remoteConnections.remove(var5);
                  --var5;
               }
            }

            if (var4 > 0) {
               return PeerTransferStatus.SENT;
            } else {
               return PeerTransferStatus.DISCONNECTED;
            }
         }
      } else {
         int var3 = this.remoteConnections.indexOf(var2);
         return var3 == -1 ? PeerTransferStatus.DESTINATION_NOT_FOUND : ((PeerSocketHandler)this.remoteConnections.get(var3)).sendMessage(var1);
      }
   }

   public void connectionStopping(final PeerTag var1) {
      synchronized(this.connectionLock) {
         if (var1 instanceof PeerSocketHandler) {
            this.remoteConnections.remove((PeerSocketHandler)var1);
         }

         EventQueue.invokeLater(new Runnable() {
            public void run() {
               PeerServer.this.dlm.removeElement(var1);
            }
         });
      }

      super.connectionStopping(var1);
   }

   public int getServerPort() {
      return this.serverPort;
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      super.commandEventReceived(commandEvent);
      if (!this.sentPort) {
         this.sentPort = true;
         this.core.setCommandEnvironmentVariable("Network_Server_Port", String.format("%d", this.serverPort));
      }

   }
}
