package ddb.dsz.plugin.shell;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.events.NewHostEvent;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.controller.CoreController.OperationState;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.library.console.Console;
import ddb.dsz.library.console.Console.StartPromptReceived;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.shell.jaxb.shellcommands.ObjectFactory;
import ddb.dsz.plugin.shell.jaxb.shellcommands.ShellMapType;
import ddb.util.JaxbCache;
import ddb.util.Pair;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusListener;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/alevt.png")
@DszName("Shell")
@DszDescription("A command console")
public class Shell extends Console implements Plugin, IdCallback, FocusListener, InternalCommandHandler {
   public static final String SHELLSTRS = "Shell/shellStartStrings.xml";
   List<Pair<String, String>> shells = new Vector();
   boolean started = false;
   JLayeredPane shellDisplay = new JLayeredPane();
   JComponent consoleDisplay;
   ShellConfiguration configDisplay;
   TaskId pendingCommand = null;
   private final Runnable CommandEnded = new Runnable() {
      public void run() {
         Shell.this.commandLine.setEnabled(false);
         if (!Shell.this.started) {
            Shell.this.configDisplay.commandFailed();
            Shell.this.setStatus("Shell command failed");
         } else {
            Shell.this.setStatus("Shell terminated");
         }

      }
   };

   @Override
   public String getHistory() {
      return "Shell/history.xml";
   }

   public Shell() {
      super.setName("Shell");
      super.setCareAboutLocalEvents(true);
      this.setupBuiltinCommands();
   }

   @Override
   public boolean startCommand(String var1) {
      return this.startCommand((Object)null, var1);
   }

   public boolean startCommand(Object var1, String var2) {
      HostInfo var3 = null;
      if (var1 instanceof String) {
         var3 = this.core.getHostById(var1.toString());
      } else if (var1 instanceof HostInfo) {
         var3 = (HostInfo)var1;
      } else if (var1 != null) {
         var3 = this.core.getHostById("localhost");
      }

      try {
         String var4 = "";
         if (var3 != null && var3.isLocal()) {
            var4 = "local ";
         }

         super.clearOutputScreen("");
         this.setStatus("Starting Remote Shell");
         this.core.startCommand(var4 + var2, this, this, (HostInfo)null);
         if (var3 != null) {
            this.setHost(var3);
         }

         return true;
      } catch (DispatcherException var5) {
         this.core.logEvent(Level.INFO, var5.getMessage(), var5);
         this.configDisplay.commandFailed();
         this.setStatus("");
         return false;
      }
   }

   @Override
   protected void fini3() {
      Task var1 = this.core.getRunningTaskById(this.pendingCommand);
      if (var1 != null) {
         try {
            this.core.startCommand("stop " + var1.getId().getId(), (IdCallback)null, (Object)null, this.target);
         } catch (DispatcherException var3) {
            this.core.logEvent(Level.WARNING, "Unable to stop shell program", var3);
         }
      }

   }

   @Override
   protected String getConsoleLogName() {
      return "Shell-screen";
   }

   @Override
   protected void setupCommandLineSpecificActionMaps() {
      super.setupCommandLineSpecificActionMaps();
      ActionMap var1 = this.commandLine.getActionMap();
      var1.put("complete", new ShellCompletionAction(this, this.commandSet));
   }

   protected int init3() {
      this.consoleDisplay = this.mainDisplayPanel;
      this.configDisplay = new ShellConfiguration(this);
      this.configDisplay.configureMaps(this.core);
      super.setDisplay(this.shellDisplay);
      this.shellDisplay.add(this.consoleDisplay, JLayeredPane.DEFAULT_LAYER);
      this.shellDisplay.add(this.configDisplay, JLayeredPane.MODAL_LAYER);
      this.consoleDisplay.setVisible(false);
      this.configDisplay.setVisible(true);
      this.configDisplay.showUser(false);
      this.shellDisplay.addComponentListener(new ComponentAdapter() {
         public void componentHidden(ComponentEvent var1) {
            this.event();
         }

         public void componentMoved(ComponentEvent var1) {
            this.event();
         }

         public void componentResized(ComponentEvent var1) {
            this.event();
         }

         public void componentShown(ComponentEvent var1) {
            this.event();
         }

         private void event() {
            Shell.this.consoleDisplay.setBounds(new Rectangle(Shell.this.shellDisplay.getWidth(), Shell.this.shellDisplay.getHeight()));
            Rectangle var1 = new Rectangle(Shell.this.configDisplay.getPreferredSize().width, Shell.this.configDisplay.getPreferredSize().height);
            int var2 = Shell.this.shellDisplay.getWidth() - var1.width;
            int var3 = Shell.this.shellDisplay.getHeight() - var1.height;
            var1.x = var2 / 2;
            var1.y = var3 / 2;
            Shell.this.configDisplay.setBounds(var1);
            Shell.this.shellDisplay.validate();
         }
      });
      JButton var1 = new JButton("New...");
      this.southPanel.add(var1, "East");

      try {
         JAXBContext var2 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var3 = var2.createUnmarshaller();
         URL var4 = this.getClass().getClassLoader().getResource("Shell/shellStartStrings.xml");
         if (var4 != null) {
            Object var5 = var3.unmarshal(var4);
            if (var5 instanceof JAXBElement) {
               JAXBElement var6 = (JAXBElement)var5;
               if (var6.getValue() instanceof ShellMapType) {
                  ShellMapType var7 = (ShellMapType)var6.getValue();
                  this.configDisplay.addAllSystems(var7.getSystem());
               }
            }
         }
      } catch (JAXBException var8) {
         this.core.logEvent(Level.WARNING, var8.getMessage(), var8);
      }

      this.configDisplay.addAllHosts(this.core.getHosts());
      var1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            List var2 = Shell.this.configDisplay.getConfiguration();
            var2.add("-stealfocus");
            Shell.this.startNewPluginWithInitArgs(Shell.this.getClass(), var2);
         }
      });
      return 0;
   }

   @Override
   protected boolean parseArgument3(String var1, String var2) {
      if (var1.equals("-stealfocus")) {
         this.core.StealFocus(this);
         return true;
      } else {
         return this.configDisplay.parseArgument(var1, var2);
      }
   }

   public void startNewPluginWithInitArgs(Class<?> var1, List<String> var2) {
      this.core.startNewPlugin(var1, this.getName(), var2, this.detached, true);
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      super.commandEnded(var1);
      if (var1.getId().equals(this.pendingCommand)) {
         this.pendingCommand = null;
         EventQueue.invokeLater(this.CommandEnded);
      }

   }

   @Override
   public void startPromptReceived(CommandEvent var1) {
      EventQueue.invokeLater(new Shell.ShellStartPromptReceived(var1));
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      super.connectionChanged(connectionChangeEvent);
      if (connectionChangeEvent instanceof NewHostEvent) {
         NewHostEvent var2 = (NewHostEvent) connectionChangeEvent;
         this.configDisplay.addNewHost(var2.getHost());
      }

      if (OperationState.Inactive.equals(this.core.getOperationState())) {
         this.configDisplay.disconnected();
      }

   }

   @Override
   public void idAcquired(TaskId taskId, Object var2) {
      super.idAcquired(taskId, var2);
      this.pendingCommand = taskId;
   }

   public HostInfo getHostById(String var1) {
      return this.core.getHostById(var1);
   }

   @Override
   public JComponent getDefaultElement() {
      return (JComponent)(this.started ? super.commandLine : this.configDisplay.getDefaultElement());
   }

   public class ShellStartPromptReceived extends StartPromptReceived implements Runnable {
      public ShellStartPromptReceived(CommandEvent var2) {
         super(var2);
         this.e = var2;
      }

      @Override
      public void run() {
         super.run();
         if (this.e.getId().equals(Shell.this.pendingCommand) && !Shell.this.started) {
            Shell.this.started = true;
            Shell.this.commandLine.setEnabled(true);
            Shell.this.consoleDisplay.setVisible(true);
            Shell.this.configDisplay.setVisible(false);
            Shell.this.shellDisplay.validate();
            Shell.this.commandLine.requestFocusInWindow();
            Task var1 = Shell.this.core.getTaskById(this.e.getId());
            if (var1 != null) {
               Shell.this.setStatus(var1.getTypedCommand());
               Shell.this.setHost(var1.getHost());
            }
         }

      }
   }
}
