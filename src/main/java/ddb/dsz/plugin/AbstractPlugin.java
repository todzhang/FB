package ddb.dsz.plugin;

import ddb.detach.AbstractTabbable;
import ddb.detach.Tabbable;
import ddb.detach.TabbableOption;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszDetachable;
import ddb.dsz.annotations.DszHideable;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.GuiCommand;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.events.HostEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.util.proxy.DszProxyHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

@DszLive(
   live = false,
   replay = false
)
@DszLogo("images/PluginAdapter/gnome-unknown.png")
@DszDescription("Abstract implementation of a Plugin")
@DszName("AbstractPlugin")
@DszUserStartable(true)
public abstract class AbstractPlugin extends AbstractTabbable implements Plugin, Comparable<Tabbable>, InternalCommandHandler {
   protected final PropertyChangeListener contentsChanged = propertyChangeEvent -> AbstractPlugin.this.fireContentsChanged();
   protected final PropertyChangeListener contentsChangedRequestFocus = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
         AbstractPlugin.this.fireContentsChangedRequestFocus();
      }
   };
   private JComponent display = new JPanel();
   private JComponent fullDisplay;
   protected CoreController core;
   protected JComponent parentDisplay;
   protected Dimension prefferedSize;
   private boolean userClosable;
   private boolean canClose;
   private boolean localEvents;
   protected boolean detached;
   protected JMenuBar menuBar;
   protected Collection<Task> tasksExamined = new Vector();
   protected HostInfo target = null;
   boolean showStatus = true;
   String identifier = null;
   protected boolean onlyLive = false;
   private Predicate evaluateGuiCommand = PredicateUtils.falsePredicate();
   protected JLabel hostRenderer;

   @Override
   protected synchronized JComponent getTabbableSpecificRenderComponent() {
      if (this.hostRenderer == null) {
         this.hostRenderer = new JLabel("--");
         this.hostRenderer.setHorizontalTextPosition(0);
      }

      return this.hostRenderer;
   }

   protected void setOnlyLive(boolean onlyLive) {
      this.onlyLive = onlyLive;
   }

   private final void parseArguments(List<String> arguments) {
      if (arguments != null) {
         for(String arg: arguments) {
            String[] var4 = arg.split("=", 2);
            if (var4.length == 2) {
               this.parseArgument(var4[0], var4[1]);
            } else {
               this.parseArgument(var4[0], (String)null);
            }
         }

      }
   }

   protected final boolean parseArgument(String var1, String var2) {
      return this.parseArgument2(var1, var2);
   }

   protected boolean parseArgument2(String var1, String var2) {
      return false;
   }

   public AbstractPlugin() {
      this.display.setLayout(new FlowLayout());
      this.display.add(new JLabel("No display specified"));
      super.setName(new String("AbstractPlugin"));
      this.prefferedSize = new Dimension(500, 250);
      this.menuBar = null;
      this.userClosable = true;
      this.canClose = true;
      this.localEvents = false;
      DszDescription dszDescription = this.getClass().getAnnotation(DszDescription.class);
      if (dszDescription != null) {
         super.setShortDescription(dszDescription.value());
      }

      DszLogo dszLogo = this.getClass().getAnnotation(DszLogo.class);
      if (dszLogo != null) {
         this.setLogo((this.getClass().getAnnotation(DszLogo.class)).value());
      }

      DszHideable annotation = this.getClass().getAnnotation(DszHideable.class);
      if (annotation == null) {
         super.setHideable(true);
         super.setUnhideable(true);
      } else {
         super.setHideable(annotation.hide());
         super.setUnhideable(annotation.unhide());
      }

      DszDetachable dszDetachable = this.getClass().getAnnotation(DszDetachable.class);
      if (dszDetachable != null) {
         super.setDetachable(dszDetachable.value());
      } else {
         super.setDetachable(true);
      }

      this.fullDisplay = new JPanel(new BorderLayout());
      this.fullDisplay.add(this.display, "Center");
   }

   protected void setDisplay(JComponent display) {
      this.fullDisplay.remove(this.display);
      this.display = display;
      this.fullDisplay.add(this.display, "Center");
      super.registerDisplay(this.fullDisplay);
   }

   @Override
   public final int init(CoreController core, JComponent parentDisplay, List<String> args) {
      core.logEvent(Level.FINEST, "Executing init() in " + this.getName());
      this.core = core;
      this.parentDisplay = parentDisplay;
      this.core.setupKeyBindings(this.fullDisplay);
      if (this.getLogo() != null) {
         this.setLogo(this.getLogo());
      }

      int var4 = this.init2();
      if (var4 != 0) {
         return var4;
      } else {
         this.parseArguments(args);
         return this.postParseArguments();
      }
   }

   protected int postParseArguments() {
      return 0;
   }

   protected int init2() {
      return 0;
   }

   @Override
   public Dimension getPreferredSize() {
      return this.prefferedSize;
   }

   @Override
   public JMenuBar getMenuBar() {
      return this.menuBar;
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      switch(commandEvent.getType()) {
      case STARTED:
         this.commandStarted(commandEvent);
         break;
      case SET_FLAGS:
         this.commandSetFlags(commandEvent);
         break;
      case START_PROMPT:
         this.commandStartPrompt(commandEvent);
         break;
      case STOP_PROMPT:
         this.commandStopPrompt(commandEvent);
         break;
      case OUTPUT:
         this.commandOutput(commandEvent);
         break;
      case PAUSED:
         this.commandPaused(commandEvent);
         break;
      case ENDED:
         this.commandEnded(commandEvent);
         break;
      case HELP:
         this.commandHelp(commandEvent);
         break;
      case INFO:
         this.commandInfo(commandEvent);
         break;
      case COMMANDLISTUPDATED:
         this.commandList(commandEvent);
         break;
      case BACKGROUNDED:
         this.commandBackgrounded(commandEvent);
         break;
      case GUICOMMAND:
         this.commandGui((GuiCommand)GuiCommand.class.cast(commandEvent));
      }

   }

   protected void commandList(CommandEvent var1) {
   }

   protected void commandStarted(CommandEvent var1) {
   }

   protected void commandSetFlags(CommandEvent var1) {
   }

   protected void commandStartPrompt(CommandEvent var1) {
   }

   protected void commandStopPrompt(CommandEvent var1) {
   }

   protected void commandOutput(CommandEvent var1) {
   }

   protected void commandPaused(CommandEvent var1) {
   }

   protected void commandEnded(CommandEvent var1) {
   }

   protected final void commandGui(GuiCommand var1) {
      synchronized(var1) {
         if (var1.isHandled()) {
            return;
         }

         if (!this.evaluateGuiCommand.evaluate(var1)) {
            return;
         }

         var1.handled();
      }

      this.handleGuiCommand(var1);
   }

   protected void setGuiCommandPredicate(Predicate predicate) {
      if (predicate == null) {
         predicate = PredicateUtils.falsePredicate();
      }

      this.evaluateGuiCommand = predicate;
   }

   protected void handleGuiCommand(GuiCommand guiCommand) {
   }

   protected void commandHelp(CommandEvent commandEvent) {
   }

   protected void commandInfo(CommandEvent commandEvent) {
   }

   protected void commandBackgrounded(CommandEvent commandEvent) {
   }

   @Override
   public final void fini() {
      this.fini2();
      this.core.logEvent(Level.FINEST, "Executing fini() in " + this.getName());
      this.target = null;
      super.fini();
   }

   protected void fini2() {
   }

   public void contentsChanged() {
      this.fireContentsChanged();
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isUserClosable() {
      return this.userClosable;
   }

   @Override
   public boolean isClosable() {
      return this.canClose;
   }

   @Override
   public void setUserClosable(boolean userClosable) {
      this.userClosable = userClosable;
   }

   @Override
   public void setCanClose(boolean canClose) {
      this.canClose = canClose;
   }

   @Override
   public void receivedFocus() {
   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      return false;
   }

   @Override
   public boolean caresAboutLocalEvents() {
      return this.localEvents;
   }

   protected void setCareAboutLocalEvents(boolean localEvents) {
      this.localEvents = localEvents;
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      if (connectionChangeEvent instanceof HostEvent) {
         HostEvent var2 = (HostEvent) connectionChangeEvent;
         HostInfo var3 = var2.getHost();
         if (var3 != null && var3.sameHost(this.target)) {
            this.target = var3;
            if (this.workbench != null) {
               this.workbench.enqueAction(WorkbenchAction.RENAMETAB, new Object[]{this, this.getName()});
            }
         }
      }

   }

   @Override
   public void setDetachable(boolean detachable) {
      super.setDetachable(detachable);
   }

   @Override
   public void setHideable(boolean hideable) {
      super.setHideable(hideable);
   }

   @Override
   public void setLogo(String logo) {
      this.setLogo(logo, this.core != null ? this.core.getTabImageSize() : new Dimension(32, 32));
   }

   @Override
   public void setShortDescription(String shortDescription) {
      super.setShortDescription(shortDescription);
   }

   @Override
   public void setUnhideable(boolean unhideable) {
      super.setUnhideable(unhideable);
   }

   @Override
   public TabbableOption getRegularOptions() {
      return null;
   }

   @Override
   public TabbableOption getStaticOptions() {
      return null;
   }

   public void execute(Runnable var1) {
      this.core.execute(var1);
   }

   @Override
   public JComponent getDefaultElement() {
      return null;
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return true;
   }

   @Override
   public Comparator<CommandEvent> getComparator() {
      return null;
   }

   @Override
   public void setTarget(HostInfo hostInfo) {
      this.setHost(hostInfo);
   }

   protected void setHost(HostInfo hostInfo) {
      this.target = hostInfo;
      String var2 = "";
      Color var3 = Color.YELLOW;
      Color var4 = Color.CYAN;
      if (hostInfo == null) {
         var3 = Color.BLACK;
         var4 = null;
      } else {
         if (hostInfo.isLocal()) {
            var4 = Color.RED;
            var3 = Color.WHITE;
         } else {
            var4 = null;
            var3 = Color.BLACK;
         }

         var2 = hostInfo.getId();
      }

      this.status.setHost(var2, var3, var4);
      this.status.notifyObservers();
      this.setDisplayedName();
   }

   @Override
   public boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback) {
      return false;
   }

   @Override
   public boolean runInternalCommand(List<String> commands, TaskId taskId, InternalCommandCallback internalCommandCallback) {
      return false;
   }

   @Override
   public String getDockedTitle() {
      String var1 = super.getDockedTitle();
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            if (AbstractPlugin.this.hostRenderer != null) {
               if (AbstractPlugin.this.target == null) {
                  AbstractPlugin.this.hostRenderer.setText("");
               } else {
                  if (!AbstractPlugin.this.target.isLocal() && AbstractPlugin.this.target.isConnected()) {
                     AbstractPlugin.this.hostRenderer.setForeground(Color.BLACK);
                  } else {
                     AbstractPlugin.this.hostRenderer.setForeground(Color.RED);
                  }

                  AbstractPlugin.this.hostRenderer.setText(AbstractPlugin.this.target.isConnected() ? AbstractPlugin.this.target.getId() : "Disconnected");
               }
            }

         }
      });
      return var1;
   }

   @Override
   public String getDetachedTitle() {
      String detachedTitle = super.getDetachedTitle();
      String coreTitle = this.core.getTitle();
      if (this.target != null) {
         detachedTitle = String.format("%s - %s", detachedTitle, this.target.getId());
      }

      if (coreTitle != null) {
         detachedTitle = String.format("%s [%s]", detachedTitle, coreTitle);
      }

      return detachedTitle;
   }

   @Override
   public final String getClazz() {
      return this.getClass().getName();
   }

   @Override
   public final void setIdentifier(String identifier) {
      this.identifier = identifier;
   }

   @Override
   public final String getIdentifier() {
      return this.identifier == null ? this.getClass().getName() : this.identifier;
   }

   @Override
   public boolean isShowStatus() {
      return this.showStatus;
   }

   @Override
   public void setShowStatus(boolean showStatus) {
      this.showStatus = false;
   }

   @Override
   public HostInfo getTarget() {
      return this.target;
   }

   @Override
   public boolean canSetTarget() {
      return true;
   }

   @Override
   public final boolean equals(Object var1) {
      return super.equals(DszProxyHandler.Unwrap(var1));
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }
}
