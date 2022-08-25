package ds.plugin.peer;

import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import ds.core.DSConstants;
import ds.core.controller.MutableCoreController;
import ds.gui.PluginWorkbench;
import ds.jaxb.external.RemoteMessage;
import ds.plugin.PluginContainer;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class Peer extends PluginContainer {
   PeerWorkbench myWorkbench;
   protected MutableCoreController mcc;
   protected JPanel mainPanel;

   public Peer() {
      super.setUserClosable(false);
   }

   @Override
   public boolean ShowButtons() {
      return false;
   }

   protected final int init3() {
      if (this.core instanceof MutableCoreController) {
         this.mcc = (MutableCoreController)MutableCoreController.class.cast(this.core);
         this.mcc.registerPeer(this);
      }

      this.myWorkbench = new PeerWorkbench(this, this.core, DSConstants.SUB_TAB_ALIGNMENT);
      this.mainPanel = new JPanel(new BorderLayout());
      this.mainPanel.add(this.designTop(), "North");
      this.mainPanel.add(this.myWorkbench, "Center");
      super.setDisplay(this.mainPanel);
      return this.init4();
   }

   protected int init4() {
      return 0;
   }

   @Override
   public PluginWorkbench getChildWorkbench() {
      return this.myWorkbench;
   }

   protected abstract JComponent designTop();

   public abstract PeerTransferStatus sendMessage(String var1, PeerTag var2);

   public void receivedMessage(RemoteMessage var1, PeerTag var2) {
      this.mcc.fireReceivedMessage(var1.getMessage(), var2);
   }

   public void connectionUpdated(PeerSocketHandler var1) {
   }

   public void connectionStopping(PeerTag var1) {
      this.mcc.firePeerDisconnected(var1);
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      if (!super.allowNewInstance(clazz)) {
         return false;
      } else if (!this.myWorkbench.allowNewInstance(clazz)) {
         return false;
      } else {
         return !Peer.class.isAssignableFrom(clazz);
      }
   }
}
