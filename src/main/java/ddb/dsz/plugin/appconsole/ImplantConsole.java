package ddb.dsz.plugin.appconsole;

import ddb.antialiasing.AntialiasedTextField;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszHideable;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.gui.debugview.DebugView;
import ddb.gui.debugview.Importance;
import ddb.gui.debugview.MessageRecordImpl;
import ddb.util.StreamDumper;
import ddb.util.StringAppender;
import ds.core.DSConstants;
import ds.core.controller.MutableCoreController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/gnome-applications.png")
@DszName("Implant Console")
@DszDescription("An output screen for stdout and stderr of the implant application")
@DszUserStartable(false)
@DszHideable(
   hide = false,
   unhide = true
)
public class ImplantConsole extends NoHostAbstractPlugin implements StringAppender {
   protected DebugView debugView;
   protected JPanel mainPanel;
   protected JTextField commandLine;
   protected InputStream stdOut;
   protected InputStream stdErr;
   protected StreamDumper dumper;
   protected boolean commandRunning;
   protected Process runningProcess;
   private File screenLog;
   private OutputStreamWriter screenLogWriter;
   protected static int BUFFER_SIZE = 500;
   protected int currentBufferFill = 0;
   protected StringBuffer sb;
   boolean establishedConnection = false;
   MutableCoreController mcc = null;
   String command = null;
   List<String> arguments = new Vector();

   public ImplantConsole() {
      this.prefferedSize = new Dimension(1000, 350);
      super.setCanClose(false);
   }

   private void attachStreams(InputStream stdOut, InputStream stdErr) {
      this.stdOut = stdOut;
      this.stdErr = stdErr;
      this.dumper = new StreamDumper(this.stdOut, this.stdErr, this, this.core.getSystemLogger());
      this.dumper.start(this.core);
   }

   public boolean startCommand() {
      if (this.command == null) {
         this.core.logEvent(Level.SEVERE, "Command not provided!");
         return false;
      } else {
         try {
            String[] pathvars = new String[]{"Path", "PATH", "LD_LIBRARY_PATH"};
            File logDir = new File(this.core.getLogDirectory());
            this.core.logEvent(Level.INFO, "Starting '" + this.command + "' in " + logDir.getAbsolutePath());
            this.runningProcess = null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((new File(String.format("%s/%s", this.core.getResourceDirectory(), "../Bin"))).getAbsolutePath());
            stringBuilder.append(";");
            stringBuilder.append((new File(String.format("%s/ExternalLibraries/%s", this.core.getResourceDirectory(), DSConstants.getOsString()))).getAbsolutePath());
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  ImplantConsole.this.commandLine.setText(ImplantConsole.this.command);
               }
            });

            try {
               Vector vector = new Vector();
               vector.add(this.command);
               vector.addAll(this.arguments);
               ProcessBuilder processBuilder = new ProcessBuilder(vector);
               Map environment = processBuilder.environment();

               for(String path: pathvars) {
                  if (environment.get(path) != null) {
                     environment.put(path, String.format("%s;%s", environment.get(path), stringBuilder.toString()));
                  }
               }

               environment.put("DSZ_LP_ENV_RES_DIR_LOCATION", (new File(this.core.getResourceDirectory())).getAbsolutePath());
               environment.put("DSZ_LP_ENV_LOG_DIR_LOCATION", (new File(this.core.getLogDirectory())).getAbsolutePath());
               environment.put("DSZ_LP_ENV_CFG_DIR_LOCATION", (new File(this.core.getUserConfigDirectory())).getAbsolutePath());
               environment.put("DSZ_LP_ENV_OPS_DIR_LOCATION", (new File(this.core.getOpDir())).getAbsolutePath());
               environment.put("DSZ_LP_ENV_DISPATCHER_PORT", String.format("%d", this.core.getDispatcherPort()));
               environment.put("DSZ_LP_ENV_LOCAL_HOST", this.core.getLocalhostAddress());
               environment.put("PYTHONOPTIMIZE", "TRUE");
               processBuilder.directory(logDir);
               this.runningProcess = processBuilder.start();
            } catch (IOException e) {
               this.core.logEvent(Level.SEVERE, e.getMessage(), e);
               return false;
            }

            if (this.runningProcess == null) {
               this.core.logEvent(Level.SEVERE, "ImplantConsole failed to start command: " + this.command);
               System.err.println("ImplantConsole failed to start command: " + this.command);
               return false;
            } else {
               this.attachStreams(this.runningProcess.getInputStream(), this.runningProcess.getErrorStream());
               this.commandRunning = true;
               this.core.schedule(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        ImplantConsole.this.runningProcess.exitValue();
                        if (ImplantConsole.this.mcc != null) {
                           ImplantConsole.this.mcc.applicationEnded(ImplantConsole.this.command);
                        } else {
                           System.out.println("AppConsole exit");
                           JOptionPane.showMessageDialog(ImplantConsole.access$001(ImplantConsole.this), "Command " + ImplantConsole.this.command + " prematurely terminated.\nYou can expect no more output.", "Command terminated", 2);
                        }
                     } catch (IllegalThreadStateException var2) {
                        ImplantConsole.this.core.schedule(this, 1L, TimeUnit.SECONDS);
                     }

                  }
               }, 1L, TimeUnit.SECONDS);
               return true;
            }
         } catch (Exception e) {
            this.core.logEvent(Level.SEVERE, "Command start failed", e);
            return false;
         }
      }
   }

   public void stopProcess() {
      if (this.runningProcess != null) {
         if (this.establishedConnection) {
            try {
               int exitValue = this.runningProcess.waitFor();
               this.core.logEvent(Level.INFO, "Lp exited with value " + exitValue);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         } else {
            this.core.logEvent(Level.SEVERE, "Lp was never connected - destroying it");
            this.runningProcess.destroy();
         }

      }
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      this.establishedConnection = true;
   }

   @Override
   protected final void fini2() {
      this.stopProcess();

      try {
         this.screenLogWriter.flush();
         this.screenLogWriter.close();
      } catch (IOException e) {
         this.core.logEvent(Level.WARNING, "Exception caught while closing " + this.screenLog.getName() + ": " + e.getMessage(), e);
      }

   }

   @Override
   protected int init2() {
      if (this.core instanceof MutableCoreController) {
         this.mcc = (MutableCoreController)MutableCoreController.class.cast(this.core);
      }

      this.debugView = new DebugView();
      boolean isDebug = true;
      if (this.core.getBuildType().toLowerCase().startsWith("release")) {
         isDebug = false;
      }

      this.debugView.setCurrentLevel(isDebug ? Importance.INFO : Importance.WARNING);
      this.debugView.setMinimumLevel(Importance.NOT_SET);
      this.commandLine = new AntialiasedTextField();
      this.commandLine.setEditable(false);
      this.mainPanel = new JPanel(new BorderLayout());
      this.mainPanel.add(this.debugView, "Center");
      this.mainPanel.add(this.commandLine, "North");
      super.setDisplay(this.mainPanel);
      this.setupScreenLog();
      return 0;
   }

   @Override
   protected int postParseArguments() {
      this.core.newThread("AppConsole Start Thread", new Runnable() {
         @Override
         public void run() {
            if (!ImplantConsole.this.startCommand()) {
               StringBuilder stringBuilder = new StringBuilder();
               stringBuilder.append("Unable to start comamnd:\n");
               stringBuilder.append(String.format("\tCommand = '%s'\n", ImplantConsole.this.command));
               if (ImplantConsole.this.arguments != null) {
                  Iterator iterator = ImplantConsole.this.arguments.iterator();

                  while(iterator.hasNext()) {
                     String var3 = (String)iterator.next();
                     stringBuilder.append(String.format("\t\t'%s'\n", var3));
                  }
               }

               ImplantConsole.this.core.logEvent(Level.SEVERE, stringBuilder.toString());
            }

         }
      }).start();
      return super.postParseArguments();
   }

   @Override
   protected final boolean parseArgument2(String arg, String command) {
      this.core.logEvent(Level.FINER, String.format("Parsing arguments:  %s=%s", arg, command));
      if (arg.equalsIgnoreCase("-exe") && command != null) {
         this.command = command;
         return true;
      } else if (arg.equalsIgnoreCase("-arg") && command != null) {
         this.arguments.add(command);
         return true;
      } else if (arg.equalsIgnoreCase("-max") && command != null) {
         try {
            this.debugView.setMaximum(Integer.parseInt(command));
            return true;
         } catch (Exception e) {
            this.core.logEvent(Level.SEVERE, "AppConsole", "Invalid parameter in AppConsole: \n" + command, e);
            return false;
         }
      } else {
         return false;
      }
   }

   private void setupScreenLog() {
      this.screenLog = this.core.createLogFile("LpConsole");

      try {
         this.screenLogWriter = new OutputStreamWriter(new FileOutputStream(this.screenLog), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         this.core.logEvent(Level.WARNING, "UnsupportedEncodingException caught while creating UTF-8 screen log\nUsing default encoding instead", e);

         try {
            this.screenLogWriter = new FileWriter(this.screenLog);
         } catch (IOException ioException) {
            this.core.logEvent(Level.SEVERE, "Exception caught while setting up logfile: " + ioException.getMessage(), ioException);
            this.core.logEvent(Level.SEVERE, "LP console window will not be logged", ioException);
         }
      } catch (FileNotFoundException fileNotFoundException) {
         this.core.logEvent(Level.SEVERE, "Exception caught while setting up logfile: " + fileNotFoundException.getMessage(), fileNotFoundException);
         this.core.logEvent(Level.SEVERE, "LP console window will not be logged", fileNotFoundException);
      }

   }

   @Override
   public void append(String s) {
      try {
         this.screenLogWriter.write(s);
         this.screenLogWriter.flush();
      } catch (IOException e) {
         this.core.logEvent(Level.SEVERE, "Exception caught while writing to screen log", e);
         this.core.logEvent(Level.SEVERE, "Unlogged text: " + s);
      }

      this.debugView.append(s);
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return !this.getClass().equals(clazz);
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      super.commandEventReceived(commandEvent);
      if (commandEvent.getId().getId() == 0) {
         MessageRecordImpl messageRecord = new MessageRecordImpl();
         if (commandEvent.getText().length() == 0) {
            messageRecord.setMessage(commandEvent.getType().toString());
         } else {
            messageRecord.setMessage(commandEvent.getText());
         }

         messageRecord.setPriority(Importance.INFO);
         messageRecord.setSection("Command-Zero");
         messageRecord.setThread(Long.valueOf(Thread.currentThread().getId()).intValue());
         this.debugView.addMessageRecord(messageRecord);
      }

   }

   public static void main(String[] args) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", args.getClass());
      var3.invoke((Object)null, args);
   }

   // $FF: synthetic method
   static JComponent access$001(ImplantConsole var0) {
      return var0.parentDisplay;
   }
}
