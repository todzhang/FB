package ddb.dsz.plugin.mirror;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorFileStatus;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorNext;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorRequest;
import ddb.dsz.plugin.peer.PeerTag;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/config-users.png")
@DszName("Reflection")
@DszDescription("Enables a connected Peer to have a mirror of the log files")
public class LiveMirror extends Mirror {
   Object transferLock = new Object();
   Map<Object, MonitorThread> tagToMonitor = new HashMap();

   @Override
   public void newConnection(PeerTag peerTag) {
   }

   @Override
   public void receivedMessage(String message, PeerTag peerTag) {
      super.receivedMessage(message, peerTag);

      try {
         Object var3 = super.unmarshal(message);
         if (var3 instanceof JAXBElement) {
            var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
         }

         if (var3 instanceof MirrorRequest) {
            MonitorThread var4 = (MonitorThread)this.tagToMonitor.get(peerTag);
            if (var4 == null) {
               var4 = new MonitorThread(this.core, this.model, peerTag);
               this.tagToMonitor.put(peerTag, var4);
            }

            this.core.newThread(var4).start();
         }

         if (var3 instanceof MirrorFileStatus) {
            MirrorFileStatus var7 = (MirrorFileStatus)var3;
            MonitorThread var5 = (MonitorThread)this.tagToMonitor.get(peerTag);
            if (var5 == null) {
               var5 = new MonitorThread(this.core, this.model, peerTag);
               this.tagToMonitor.put(peerTag, var5);
            }

            var5.addFile(var7.getFile(), var7.getLength());
         }

         if (var3 instanceof MirrorNext) {
            ((MonitorThread)this.tagToMonitor.get(peerTag)).resume();
         }
      } catch (JAXBException var6) {
      }

   }
}
