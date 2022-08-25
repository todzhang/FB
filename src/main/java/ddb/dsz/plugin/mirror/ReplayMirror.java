package ddb.dsz.plugin.mirror;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorRequest;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorTransfer;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.ObjectFactory;
import ddb.dsz.plugin.peer.PeerTag;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.InflaterInputStream;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

@DszLive(
   live = false,
   replay = true
)
@DszLogo("images/config-users.png")
@DszName("Reflection")
@DszDescription("Mirrors the connected Peer's logs")
public class ReplayMirror extends Mirror {
   PeerTag tag = null;
   JLabel received = new JLabel("0");
   JLabel sent = new JLabel("0");
   ScheduledFuture<?> monitor = null;
   ObjectFactory objFact = new ObjectFactory();
   JCheckBox sendData = new JCheckBox("Retrieve Data", false);
   boolean connected = false;
   File lock;
   Map<String, FileInformation> transferedSize = LazyMap.decorate(new HashMap(), new Transformer() {
      public Object transform(Object var1) {
         FileInformation var2 = null;
         if (var1 instanceof String) {
            String var3 = (String)String.class.cast(var1);
            var2 = new FileInformation();
            var2.setName(var3);
            var2.setSize(0L);
            var2.setSoFar(0L);
            ReplayMirror.this.model.addRecord(var2);
         }

         return var2;
      }
   });
   Runnable RequestData = new Runnable() {
      public void run() {
         try {
            DirectoryStatus var1 = new DirectoryStatus(ReplayMirror.this.core, ReplayMirror.access$201(ReplayMirror.this));
            var1.run();
         } catch (JAXBException var10) {
            ReplayMirror.this.core.logEvent(Level.WARNING, var10.getMessage(), var10);
         } finally {
            try {
               ReplayMirror.this.core.sendMessageToPeer(ReplayMirror.this.marshall(ReplayMirror.this.objFact.createMirrorRequest(new MirrorRequest())), ReplayMirror.this.tag);
            } catch (JAXBException var9) {
               ReplayMirror.this.core.logEvent(Level.WARNING, var9.getMessage(), var9);
            }

         }

      }
   };

   protected int init3() {
      super.mainPanel.add(this.sendData, "North");
      this.lock = new File(this.core.getLogDirectory(), ".replay");

      try {
         this.lock.createNewFile();
      } catch (IOException var2) {
      }

      this.lock.deleteOnExit();
      this.sendData.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            if (ReplayMirror.this.connected && ReplayMirror.this.sendData.isSelected()) {
               ReplayMirror.this.sendData.setEnabled(false);
               ReplayMirror.this.core.submit(ReplayMirror.this.RequestData);
            }

         }
      });
      return 0;
   }

   @Override
   public void receivedMessage(String message, PeerTag peerTag) {
      super.receivedMessage(message, peerTag);
      Object var3 = this.unmarshal(message);
      if (var3 instanceof JAXBElement) {
         var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
      }

      if (var3 instanceof MirrorTransfer) {
         MirrorTransfer var4 = (MirrorTransfer)var3;
         this.handleTransfer(var4.getFile(), var4.getOffset(), var4.getLength(), var4.getData());
      }

   }

   @Override
   public void newConnection(PeerTag peerTag) {
      this.tag = peerTag;
      this.connected = true;
      if (this.sendData.isSelected()) {
         this.sendData.setEnabled(false);
         this.core.submit(this.RequestData);
      }

   }

   public void handleTransfer(String var1, BigInteger var2, BigInteger var3, byte[] var4) {
      FileInformation var5 = (FileInformation)this.transferedSize.get(var1);
      File var6 = new File(String.format("%s/%s", this.core.getLogDirectory(), var1));
      if (!var6.getParentFile().exists()) {
         var6.getParentFile().mkdirs();
      }

      long var7 = 0L;
      if (var6.getParentFile().exists()) {
         try {
            long var13;
            if (var6.exists()) {
               long var9 = var6.length();
               long var11 = var2.longValue();
               var13 = var3.longValue();
               if (var9 < var11) {
                  return;
               }

               if (var9 > var11) {
                  if (var11 + var13 < var9) {
                     var5.setSize(var11 + var13);
                     var5.setSoFar(var5.getSize());
                     this.model.updateRecord(var5);
                     return;
                  }

                  long var15 = var11 + var13 - var9;
                  var2 = BigInteger.valueOf(var9);
                  var3 = BigInteger.valueOf(var15);
                  var7 = var13 - var15;
               }

               if (var6.length() != var2.longValue()) {
                  return;
               }
            }

            var5.setSize(var2.longValue() + var3.longValue());
            var5.setSoFar(var5.getSize());
            FileOutputStream var20 = null;
            int var10 = 0;

            while(var10 < 100) {
               try {
                  var20 = new FileOutputStream(var6, true);
                  break;
               } catch (FileNotFoundException var18) {
                  System.out.println(var10);
                  TimeUnit.MILLISECONDS.sleep(250L);
                  ++var10;
               }
            }

            if (var20 == null) {
               return;
            }

            ByteArrayInputStream var21 = new ByteArrayInputStream(var4);
            InflaterInputStream var22 = new InflaterInputStream(var21);
            byte[] var12 = new byte[1024];

            int var23;
            for(var13 = 0L; var13 < var3.longValue(); var13 += (long)var23) {
               var23 = var22.read(var12);
               if (var7 > 0L) {
                  if (var7 > (long)var23) {
                     var7 -= (long)var23;
                  } else {
                     long var16 = (long)var23 - var7;
                     var20.write(var12, Long.valueOf(var7).intValue(), Long.valueOf(var16).intValue());
                     var7 = 0L;
                  }
               } else {
                  var20.write(var12, 0, var23);
               }
            }

            var20.close();
         } catch (Exception var19) {
            var19.printStackTrace();
         }
      }

      this.model.updateRecord(var5);
      super.requestNext(this.tag);
   }

   // $FF: synthetic method
   static TransferredFilesModel access$201(ReplayMirror var0) {
      return var0.model;
   }
}
