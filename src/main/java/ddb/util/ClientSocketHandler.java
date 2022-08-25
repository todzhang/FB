package ddb.util;

import ddb.dsz.core.controller.CoreController;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Closure;

public abstract class ClientSocketHandler {
   private boolean stop = false;
   Socket socket;
   CoreController core;
   Unmarshaller unmarsh;
   Marshaller marsh;
   Long received = 0L;
   Long sent = 0L;
   Logger transactionLogger;
   protected BlockingQueue<JAXBElement<?>> pendingWrites = new LinkedBlockingQueue(10);
   protected BlockingQueue<Object> pendingReads = new LinkedBlockingQueue();
   Closure handleNewMessage;
   static final Charset utf8 = Charset.forName("Utf-8");
   final Runnable forwarder = new Runnable() {
      @Override
      public void run() {
         while(!ClientSocketHandler.this.stop) {
            Object var1 = null;

            try {
               var1 = ClientSocketHandler.this.pendingReads.poll(5L, TimeUnit.SECONDS);
            } catch (InterruptedException var3) {
               ClientSocketHandler.this.core.logEvent(Level.INFO, var3.getMessage(), var3);
            }

            if (var1 != null) {
               ClientSocketHandler.this.handleNewMessage.execute(var1);
            }
         }

      }
   };
   final Runnable writer = new Runnable() {
      BufferedOutputStream bos;

      @Override
      public void run() {
         try {
            this.bos = new BufferedOutputStream(ClientSocketHandler.this.socket.getOutputStream());
         } catch (IOException var7) {
            ClientSocketHandler.this.core.logEvent(Level.SEVERE, var7.getMessage(), var7);
            return;
         }

         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         try {
            while(!ClientSocketHandler.this.stop) {
               JAXBElement var2 = null;

               try {
                  var2 = (JAXBElement)ClientSocketHandler.this.pendingWrites.poll(5L, TimeUnit.SECONDS);
               } catch (InterruptedException var6) {
                  ClientSocketHandler.this.core.logEvent(Level.INFO, var6.getMessage(), var6);
               }

               if (var2 != null) {
                  ClientSocketHandler.this.outgoingItem(var2);
                  var1.reset();
                  ClientSocketHandler.this.marsh.marshal(var2, var1);
                  ClientSocketHandler.this.sent = ClientSocketHandler.this.sent + (long)var1.size();
                  this.bos.write(var1.toByteArray());
                  this.bos.write(1);
                  this.bos.flush();
               }
            }

            synchronized(this) {
               this.notifyAll();
            }
         } catch (SocketException var8) {
         } catch (Exception var9) {
            ClientSocketHandler.this.core.logEvent(Level.SEVERE, var9.getMessage(), var9);
         }

         ClientSocketHandler.this.stop();
      }
   };
   final Runnable reader = new Runnable() {
      BufferedInputStream bufInStream;

      @Override
      public void run() {
         try {
            this.bufInStream = new BufferedInputStream(ClientSocketHandler.this.socket.getInputStream());
            StringBuilder var1 = new StringBuilder();

            while(!ClientSocketHandler.this.stop) {
               byte[] var2 = new byte[8096];
               int var3 = this.bufInStream.read(var2);
               if (var3 == -1) {
                  this.bufInStream.close();
                  ClientSocketHandler.this.stop();
                  return;
               }

               ClientSocketHandler.this.received = ClientSocketHandler.this.received + (long)var3;

               for(int var4 = 0; var4 < var3; ++var4) {
                  if (var2[var4] == 1) {
                     var1.append(new String(var2, 0, var4, ClientSocketHandler.utf8));

                     try {
                        ClientSocketHandler.this.incomingItem(ClientSocketHandler.this.unmarsh.unmarshal(new StringReader(var1.toString())));
                     } catch (JAXBException var8) {
                        ClientSocketHandler.this.core.logEvent(Level.WARNING, var8.getMessage(), var8);
                     }

                     var3 -= var4 + 1;
                     var2 = Arrays.copyOfRange(var2, var4 + 1, var2.length);
                     var4 = 0;
                     var1.setLength(0);
                  }
               }

               var1.append(new String(var2, 0, var3));
            }

            synchronized(this) {
               this.notifyAll();
            }
         } catch (SocketException var9) {
         } catch (Exception var10) {
            ClientSocketHandler.this.core.logEvent(Level.SEVERE, var10.getMessage(), var10);
         }

         ClientSocketHandler.this.stop();
      }
   };

   public Marshaller getMarshaller() {
      return this.marsh;
   }

   public Unmarshaller getUnmarshaller() {
      return this.unmarsh;
   }

   protected void incomingItem(Object var1) {
      this.pendingReads.offer(var1);
   }

   protected void outgoingItem(Object var1) {
   }

   public ClientSocketHandler(CoreController var1, Socket var2, Class<?> var3, Closure var4) throws JAXBException {
      this.core = var1;
      this.socket = var2;
      this.handleNewMessage = var4;
      JAXBContext var5 = JaxbCache.getContext(var3);
      this.unmarsh = var5.createUnmarshaller();
      this.marsh = var5.createMarshaller();

      try {
         this.marsh.setProperty("jaxb.encoding", "UTF-8");
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      this.marsh.setProperty("jaxb.formatted.output", Boolean.TRUE);
   }

   public void start() {
      Thread[] var1 = new Thread[]{this.core.newThread(this.writer), this.core.newThread(this.reader), this.core.newThread(this.forwarder)};
      var1[0].setName(String.format("%s: Writer", this.getName()));
      var1[1].setName(String.format("%s: Reader", this.getName()));
      var1[2].setName(String.format("%s: Forwarder", this.getName()));
      Thread[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Thread var5 = var2[var4];
         var5.start();
      }

   }

   public abstract String getName();

   public void stop() {
      try {
         this.stop = true;
         synchronized(this.writer) {
            this.writer.wait(10000L);
         }

         synchronized(this.reader) {
            this.reader.wait(10000L);
         }

         synchronized(this.forwarder) {
            this.forwarder.wait(10000L);
         }

         this.socket.close();
      } catch (Exception var8) {
         this.core.logEvent(Level.WARNING, var8.getMessage(), var8);
      }

   }

   public void publish(JAXBElement<?> var1) {
      try {
         this.pendingWrites.put(var1);
      } catch (InterruptedException var3) {
         this.core.logEvent(Level.WARNING, (String)null, var3);
      }

   }

   public boolean isConnected() {
      return !this.stop;
   }

   public Long getSent() {
      return this.sent;
   }

   public Long getReceived() {
      return this.received;
   }

   public Socket getSocket() {
      return this.socket;
   }

   protected void setClosure(Closure var1) {
      this.handleNewMessage = var1;
   }
}
