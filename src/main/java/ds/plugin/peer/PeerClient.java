package ds.plugin.peer;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;

@DszLogo("images/kwallet2.png")
@DszName("Peer")
@DszDescription("Handles communication between this computer and a Peer")
public class PeerClient extends Peer {
   JLabel label = new JLabel("Address:");
   JTextField field = new JTextField("localhost:0");
   JButton connect = new JButton("Connect");
   PeerSocketHandler socketHandler = null;
   Closure closure = ClosureUtils.nopClosure();
   JLabel connectedTo = new JLabel("");

   protected JComponent designTop() {
      JPanel var1 = new JPanel(new BorderLayout());
      JPanel var2 = new JPanel(new BorderLayout());
      var1.add(this.connectedTo, "East");
      var1.add(var2, "Center");
      var2.add(this.label, "West");
      var2.add(this.field, "Center");
      var2.add(this.connect, "East");
      this.connect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            PeerClient.this.pending();
            PeerClient.this.core.submit(new Runnable() {
               public void run() {
                  if (PeerClient.this.socketHandler != null && PeerClient.this.socketHandler.isConnected()) {
                     PeerClient.this.socketHandler.stop();
                     PeerClient.this.socketHandler = null;
                     PeerClient.this.disconnected();
                  } else {
                     String[] var1 = PeerClient.this.field.getText().split(":");
                     String var2 = "localhost";
                     boolean var3 = false;
                     int var6;
                     if (var1.length == 1) {
                        var6 = Integer.parseInt(var1[0]);
                     } else {
                        var2 = var1[0];
                        var6 = Integer.parseInt(var1[1]);
                     }

                     try {
                        System.out.printf("Connecting to %s:%d\n", var2, var6);
                        PeerClient.this.socketHandler = new PeerSocketHandler(PeerClient.this.core, new Socket(var2, var6), PeerClient.this);
                        PeerClient.this.socketHandler.begin("Replay Peer");
                        PeerClient.this.mcc.firePeerConnected(PeerClient.this.socketHandler);
                        PeerClient.this.connected();
                     } catch (Exception var5) {
                        PeerClient.this.disconnected();
                     }
                  }

               }
            });
         }
      });
      var1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "PeerClient"));
      return var1;
   }

   void pending() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            PeerClient.this.connect.setEnabled(false);
            PeerClient.this.field.setEditable(false);
         }
      });
   }

   void connected() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            PeerClient.this.connect.setText("Disconnect");
            PeerClient.this.connect.setEnabled(true);
            PeerClient.this.field.setEditable(false);
         }
      });
   }

   void disconnected() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            PeerClient.this.connect.setText("Connect");
            PeerClient.this.connectedTo.setText("");
            PeerClient.this.connect.setEnabled(true);
            PeerClient.this.field.setEditable(true);
         }
      });
   }

   public PeerTransferStatus sendMessage(String var1, PeerTag var2) {
      if (var2 != this.socketHandler && var2 != null) {
         return PeerTransferStatus.DESTINATION_NOT_FOUND;
      } else {
         return this.socketHandler == null ? PeerTransferStatus.DISCONNECTED : this.socketHandler.sendMessage(var1);
      }
   }

   public void connectionUpdated(final PeerSocketHandler var1) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            PeerClient.this.connectedTo.setText("Connected to:" + var1.getPeerName());
         }
      });
   }

   public void connectionStopping(PeerTag var1) {
      this.disconnected();
      super.connectionStopping(var1);
   }
}
