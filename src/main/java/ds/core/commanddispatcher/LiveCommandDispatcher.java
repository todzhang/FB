package ds.core.commanddispatcher;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.GuiCommand;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ddb.util.Pair;
import ddb.util.SocketConnectedPredicate;
import ds.core.CommandFormatter;
import ds.core.commanddispatcher.live.CommandInfoClosure;
import ds.core.commanddispatcher.live.CommandListClosure;
import ds.core.commanddispatcher.live.CommandOutputClosure;
import ds.core.commanddispatcher.live.CommandResultClosure;
import ds.core.commanddispatcher.live.CommandStartedClosure;
import ds.core.commanddispatcher.live.ConnectionInfoClosure;
import ds.core.commanddispatcher.live.DataInfoClosure;
import ds.core.commanddispatcher.live.GuiCommandClosure;
import ds.core.commanddispatcher.live.HelpClosure;
import ds.core.commanddispatcher.live.IdMapClosure;
import ds.core.commanddispatcher.live.MessageClosure;
import ds.core.commanddispatcher.live.PongClosure;
import ds.core.commanddispatcher.live.SetFlagsClosure;
import ds.core.commanddispatcher.live.SetTitleClosure;
import ds.core.commanddispatcher.live.ShutdownClosure;
import ds.core.commanddispatcher.live.StartPromptClosure;
import ds.core.commanddispatcher.live.StatisticsClosure;
import ds.core.commanddispatcher.live.StopPromptClosure;
import ds.core.commanddispatcher.live.ThrottleClosure;
import ds.core.commandevents.GuiCommandImpl;
import ds.core.controller.MutableCoreController;
import ds.core.impl.task.TaskImpl;
import ds.jaxb.ipc.Message;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.SwitchClosure;
import org.apache.commons.collections.map.LRUMap;

public class LiveCommandDispatcher extends AbstractCommandDispatcher implements CommandDispatcher {
   public static final String LIVE_IN = "LiveCommandDispatcher Input";
   public static final String LIVE_PARSED = "LiveCommandDispatcher Parsed";
   public static final String LIVE_OUT = "LiveCommandDispatcher Output";
   public static final Charset UTF8 = Charset.forName("UTF-8");
   Closure messageClosures;
   final MessageClosure[] listOfClosures = new MessageClosure[]{new CommandOutputClosure(this), new CommandStartedClosure(this), new CommandResultClosure(this), new DataInfoClosure(this), new CommandInfoClosure(this), new StatisticsClosure(this), new SetFlagsClosure(this), new CommandListClosure(this), new ThrottleClosure(this), new StartPromptClosure(this), new StopPromptClosure(this), new IdMapClosure(this), new ConnectionInfoClosure(this), new SetTitleClosure(this), new HelpClosure(this), new GuiCommandClosure(this), new PongClosure(this), new ShutdownClosure(this)};
   private Predicate isSocketConnected;
   private Socket socket;
   private BufferedInputStream bufInStream;
   private BufferedOutputStream bufOutStream;
   private CommandFormatter formatter;
   private final Object INCOMING_LOCK;
   private BlockingQueue<Message> incomingMessages;
   private BlockingQueue<Message> outgoingMessages;
   private final MutableCoreController mainSystem;
   private Map<String, String> helpCache;
   private boolean useHelpCache;
   private Map<Integer, Pair<IdCallback, Object>> callbacks;
   private MultipleCommandDispatcherClient.ObtainedOperationCallback callback;
   boolean readFinished;
   boolean failureNotified;

   public LiveCommandDispatcher(EventPublisher publisher, MutableCoreController mutableCore, Socket socket, MultipleCommandDispatcherClient.ObtainedOperationCallback obtainedOperationCallback) throws IOException, JAXBException {
      super(publisher, mutableCore);
      this.messageClosures = SwitchClosure.getInstance(this.listOfClosures, this.listOfClosures, new Closure() {
         @Override
         public void execute(Object input) {
            LiveCommandDispatcher.this.mainSystem.logEvent(Level.SEVERE, String.format("Unhandled item: %s", input));
         }
      });
      this.isSocketConnected = SocketConnectedPredicate.getInstance();
      this.INCOMING_LOCK = new Object();
      this.incomingMessages = new LinkedBlockingQueue();
      this.outgoingMessages = new LinkedBlockingQueue();
      this.helpCache = new LRUMap(10);
      this.useHelpCache = true;
      this.callbacks = new Hashtable();
      this.readFinished = false;
      this.failureNotified = false;
      this.socket = socket;
      this.mainSystem = mutableCore;
      this.callback = obtainedOperationCallback;
      this.formatter = new CommandFormatter("xml/schema/ipcComms.xsd");
      this.bufInStream = new BufferedInputStream(socket.getInputStream());
      this.bufOutStream = new BufferedOutputStream(socket.getOutputStream());
      Thread[] threads = new Thread[]{mutableCore.newThread(new Runnable() {
         @Override
         public void run() {
            LiveCommandDispatcher.this.handleSocket();
         }
      }), mutableCore.newThread(new Runnable() {
         @Override
         public void run() {
            LiveCommandDispatcher.this.handleOutgoingMessages();
         }
      }), mutableCore.newThread(new Runnable() {
         @Override
         public void run() {
            LiveCommandDispatcher.this.handleMessages();
         }
      })};
      threads[0].setName("LiveCommandDispatcher:  Read From socket");
      threads[1].setName("LiveCommandDispatcher:  Write To socket");
      threads[2].setName("LiveCommandDispatcher:  Handle Incoming Messages");

      for(Thread t : threads) {
         t.start();
      }

   }

   private int getNextIncomingMessage(byte[] bytes) throws IOException {
      return this.bufInStream.read(bytes);
   }

   public void handleSocket() {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         while(!this.readFinished && this.isSocketConnected.evaluate(this.socket)) {
            byte[] var2 = new byte[8096];
            int var3 = this.getNextIncomingMessage(var2);
            if (var3 == -1) {
               this.bufInStream.close();
               this.socket.close();
               this.socket = null;
               this.readFinished = true;
               return;
            }

            for(int i = 0; i < var3; ++i) {
               if (var2[i] == 1) {
                  byteArrayOutputStream.write(var2, 0, i);
                  String var5 = new String(byteArrayOutputStream.toByteArray(), UTF8);
                  byteArrayOutputStream.reset();
                  if (this.core.isDebugMode()) {
                     this.core.logEvent(Level.FINEST, "LiveCommandDispatcher Input", "Message IN:   \n" + var5);
                  }

                  try {
                     Message var6 = this.formatter.unmarshallReceivedBytes((Reader)(new StringReader(var5)));
                     synchronized(this.INCOMING_LOCK) {
                        this.incomingMessages.put(var6);
                     }
                  } catch (Exception e) {
                     try {
                        byte var7 = 8;

                        for(int var8 = 0; var8 < var3; var8 += var7) {
                           int var9;
                           for(var9 = 0; var9 < var7; ++var9) {
                              if (var8 + var9 < var3) {
                                 System.out.printf("%02x ", var2[var8 + var9]);
                              } else {
                                 System.out.print("  ");
                              }
                           }

                           System.out.print("  ");

                           for(var9 = 0; var9 < var7 && var8 + var9 < var3; ++var9) {
                              if (var2[var8 + var9] >= 32 && var2[var8 + var9] <= 123) {
                                 System.out.printf("%c", var2[var8 + var9]);
                              } else {
                                 System.out.print(".");
                              }

                              System.out.print("  ");
                           }

                           System.out.println();
                        }

                        System.out.println(new String(var2, 0, var3, UTF8));
                     } catch (Throwable var11) {
                        var11.printStackTrace();
                     }

                     this.core.logEvent(Level.WARNING, String.format("Unable to unmarshall data:\n%s\n", var5), e);
                  }

                  var3 -= i + 1;
                  var2 = Arrays.copyOfRange(var2, i + 1, var2.length);
                  i = 0;
               }
            }

            byteArrayOutputStream.write(var2, 0, var3);
         }
      } catch (SocketException socketException) {
         this.core.logEvent(Level.SEVERE, "Socket exception in comms thread\n" + socketException.toString(), socketException);
         this.mainSystem.lpConnectionTerminated();
         this.socket = null;
      } catch (IOException ioException) {
         this.core.logEvent(Level.SEVERE, "IOException caught in handle socket routine\n" + ioException.toString(), ioException);
         if (!this.isSocketConnected.evaluate(this.socket)) {
            this.socketBroken();
            this.core.logEvent(Level.SEVERE, "Connection to dispatcher has been lost");
            this.mainSystem.lpConnectionTerminated();
         }
      }

   }

   @Override
   public void publishEvent(CommandEvent commandEvent) {
      super.publishEvent(commandEvent);
      if (commandEvent instanceof GuiCommand) {
         this.mainSystem.schedule(new LiveCommandDispatcher.GuiCommandRerun(GuiCommand.class.cast(commandEvent)), 1L, TimeUnit.MINUTES);
         this.mainSystem.schedule(new LiveCommandDispatcher.GuiCommandFailed(GuiCommand.class.cast(commandEvent)), 2L, TimeUnit.MINUTES);
      }

   }

   private Message getNextOutgoingMessage() {
      try {
         return this.outgoingMessages.poll(1L, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         this.core.logEvent(Level.FINE, e.getMessage(), e);
         return null;
      }
   }

   public void handleOutgoingMessages() {
      while(true) {
         try {
            if (this.isSocketConnected.evaluate(this.socket)) {
               Message message = this.getNextOutgoingMessage();
               if (message == null) {
                  continue;
               }

               byte[] bytes = this.formatter.formatMessageAsByteArray(message);

               try {
                  if (this.mainSystem.isDebugMode()) {
                     this.mainSystem.logEvent(Level.FINEST, "LiveCommandDispatcher Output", "Message OUT: \n" + new String(bytes));
                  }

                  this.bufOutStream.write(bytes);
                  this.bufOutStream.write(1);
                  this.bufOutStream.flush();
               } catch (IOException var4) {
                  this.core.logEvent(Level.SEVERE, "IOException caught in handle socket routine\n" + var4.toString(), var4);
                  if (!this.isSocketConnected.evaluate(this.socket)) {
                     this.socketBroken();
                     this.core.logEvent(Level.SEVERE, "Connection to dispatcher has been lost");
                     this.mainSystem.lpConnectionTerminated();
                  }
               }
               continue;
            }
         } catch (JAXBException e) {
            this.core.logEvent(Level.WARNING, e.getMessage(), e);
         }

         return;
      }
   }

   public void handleMessages() {
      while(this.incomingMessages.size() > 0 || this.isSocketConnected.evaluate(this.socket)) {
         Message[] messages = null;
         synchronized(this.INCOMING_LOCK) {
            messages = this.incomingMessages.toArray(new Message[this.incomingMessages.size()]);
            this.incomingMessages.clear();
         }

         if (messages != null && messages.length != 0) {
            for(Message msg: messages) {
               if (this.core.isDebugMode()) {
                  try {
                     String asString = this.formatter.formatMessageAsString(msg);
                     this.core.logEvent(Level.FINEST, "LiveCommandDispatcher Parsed", "Rcvd from disp:  \n" + asString);
                  } catch (JAXBException e) {
                     this.core.logEvent(Level.SEVERE, e.getMessage(), e);
                  }
               }

               try {
                  this.messageClosures.execute(msg);
               } catch (Throwable throwable) {
                  this.core.logEvent(Level.SEVERE, throwable.getMessage(), throwable);
               }
            }
         } else {
            try {
               TimeUnit.MILLISECONDS.sleep(10L);
            } catch (Exception var9) {
            }
         }
      }

   }

   public void handleUnknownTaskPrompt(Message message) {
   }

   public boolean isConnected() {
      if (this.socket == null) {
         return false;
      } else if (this.socket.isClosed()) {
         return false;
      } else {
         return this.socket.isConnected();
      }
   }

   public void addPrefixes(TaskId taskId, List<String> prefixes) throws DispatcherException, IOException, JAXBException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createAddPrefixes(taskId, prefixes));
      }
   }

   private void enqueMessage(Message message) {
      try {
         this.outgoingMessages.put(message);
      } catch (Exception var3) {
         this.core.logEvent(Level.SEVERE, "Unable to enque message!");
      }
   }

   public void disconnect() {
      if (this.isSocketConnected.evaluate(this.socket)) {
         try {
            this.socket.close();
         } catch (IOException e) {
            this.mainSystem.logEvent(Level.WARNING, "Error while closing socket", e);
         }

      }
   }

   public void interruptCommand(TaskId taskId) throws JAXBException, IOException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         this.mainSystem.logEvent(Level.INFO, "interruptCommand: Not sending interrupt.  No dispatcher connection");
      } else {
         this.enqueMessage(this.formatter.createInterruptCommand(taskId));
      }
   }

   public void requestCommandListUpdate() throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createListCommands());
      }
   }

   public void requestHelpStatement(String helpType, HostInfo hostInfo) throws JAXBException, IOException {
      String var3 = (String)this.helpCache.get(helpType);
      this.enqueMessage(this.formatter.createGetHelp(helpType, hostInfo));
   }

   public int requestStatistics() throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         Message var1 = this.formatter.createGetStatistics();
         this.enqueMessage(var1);
         return var1.getReq().getReqId();
      }
   }

   public void setUseHelpCache(boolean useHelpCache) {
      this.useHelpCache = useHelpCache;
   }

   public void restartCommandOutput(TaskId var1) throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createRestartOutput(var1));
      }
   }

   public void sendPing() throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createPing());
      }
   }

   public void sendPromptReply(int reqId, TaskId taskId, String cmdValue) throws IOException, JAXBException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createUserEntry(reqId, taskId, cmdValue));
      }
   }

   public void sendPromptStopped(TaskId var1, int reqId) throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createPromptStopped(reqId, var1));
      }
   }

   public void sendShutdownNotification() throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.sendPing();
         this.enqueMessage(this.formatter.createShutdown());
         this.mainSystem.schedule(new Runnable() {

            @Override
            public void run() {
               try {
                  LiveCommandDispatcher.this.sendPing();
                  LiveCommandDispatcher.this.mainSystem.schedule(this, 1L, TimeUnit.SECONDS);
               } catch (Exception var2) {
               }

            }
         }, 1L, TimeUnit.SECONDS);
      }
   }

   public void startCommand(String fullCommand, IdCallback idCallback, Object o, HostInfo prospectiveHost) throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         TaskImpl task = new TaskImpl(fullCommand, prospectiveHost);
         synchronized(this.mainSystem) {
            this.mainSystem.addNewTask(task);
         }

         task.setState(TaskState.TASKED);
         Integer var6 = task.getTempId();
         this.callbacks.put(var6, new Pair(idCallback, o));
         this.enqueMessage(this.formatter.createStartCommand(task));
      }
   }

   public void stopCommand(TaskId taskId) throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         this.mainSystem.logEvent(Level.INFO, "stopCommand: Not sending stop.  No dispatcher connection");
      } else {
         this.enqueMessage(this.formatter.createStopCommand(taskId));
      }
   }

   public void stopCommandOutput(TaskId taskId) throws JAXBException, IOException, DispatcherException {
      if (!this.isSocketConnected.evaluate(this.socket)) {
         throw new DispatcherException("Not connected");
      } else {
         this.enqueMessage(this.formatter.createStopOutput(taskId));
      }
   }

   @Override
   public void setOperation(Operation op) {
      super.setOperation(op);
      this.callback.obtainedOperation(this.op, this);
   }

   public MutableCoreController getMainSystem() {
      return this.mainSystem;
   }

   public Pair<IdCallback, Object> extractCallback(int callbackId) {
      return (Pair)this.callbacks.remove(callbackId);
   }

   public void installHelp(String command, String help) {
      this.helpCache.put(command, help);
   }

   public CommandFormatter getFormatter() {
      return this.formatter;
   }

   public void sendGuiCommandResponse(int reqId, boolean success) {
      this.enqueMessage(this.formatter.createGuiCommandResponse(reqId, success));
   }

   private synchronized void socketBroken() {
      if (!this.failureNotified) {
         this.failureNotified = true;
         JOptionPane.showMessageDialog(null, "<html>The interprocess communication socket has failed.<p>Communication with the core has stopped.<p>No new commands may be issued.", "Socket Broken", 0);
      }
   }

   private class GuiCommandFailed implements Runnable {
      final GuiCommand cmd;

      public GuiCommandFailed(GuiCommand cmd) {
         this.cmd = cmd;
      }

      @Override
      public void run() {
         synchronized(this.cmd) {
            if (this.cmd.isHandled()) {
               return;
            }

            this.cmd.handled();
         }

         LiveCommandDispatcher.this.sendGuiCommandResponse(this.cmd.getReqId(), false);
      }
   }

   private class GuiCommandRerun implements Runnable {
      final GuiCommand cmd;

      public GuiCommandRerun(GuiCommand cmd) {
         this.cmd = cmd;
      }

      @Override
      public void run() {
         synchronized(this.cmd) {
            if (!this.cmd.isHandled()) {
               if (this.cmd instanceof GuiCommandImpl) {
                  ((GuiCommandImpl)GuiCommandImpl.class.cast(this.cmd)).eraseTask();
               }

               LiveCommandDispatcher.this.publisher.publish(this.cmd);
            }
         }
      }
   }
}
