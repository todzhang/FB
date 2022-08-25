package ddb.dsz.plugin.mirror;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.MirrorTransfer;
import ddb.dsz.plugin.mirror.jaxb.mirrorcomms.ObjectFactory;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import ddb.util.JaxbCache;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.DeflaterOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

public class MonitorThread extends DirectoryScanner implements StoppableRunnable {
   public static final int MAX_SIZE = 4096;
   public static final int SEND_THRESH = 16384;
   Map<String, Long> transferedSize = LazyMap.decorate(new HashMap(), new Transformer() {
      public Object transform(Object var1) {
         return 0L;
      }
   });
   Marshaller marsh;
   ObjectFactory of;
   TransferredFilesModel model;
   PeerTag tag;
   final Object resumeLock = new Object();

   public MonitorThread(CoreController var1, TransferredFilesModel var2, PeerTag var3) throws JAXBException {
      super(var1);
      this.model = var2;
      this.tag = var3;
      JAXBContext var4 = JaxbCache.getContext(ObjectFactory.class);
      this.marsh = var4.createMarshaller();
      this.of = new ObjectFactory();
   }

   public void run() {
      if (!this.stop) {
         try {
            synchronized(this.resumeLock) {
               while(!this.scanDirectory(this.core.getLogDirectory(), "")) {
                  TimeUnit.SECONDS.timedWait(this.resumeLock, 3L);
               }
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         this.core.schedule(this, 10L, TimeUnit.SECONDS);
      }
   }

   public void resume() {
      synchronized(this.resumeLock) {
         this.resumeLock.notifyAll();
      }
   }

   protected boolean handleFile(String var1, File var2) {
      FileInformation var3 = this.model.getFileInformation(var1);
      Long var4 = (Long)this.transferedSize.get(var1);
      var3.setName(var1);
      var3.setSize(var2.length());
      int var5 = 0;
      boolean var6 = true;

      while(var2.length() > var4 && (var6 || var4 + 512L < var2.length()) && var5 < 10) {
         try {
            long var7 = var2.length() - var4;
            FileInputStream var9 = new FileInputStream(var2);
            ByteArrayOutputStream var10 = new ByteArrayOutputStream();
            DeflaterOutputStream var11 = new DeflaterOutputStream(var10);
            var9.skip(var4);
            byte[] var12 = new byte[4096];
            long var13 = 0L;

            while(var7 > 0L && var13 < 98304L) {
               int var15 = var9.read(var12);
               if (var15 <= 0) {
                  break;
               }

               var7 -= (long)var15;
               var13 += (long)var15;
               var11.write(var12, 0, var15);
               var11.flush();
            }

            var11.finish();
            this.send(var1, BigInteger.valueOf(var4), BigInteger.valueOf(var13), var10.toByteArray());
            var4 = var4 + var13;
            this.transferedSize.put(var1, var4);
            synchronized(var3) {
               var3.setSoFar(var3.getSoFar() + var13);
            }

            this.model.updateRecord(var3);
            ++var5;
            var6 = false;
         } catch (Exception var18) {
            var18.printStackTrace();
            return false;
         }
      }

      return true;
   }

   private void send(String var1, BigInteger var2, BigInteger var3, byte[] var4) {
      try {
         MirrorTransfer var5 = new MirrorTransfer();
         var5.setData(var4);
         var5.setFile(var1);
         var5.setOffset(var2);
         var5.setLength(var3);
         StringWriter var6 = new StringWriter();
         this.marsh.marshal(this.of.createMirrorTransfer(var5), var6);
         if (this.core.sendMessageToPeer(var6.toString(), this.tag) != PeerTransferStatus.SENT) {
            this.stop();
         }
      } catch (Exception var7) {
         this.core.logEvent(Level.SEVERE, var7.getMessage(), var7);
         this.stop();
      }

   }

   public void addFile(String var1, BigInteger var2) {
      FileInformation var3 = this.model.getFileInformation(var1);
      var3.setSize(var2.longValue());
      var3.setSoFar(var2.longValue());
      this.model.updateRecord(var3);
      this.transferedSize.put(var1, var2.longValue());
   }
}
