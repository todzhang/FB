package ddb.dsz.library.console;

import ddb.GuiConstants;
import ddb.actions.cursor.CursorScope;
import ddb.actions.deletetext.DeleteScope;
import ddb.actions.history.HistoryDirection;
import ddb.actions.history.HistoryListener;
import ddb.antialiasing.AntialiasedJLabel;
import ddb.antialiasing.AntialiasedTextField;
import ddb.console.ColorTheme;
import ddb.console.OptionPane;
import ddb.detach.TabbableOption;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.command.CommandEvent.XmlOutput;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.events.LpTerminatedEvent;
import ddb.dsz.core.controller.CommandSet;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.library.console.actions.ChangeColorTheme;
import ddb.dsz.library.console.actions.CopyCommandLine;
import ddb.dsz.library.console.actions.CutCommandLine;
import ddb.dsz.library.console.actions.DecreaseFontSize;
import ddb.dsz.library.console.actions.IncreaseFontSize;
import ddb.dsz.library.console.actions.InteruptCurrentCommand;
import ddb.dsz.library.console.actions.PasteCommandLine;
import ddb.dsz.library.console.actions.ResetFontSizeToDefault;
import ddb.dsz.library.console.builtins.BuiltinCommand;
import ddb.dsz.library.console.builtins.ClearHandler;
import ddb.dsz.library.console.builtins.CommentHandler;
import ddb.dsz.library.console.builtins.ForegroundCommandHandler;
import ddb.dsz.library.console.builtins.HelpHandler;
import ddb.dsz.library.console.builtins.SetHandler;
import ddb.dsz.library.console.builtins.TitleHandler;
import ddb.dsz.library.console.jaxb.consolecommands.Command;
import ddb.dsz.library.console.jaxb.consolecommands.Commands;
import ddb.dsz.library.console.jaxb.consolecommands.ObjectFactory;
import ddb.dsz.plugin.AbstractPlugin;
import ddb.dsz.plugin.Plugin;
import ddb.history.History;
import ddb.imagemanager.ImageManager;
import ddb.listeners.RightClickListener;
import ddb.predicate.PredicateClosure;
import ddb.predicate.PredicateClosureImpl;
import ddb.util.FileAndTextTransferHandler;
import ddb.util.JaxbCache;
import ddb.util.StringCompletor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.MapTransformer;
import org.apache.commons.collections.functors.SwitchClosure;
import org.apache.commons.collections.map.TransformedMap;

public abstract class Console extends AbstractPlugin implements Plugin, IdCallback, FocusListener, InternalCommandHandler, HistoryListener {
   public static Predicate ALWAYS_DISPLAY = new Predicate() {
      @Override
      public boolean evaluate(Object o) {
         if (o == null) {
            return false;
         } else if (CommandEvent.class.isInstance(o)) {
            CommandEvent commandEvent = CommandEvent.class.cast(o);
            return commandEvent.getId().idMatch(TaskId.GLOBAL.getId());
         } else {
            return false;
         }
      }
   };
   public static final String BUSY_ICON = "/images/firefox-throbber.gif";
   private AbstractAction interruptCurrentCommand = new InteruptCurrentCommand(this, "kill command");
   private AbstractAction increaseFontSize = new IncreaseFontSize(this, "inc font size");
   private AbstractAction decreaseFontSize = new DecreaseFontSize(this, "dec font size");
   private AbstractAction resetFontSizeToDefault = new ResetFontSizeToDefault(this, "reset font size");
   AbstractAction cutCommandLine = new CutCommandLine(this);
   AbstractAction copyCommandLine = new CopyCommandLine(this);
   AbstractAction pasteCommandLine = new PasteCommandLine(this);
   AbstractAction changeColorTheme = new ChangeColorTheme(this);
   public static final String CUSTOM_THEME = "Custom Theme";
   private static final int PREFERRED_WIDTH = 800;
   private static final int PREFERRED_HEIGHT = 600;
   private static final int SPINNER_WIDTH = 100;
   private static final int STATUSBAR_HEIGHT = 40;
   private static final Dimension PREFERRED_SIZE = new Dimension(800, 600);
   private static final String COMMANDS = "/Console/commands.xml";
   protected Runnable stopPromptMode = new Runnable() {
      @Override
      public void run() {
         Console.this.inPromptMode = false;
         Console.this.promptMessageLabel.setVisible(false);
         Console.super.fireContentsChanged();
         Console.this.updateDisplayLayout();
      }
   };
   public static final String INTERNAL_COMMAND_PREFIX = ".";
   protected JPanel mainDisplayPanel;
   protected JPanel southPanel;
   private AntialiasedJLabel statusBar;
   protected AntialiasedTextField commandLine;
   protected ConsoleOutputPane outputPane;
   protected AntialiasedJLabel promptMessageLabel;
   private JLabel feedbackIcon;
   protected Task lastBackgroundedTask;
   protected CommandSet commandSet;
   protected JCheckBoxMenuItem WordWrapEnabled;
   protected JCheckBoxMenuItem AutoScroll;
   private EnterKeyPressAction enterKeyPressAction;
   protected final Stack<Task> currentTasks;
   protected Collection<Task> silentTasks;
   private boolean pendingReply;
   private boolean waitingForHelpStatement;
   private String helpCommand;
   private final Object helpLock;
   private File screenLog;
   private OutputStreamWriter screenLogWriter;
   protected int pendingReqId;
   private TaskId pendingCmdId;
   protected boolean inPromptMode;
   private History<String> normalHistory;
   private History<String> promptHistory;
   protected Hashtable<String, BuiltinCommand> builtinCommands;
   protected JPanel commandLinePanel;
   private JPanel header;
   private Collection<JMenuItem> menuItems = new Vector();
   Transformer toUpper = new Transformer() {
      @Override
      public Object transform(Object var1) {
         return var1 == null ? var1 : var1.toString().toUpperCase();
      }
   };
   private Map<String, String> variables;
   Transformer getValue;
   Predicate shouldIgnore;
   PredicateClosure[] processCommandLineActions;
   Closure defaultProcessAction;
   Closure processCommandLineClosure;

   public void updateDisplayLayout() {
      this.getDisplay().revalidate();
   }

   public String getHistory() {
      return "/Console/history.xml";
   }

   protected boolean isWaitingForSyncResponse() {
      return this.pendingReply;
   }

   protected void setWaitingForSyncResponse(boolean var1) {
      this.pendingReply = var1;
   }

   public Console() {
      this.variables = TransformedMap.decorate(new HashMap(), this.toUpper, TransformerUtils.nopTransformer());
      this.getValue = ChainedTransformer.getInstance(new Transformer[]{this.toUpper, MapTransformer.getInstance(this.variables)});
      this.shouldIgnore = new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            if (!(var1 instanceof CommandEvent)) {
               return true;
            } else if (Console.ALWAYS_DISPLAY.evaluate(var1)) {
               return false;
            } else {
               CommandEvent var2 = (CommandEvent)CommandEvent.class.cast(var1);
               if (Console.this.core.getTaskById(var2.getId()) != null) {
                  return false;
               } else if (var2.getId().idMatch(0)) {
                  return false;
               } else {
                  return !var2.getType().equals(CommandEventType.HELP);
               }
            }
         }
      };
      this.processCommandLineActions = new PredicateClosure[]{new PredicateClosureImpl(new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            return Console.this.inPromptMode;
         }
      }, new Closure() {
         @Override
         public void execute(Object var1) {
            String var2 = var1.toString();

            try {
               Console.this.addHistoryItem(var2);
               Console.this.sendPromptReply(var2);
            } catch (Exception var4) {
               var4.printStackTrace();
               System.err.println("Exception in EnterKeyPressAction.actionPerformed()");
            }

            Console.this.commandLine.setText("");
         }
      }), new PredicateClosureImpl(new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            return Console.this.isWaitingForSyncResponse();
         }
      }, ClosureUtils.nopClosure())};
      this.defaultProcessAction = new Closure() {
         @Override
         public void execute(Object var1) {
            String var2 = var1.toString();
            Console.this.commandLine.setText("");
            Console.this.appendTimestampedString(var2 + "\n", ConsoleOutputPane.OutputLevel.BOLD);
            if (var2.length() != 0) {
               Console.this.addHistoryItem(var2);
               if (var2.startsWith(".")) {
                  Console.this.processBuiltinCommand(var2);
               } else if (!Console.this.startCommand(var2)) {
                  Console.this.appendOutputMessage("DispatcherException - Unable to start command\n", ConsoleOutputPane.OutputLevel.ERROR);
               }

            }
         }
      };
      this.processCommandLineClosure = SwitchClosure.getInstance(this.processCommandLineActions, this.processCommandLineActions, this.defaultProcessAction);
      this.setOnlyLive(true);
      this.currentTasks = new Stack();
      this.silentTasks = new Vector();
      this.mainDisplayPanel = new JPanel();
      this.mainDisplayPanel.setLayout(new BorderLayout());
      this.promptMessageLabel = new AntialiasedJLabel();
      this.promptMessageLabel.setFont(GuiConstants.FIXED_WIDTH_FONT.Basic.deriveFont(0, 18.0F));
      super.setDisplay(this.mainDisplayPanel);
      super.setName("Console");
      super.prefferedSize = PREFERRED_SIZE;
      super.setCareAboutLocalEvents(true);
      this.helpLock = new Object();
      this.inPromptMode = false;
      this.normalHistory = History.getHistoryString(500);
      this.promptHistory = History.getHistoryString(500);
      this.builtinCommands = new Hashtable();
      this.setupBuiltinCommands();
   }

   @Override
   public TabbableOption getStaticOptions() {
      return OptionPane.getInstance();
   }

   protected abstract String getConsoleLogName();

   @Override
   protected final int init2() {
      this.screenLog = this.core.createLogFile(this.getConsoleLogName());
      this.commandSet = this.core.getCommandSet();

      try {
         this.screenLogWriter = new OutputStreamWriter(new FileOutputStream(this.screenLog), "UTF-8");
      } catch (UnsupportedEncodingException var6) {
         this.core.logEvent(Level.WARNING, "UnsupportedEncodingException caught while creating UTF-8 screen log\nUsing default encoding instead", var6);

         try {
            this.screenLogWriter = new FileWriter(this.screenLog);
         } catch (IOException var5) {
            this.core.logEvent(Level.SEVERE, "Exception caught while setting up logfile: " + var5.getMessage(), var5);
            this.core.logEvent(Level.SEVERE, "Console window will not be logged", var5);
            System.err.println("Console window will not be logged.");
         }
      } catch (FileNotFoundException var7) {
         this.core.logEvent(Level.SEVERE, "Exception caught while setting up logfile: " + var7.getMessage(), var7);
         this.core.logEvent(Level.SEVERE, "Console window will not be logged", var7);
         System.err.println("Console window will not be logged.");
      }

      String var1 = System.getProperty("DSMAXCHARS");
      if (var1 != null) {
         this.outputPane = new ConsoleOutputPane(this.core, Integer.parseInt(var1) / 100);
      } else {
         this.outputPane = new ConsoleOutputPane(this.core);
      }

      this.commandLinePanel = new JPanel(new BorderLayout());
      this.southPanel = new JPanel();
      this.southPanel.setLayout(new BorderLayout());
      this.setWaitingForSyncResponse(false);
      this.statusBar = new AntialiasedJLabel();
      this.statusBar.setSize(new Dimension(700, 40));
      this.statusBar.setHorizontalAlignment(0);
      if (this.screenLog != null) {
         this.statusBar.setText("log: " + this.screenLog.getName());
      } else {
         this.statusBar.setText("Console output not being logged");
      }

      this.header = new JPanel();
      this.header.setLayout(new BorderLayout());
      this.header.add(this.statusBar, "Center");
      this.commandLine = new AntialiasedTextField();
      this.commandLine.setFocusTraversalKeysEnabled(false);
      this.commandLine.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1) {
            Console.this.commandLine.requestFocus();
            Console.this.commandLine.requestFocusInWindow();
         }
      });
      this.outputPane.addConnectedThemable(this.commandLine);
      this.setupStandardActionMaps(this.outputPane);
      this.setupStandardActionMaps(this.commandLine);
      this.setupStandardActionMaps(this.mainDisplayPanel);
      this.setupCommandLineSpecificActionMaps();
      this.commandLine.setDragEnabled(true);
      this.commandLine.setTransferHandler(new FileAndTextTransferHandler());
      this.enterKeyPressAction = new EnterKeyPressAction(this);
      this.commandLine.addActionListener(this.enterKeyPressAction);
      this.commandLine.setPreferredSize(new Dimension(950, 35));
      this.feedbackIcon = new JLabel();
      this.feedbackIcon.setDisabledIcon(new ImageIcon(this.getClass().getResource("/images/firefox-throbber.gif")));
      this.feedbackIcon.setIcon(ImageManager.getIcon(ConsoleIcon.IDLE.getIcon(), new Dimension(20, 20)));
      this.feedbackIcon.setSize(new Dimension(40, 42));
      this.commandLinePanel.add(this.feedbackIcon, "West");
      this.commandLinePanel.add(this.commandLine, "Center");
      this.southPanel.add(this.promptMessageLabel, "North");
      this.southPanel.add(this.commandLinePanel, "Center");
      this.mainDisplayPanel.add(this.outputPane, "Center");
      this.mainDisplayPanel.add(this.southPanel, "South");
      this.outputPane.passInMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent var1) {
            Console.this.commandLine.requestFocusInWindow();
         }
      });
      this.core.submit(new Runnable() {
         @Override
         public void run() {
            try {
               Console.this.loadExistingHistoryFile();
            } catch (Exception var2) {
               Console.this.appendOutputMessage("Error while loading history file\n", ConsoleOutputPane.OutputLevel.ERROR);
               var2.printStackTrace();
            }

         }
      });
      this.menuBar = new JMenuBar();
      JMenu var2 = new JMenu("Console");
      this.menuBar.add(var2);
      this.addMenuItem("Save history", ConsoleIcon.SAVE, new LoadSaveHistoryAction(this, this.mainDisplayPanel, this.normalHistory, LoadSaveHistoryAction.HistoryActionType.SAVE), (JMenu)var2);
      this.addMenuItem("Load history", ConsoleIcon.LOAD, new LoadSaveHistoryAction(this, this.mainDisplayPanel, this.normalHistory, LoadSaveHistoryAction.HistoryActionType.LOAD), (JMenu)var2);
      this.addMenuItem("Inc font size", ConsoleIcon.PLUS, this.increaseFontSize, (JMenu)var2);
      this.addMenuItem("Reset font size", (ConsoleIcon)null, this.resetFontSizeToDefault, (JMenu)var2);
      this.addMenuItem("Dec font size", ConsoleIcon.MINUS, this.decreaseFontSize, (JMenu)var2);
      this.addMenuItem("Change color theme", (ConsoleIcon)null, this.changeColorTheme, (JMenu)var2);
      var2.addSeparator();
      this.WordWrapEnabled = new JCheckBoxMenuItem("Word Wrap");
      this.WordWrapEnabled.setSelected(this.outputPane.getWordWrap());
      this.WordWrapEnabled.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            Console.this.outputPane.setWordWrap(!Console.this.outputPane.getWordWrap());
            Console.this.WordWrapEnabled.setSelected(Console.this.outputPane.getWordWrap());
         }
      });
      var2.add(this.WordWrapEnabled);
      this.AutoScroll = new JCheckBoxMenuItem("Auto Scroll");
      this.AutoScroll.setSelected(this.outputPane.getAutoScroll());
      this.AutoScroll.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            Console.this.outputPane.setAutoScroll(!Console.this.outputPane.getAutoScroll());
            Console.this.AutoScroll.setSelected(Console.this.outputPane.getAutoScroll());
         }
      });
      JCheckBoxMenuItem var3 = new JCheckBoxMenuItem("Pause Input");
      var3.setModel(this.outputPane.getPausedModel());
      var2.add(var3);
      JPopupMenu var4 = new JPopupMenu();
      this.addMenuItem("Cut", ConsoleIcon.CUT, this.cutCommandLine, (JPopupMenu)var4);
      this.addMenuItem("Copy", ConsoleIcon.COPY, this.copyCommandLine, (JPopupMenu)var4);
      this.addMenuItem("Paste", ConsoleIcon.PASTE, this.pasteCommandLine, (JPopupMenu)var4);
      this.commandLine.addMouseListener(new RightClickListener(var4));
      this.core.setupKeyBindings(this.outputPane);
      this.core.setupKeyBindings(this.commandLine);
      this.core.setupKeyBindings(this.mainDisplayPanel);
      this.setupMenuKeybindings();
      this.setTheme(OptionPane.getInstance().getSharedTheme());
      return this.init3();
   }

   protected int init3() {
      return 0;
   }

   @Override
   protected final boolean parseArgument2(String var1, String var2) {
      if (var1.equalsIgnoreCase("-max") && var2 != null) {
         Integer var3 = Integer.parseInt(var2);
         this.outputPane.setMaximumCharacters(var3);
      }

      if (var1.equalsIgnoreCase("-theme") && var2 != null) {
         Iterator var5 = OptionPane.getInstance().getAllThemes().iterator();

         ColorTheme var4;
         do {
            if (!var5.hasNext()) {
               this.core.logEvent(Level.WARNING, "Invalid theme name: " + var2);
               return false;
            }

            var4 = (ColorTheme)var5.next();
         } while(!var2.equalsIgnoreCase(var4.getName()));

         this.setTheme(var4);
         return true;
      } else {
         return this.parseArgument3(var1, var2);
      }
   }

   protected boolean parseArgument3(String var1, String var2) {
      return false;
   }

   private void addMenuItem(String var1, ConsoleIcon var2, ActionListener var3, JMenu var4) {
      var4.add(this.createMenuItem(var1, var2, var3));
   }

   private void addMenuItem(String var1, ConsoleIcon var2, ActionListener var3, JPopupMenu var4) {
      var4.add(this.createMenuItem(var1, var2, var3));
   }

   private JMenuItem createMenuItem(String var1, ConsoleIcon var2, ActionListener var3) {
      JMenuItem var4 = new JMenuItem(var1);
      if (var2 != null) {
         var4.setIcon(ImageManager.getIcon(var2.getIcon(), this.core.getLabelImageSize()));
      }

      var4.addActionListener(var3);
      this.menuItems.add(var4);
      return var4;
   }

   @Override
   protected final void fini2() {
      this.fini3();
      this.outputPane.stop();

      try {
         this.screenLogWriter.flush();
         this.screenLogWriter.close();
      } catch (IOException var2) {
         this.core.logEvent(Level.WARNING, "Exception caught while closing " + this.screenLog.getName() + ": " + var2.getMessage(), var2);
      }

      this.builtinCommands.clear();
      this.menuItems.clear();
   }

   protected void fini3() {
   }

   private void loadExistingHistoryFile() throws Exception {
      URL var1 = Console.class.getClassLoader().getResource(this.getHistory());
      if (var1 != null) {
         List var2 = HistoryFileParserWriter.parse(var1.openStream());
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.normalHistory.addHistoryItem(var4);
         }
      }

   }

   public void addHistoryItem(String var1) {
      if (this.isInPromptMode()) {
         if (var1.trim().length() > 0) {
            this.promptHistory.addHistoryItem(var1);
         }
      } else {
         this.normalHistory.addHistoryItem(var1);
      }

   }

   public AntialiasedTextField getCommandLine() {
      return this.commandLine;
   }

   CoreController getController() {
      return this.core;
   }

   public void processCommandLine() {
      this.processCommandLineClosure.execute(this.commandLine.getText());
   }

   public abstract boolean startCommand(String var1);

   protected String getPrefix() {
      StringBuilder var1 = new StringBuilder();
      if (this.getValue.transform("user") != null) {
         var1.append(String.format("USER=%s ", this.getValue.transform("user")));
      }

      return var1.toString();
   }

   void killCurrentCommand() {
      if (this.isWaitingForSyncResponse()) {
         try {
            Task var1 = (Task)this.currentTasks.peek();
            String var2 = var1.getTypedCommand();
            this.core.killCommand(var1);
            this.appendOutputMessage("Killed '" + var2 + "'\n", ConsoleOutputPane.OutputLevel.BOLD);
            this.setWorkingState(Console.ConsoleState.IDLE);
         } catch (DispatcherException var3) {
            this.appendOutputMessage("Exception caught while trying to kill command\n", ConsoleOutputPane.OutputLevel.WARNING);
            this.appendOutputMessage(var3.getMessage() + "\n", ConsoleOutputPane.OutputLevel.WARNING);
         }

         this.evalutateWaitingForSyncTask();
      }
   }

   public void interruptCurrentCommand() {
      if (this.isWaitingForSyncResponse()) {
         try {
            Task var1 = (Task)this.currentTasks.peek();
            String var2 = var1.getCommandName();
            this.core.interruptCommand(var1);
            this.appendOutputMessage(String.format("\n[Interrupt send to command %d: %s]\n", var1.getId().getId(), var2), ConsoleOutputPane.OutputLevel.BOLD);
         } catch (DispatcherException var3) {
            this.appendOutputMessage("Exception caught while trying to interrupt command\n", ConsoleOutputPane.OutputLevel.WARNING);
            this.appendOutputMessage(var3.getMessage() + "\n", ConsoleOutputPane.OutputLevel.WARNING);
         }

      }
   }

   void sendPromptReply(String var1) {
      try {
         this.core.sendPromptReply(this.pendingReqId, this.pendingCmdId, var1);
      } catch (DispatcherException var3) {
         this.core.logEvent(Level.SEVERE, "Unable to send requested input to dispatcher\nCmdId: " + this.pendingCmdId + "  ReqId: " + this.pendingReqId, var3);
         this.appendOutputMessage("Unable to send requested input to dispatcher\n", ConsoleOutputPane.OutputLevel.ERROR);
      }

   }

   protected void commandOutputReceived(CommandEvent var1, Task var2) {
      if (!ALWAYS_DISPLAY.evaluate(var1)) {
         TaskId var3 = var1.getId();
         if (var3.idMatch(0)) {
            this.appendOutputMessage(var1.getText(), ConsoleOutputPane.OutputLevel.DEFAULT);
            return;
         }

         if (this.currentTasks.size() == 0) {
            return;
         }

         if (var2 == null) {
            this.core.logEvent(Level.INFO, "Can't locate a task for this reply.  Dropping output:" + var1.getText());
            return;
         }

         TaskState var4 = var2.getState();
         if (var4.equals(TaskState.KILLED)) {
            return;
         }

         boolean var5 = false;
         synchronized(this.currentTasks) {
            if (!this.currentTasks.contains(var2)) {
               var5 = true;
            } else if (this.silentTasks.contains(var2)) {
               return;
            }
         }

         if (var5) {
            this.appendOutputMessage("What happened? What command is this from?\n", ConsoleOutputPane.OutputLevel.WARNING);
            this.appendOutputMessage("Reply id: " + var3, ConsoleOutputPane.OutputLevel.WARNING);
            if (this.currentTasks.size() != 0) {
               this.appendOutputMessage("\tCurrent task id: " + ((Task)this.currentTasks.peek()).getId(), ConsoleOutputPane.OutputLevel.WARNING);
            }

            this.appendOutputMessage("\n", ConsoleOutputPane.OutputLevel.WARNING);
         }
      }

      String var9 = var1.getText();
      XmlOutput var10 = var1.getXmlOutput();
      if (var9 != null) {
         switch(var10) {
         case ERROR:
            this.appendOutputMessage(var9, ConsoleOutputPane.OutputLevel.ERROR);
            break;
         case GOOD:
            this.appendOutputMessage(var9, ConsoleOutputPane.OutputLevel.NOTICE);
            break;
         case WARNING:
            this.appendOutputMessage(var9, ConsoleOutputPane.OutputLevel.WARNING);
            break;
         case DEFAULT:
         default:
            this.appendOutputMessage(var9, ConsoleOutputPane.OutputLevel.DEFAULT);
         }
      }

      super.fireContentsChanged();
   }

   @Override
   protected void commandBackgrounded(CommandEvent commandEvent) {
      super.commandBackgrounded(commandEvent);
      Task var2 = this.core.getTaskById(commandEvent.getId());
      if (var2 != null) {
         if (this.currentTasks.contains(var2)) {
            this.silentTasks.add(var2);
         }

      }
   }

   public void appendOutputMessage(String var1, ConsoleOutputPane.OutputLevel var2) {
      if (this.screenLogWriter != null && var1 != null) {
         try {
            this.screenLogWriter.write(var1);
            this.screenLogWriter.flush();
         } catch (IOException var4) {
            this.core.logEvent(Level.SEVERE, "Exception caught while writing to screen log", var4);
            this.core.logEvent(Level.SEVERE, "Unlogged text: " + var1, var4);
         }
      }

      this.outputPane.appendOutputMessage(var1, var2);
   }

   public void appendToOutput(String var1) {
      this.appendOutputMessage(var1, ConsoleOutputPane.OutputLevel.DEFAULT);
   }

   public void appendTimestampedString(String var1, ConsoleOutputPane.OutputLevel var2) {
      this.appendOutputMessage(this.getTimestamp() + ">> " + var1, var2);
   }

   private String getTimestamp() {
      Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      String var2 = "0" + Integer.toString(var1.get(11));
      if (var2.length() > 2) {
         var2 = var2.substring(var2.length() - 2);
      }

      String var3 = "0" + Integer.toString(var1.get(12));
      if (var3.length() > 2) {
         var3 = var3.substring(var3.length() - 2);
      }

      String var4 = "0" + Integer.toString(var1.get(13));
      if (var4.length() > 2) {
         var4 = var4.substring(var4.length() - 2);
      }

      String var5 = var2 + ":" + var3 + ":" + var4;
      return var5;
   }

   public void startPromptReceived(CommandEvent var1) {
      EventQueue.invokeLater(new Console.StartPromptReceived(var1));
   }

   public void stopPromptReceived(CommandEvent var1) {
      if (var1.getId().equals(this.pendingCmdId)) {
         EventQueue.invokeLater(this.stopPromptMode);
      }

   }

   public boolean isInPromptMode() {
      return this.inPromptMode;
   }

   public void setInPromptMode(boolean var1) {
      this.inPromptMode = var1;
   }

   @Override
   public void idAcquired(TaskId taskId, Object var2) {
      Task var3 = this.core.getTaskById(taskId);
      if (var3 == null) {
         this.outputPane.appendOutputMessage("Received ID callback for unknown command", ConsoleOutputPane.OutputLevel.WARNING);
      } else {
         this.currentTasks.push(var3);
      }
   }

   protected void setCurrentSynchTask(Task var1) {
      this.currentTasks.push(var1);
   }

   protected void requestHelpStatement(ParsedCommandLine var1, HostInfo var2) {
      synchronized(this.helpLock) {
         this.helpCommand = var1.getCommandName();
         this.waitingForHelpStatement = true;
         this.commandLine.setEnabled(false);
      }

      try {
         this.core.requestHelpStatement(this.helpCommand, var2);
      } catch (DispatcherException var5) {
         this.outputPane.appendOutputMessage("Exception caught while requesting help statement for " + this.helpCommand, ConsoleOutputPane.OutputLevel.ERROR);
         this.waitingForHelpStatement = false;
      }
   }

   public void setWorkingState(Console.ConsoleState var1) {
      EventQueue.invokeLater(new Console.SetWorkingState(var1));
   }

   @Override
   protected void setStatus(String status) {
      EventQueue.invokeLater(new Console.SetStatus(status));
   }

   private void setupMenuKeybindings() {
      Map var1 = this.core.getKeyBindings();
      if (var1 != null) {
         Iterator var2 = this.menuItems.iterator();

         while(var2.hasNext()) {
            JMenuItem var3 = (JMenuItem)var2.next();
            ActionListener[] var4 = var3.getActionListeners();
            ActionListener[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               ActionListener var8 = var5[var7];
               if (var8 instanceof AbstractAction) {
                  AbstractAction var9 = (AbstractAction)AbstractAction.class.cast(var8);
                  Iterator var10 = var1.keySet().iterator();

                  while(var10.hasNext()) {
                     KeyStroke var11 = (KeyStroke)var10.next();
                     if (((String)var1.get(var11)).equals(var9.getValue("Name"))) {
                        var3.setAccelerator(var11);
                     }
                  }
               }
            }
         }

      }
   }

   private void setupStandardActionMaps(JComponent var1) {
      ActionMap var2 = var1.getActionMap();
      var2.put("kill command", this.interruptCurrentCommand);
      var2.put("inc font size", this.increaseFontSize);
      var2.put("dec font size", this.decreaseFontSize);
      var2.put("reset font size", this.resetFontSizeToDefault);
   }

   protected void setupCommandLineSpecificActionMaps() {
      ActionMap var1 = this.commandLine.getActionMap();
      CursorScope.fill(this.commandLine);
      DeleteScope.fill(this.commandLine);
      HistoryDirection.fill(this, var1);
      var1.put("copy text", this.copyCommandLine);
      var1.put("cut text", this.cutCommandLine);
      var1.put("paste text", this.pasteCommandLine);
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      super.commandEnded(var1);
      Task var2 = this.core.getTaskById(var1.getId());
      if (!this.shouldIgnore.evaluate(var1)) {
         if (this.inPromptMode && this.pendingCmdId.equals(var1.getId())) {
            EventQueue.invokeLater(this.stopPromptMode);
         }

         if (this.currentTasks.contains(var2)) {
            this.currentTasks.remove(var2);

            while(this.silentTasks.contains(var2)) {
               this.silentTasks.remove(var2);
            }

            this.removeChildTasks(var2);
            Task var3 = var2.getParentTask();
            if (var3 != null && !this.silentTasks.contains(var3)) {
               this.setStatus(var3.getTypedCommand());
            }

            if (this.currentTasks.size() == 0 && this.isWaitingForSyncResponse()) {
               this.setStatus("");
               super.fireContentsChanged();
            }

            this.evalutateWaitingForSyncTask();
         }
      }
   }

   @Override
   protected void commandHelp(CommandEvent commandEvent) {
      super.commandHelp(commandEvent);
      if (!this.shouldIgnore.evaluate(commandEvent)) {
         synchronized(this.helpLock) {
            if (this.waitingForHelpStatement) {
               if (commandEvent.getCommand().equalsIgnoreCase(this.helpCommand)) {
                  this.outputPane.appendOutputMessage(commandEvent.getText() + "\n", ConsoleOutputPane.OutputLevel.DEFAULT);
                  this.waitingForHelpStatement = false;
                  EventQueue.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                        Console.this.commandLine.setEnabled(true);
                        Console.this.commandLine.requestFocusInWindow();
                     }
                  });
               }

            }
         }
      }
   }

   @Override
   protected void commandOutput(CommandEvent var1) {
      super.commandOutput(var1);
      Task var2 = this.core.getTaskById(var1.getId());
      if (!this.shouldIgnore.evaluate(var1)) {
         if (this.currentTasks.contains(var2) || ALWAYS_DISPLAY.evaluate(var1)) {
            this.commandOutputReceived(var1, var2);
         }

      }
   }

   @Override
   protected void commandSetFlags(CommandEvent var1) {
      super.commandSetFlags(var1);
      Task var2 = this.core.getTaskById(var1.getId());
      if (!this.shouldIgnore.evaluate(var1)) {
         if (this.isWaitingForSyncResponse()) {
            String var3 = var1.getText();
            if (var3.toLowerCase().startsWith("background")) {
               boolean var4 = false;
               synchronized(this) {
                  if (this.currentTasks.contains(var2)) {
                     if (this.currentTasks.size() > 1) {
                        var4 = false;
                     } else {
                        var4 = true;
                     }

                     this.currentTasks.remove(var2);
                     this.evalutateWaitingForSyncTask();
                  }
               }

               if (var4) {
                  this.appendOutputMessage("[" + this.getTimestamp() + "] Backgrounded '" + var2.getTypedCommand() + "'  Id: " + var2.getId() + "\n", ConsoleOutputPane.OutputLevel.BOLD);
               }
            }

            if (var3.toLowerCase().equals("guiflag=focus") && this.currentTasks.contains(var2)) {
            }
         }

      }
   }

   protected void evalutateWaitingForSyncTask() {
      boolean var1 = false;
      synchronized(this) {
         if (this.currentTasks.size() == 0) {
            this.setWaitingForSyncResponse(false);
            var1 = true;
         }
      }

      this.setWorkingState(var1 ? Console.ConsoleState.IDLE : Console.ConsoleState.BUSY);
   }

   private void removeChildTasks(Task var1) {
      LinkedBlockingQueue var2 = new LinkedBlockingQueue();
      var2.offer(var1);

      while(true) {
         Task var3;
         do {
            if (var2.peek() == null) {
               return;
            }

            var3 = (Task)var2.poll();
         } while(var3 == null);

         Iterator var4 = this.currentTasks.iterator();

         Task var5;
         while(var4.hasNext()) {
            var5 = (Task)var4.next();
            if (var5.getParentId().equals(var1.getId())) {
               var2.add(var5);
               var4.remove();
            }
         }

         var4 = this.silentTasks.iterator();

         while(var4.hasNext()) {
            var5 = (Task)var4.next();
            if (var5.getParentId().equals(var1.getId())) {
               var2.add(var5);
               var4.remove();
            }
         }
      }
   }

   @Override
   protected void commandStarted(CommandEvent var1) {
      super.commandStarted(var1);
      TaskId var2 = var1.getId();
      TaskId var3 = var1.getPid();
      Task var4 = this.core.getTaskById(var2);
      Task var5 = this.core.getTaskById(var3);
      if (!this.shouldIgnore.evaluate(var1)) {
         synchronized(this.currentTasks) {
            if (this.currentTasks.contains(var4) || this.currentTasks.contains(var5)) {
               if (((Task)this.currentTasks.peek()).equals(var4)) {
                  if (!this.silentTasks.contains(var4) && !this.silentTasks.contains(var5)) {
                     this.setWorkingState(Console.ConsoleState.BUSY);

                     for(int var8 = 0; var8 < 15 && var4.getHost() == null; ++var8) {
                        try {
                           TimeUnit.MILLISECONDS.sleep(10L);
                        } catch (Exception var11) {
                        }
                     }

                     String var7;
                     if (var4.getHost() == null) {
                        var7 = String.format("default target: %s", this.target != null ? this.target.getId() : "(unknown)");
                     } else {
                        var7 = String.format("target: %s", var4.getHost().getId());
                     }

                     this.appendOutputMessage(String.format("[%s] ID: %d '%s' started [%s]\n", this.getTimestamp(), var4.getId().getId(), var4.getCommandName(), var7), ConsoleOutputPane.OutputLevel.NOTICE);
                     super.fireContentsChanged();
                  }
               } else if (this.currentTasks.contains(var5)) {
                  this.currentTasks.push(var4);
               }

            }
         }
      }
   }

   @Override
   protected void commandStartPrompt(CommandEvent var1) {
      super.commandStartPrompt(var1);
      if (!this.shouldIgnore.evaluate(var1)) {
         this.startPromptReceived(var1);
      }
   }

   @Override
   protected void commandStopPrompt(CommandEvent var1) {
      super.commandStopPrompt(var1);
      Task var2 = this.core.getTaskById(var1.getId());
      if (!this.shouldIgnore.evaluate(var1)) {
         if (this.currentTasks.contains(var2)) {
            this.stopPromptReceived(var1);
         }
      }
   }

   public void moveFocusToCommandLine() {
      this.commandLine.requestFocus();
   }

   public void mouseClicked(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   public void mouseEntered(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   public void mouseReleased(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   public void mouseDragged(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   public void mouseMoved(MouseEvent var1) {
      this.moveFocusToCommandLine();
   }

   @Override
   public void focusGained(FocusEvent var1) {
      this.moveFocusToCommandLine();
   }

   @Override
   public void focusLost(FocusEvent var1) {
   }

   @Override
   public void receivedFocus() {
      super.receivedFocus();
      this.moveFocusToCommandLine();
   }

   public void decreaseFontSize() {
      this.outputPane.decreaseFontSize();
   }

   public void increaseFontSize() {
      this.outputPane.increaseFontSize();
   }

   public void resetFontSizeToDefault() {
      this.outputPane.resetFontSizeToDefault();
   }

   @Override
   public void historyActionPerformed(HistoryDirection historyDirection) {
      History var2 = this.normalHistory;
      if (this.isInPromptMode()) {
         var2 = this.promptHistory;
      }

      String var3 = (String)var2.doHistoryAction(historyDirection, this.commandLine.getText());
      if (var3 != null) {
         this.commandLine.setText(var3);
      }

   }

   public synchronized void setTheme(ColorTheme var1) {
      this.commandLine.setTheme(var1);
      this.outputPane.setTheme(var1);
   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      return this.currentTasks.contains(task);
   }

   public ColorTheme getCurrentTheme() {
      return this.outputPane.getTheme();
   }

   protected void setupBuiltinCommands2() {
      JAXBContext var1 = JaxbCache.getContext(ObjectFactory.class);
      if (var1 != null) {
         Commands var3 = null;

         try {
            Unmarshaller var2 = var1.createUnmarshaller();
            if (var2 == null) {
               return;
            }

            Object var4 = var2.unmarshal(Console.class.getResource("/Console/commands.xml"));
            if (var4 instanceof JAXBElement) {
               var4 = ((JAXBElement)JAXBElement.class.cast(var4)).getValue();
            }

            if (var4 instanceof Commands) {
               var3 = (Commands)Commands.class.cast(var4);
            }

            if (var3 == null) {
               return;
            }
         } catch (Exception var6) {
            if (this.core != null) {
               this.core.logEvent(Level.SEVERE, (String)null, var6);
            } else {
               var6.printStackTrace();
            }

            return;
         }

         Iterator var7 = var3.getCommand().iterator();

         while(var7.hasNext()) {
            Command var5 = (Command)var7.next();
            this.builtinCommands.put(var5.getName(), BuiltinCommand.generate(this, var5, this.builtinCommands));
         }

      }
   }

   protected void setupBuiltinCommands() {
      BuiltinCommand var1 = new BuiltinCommand("help");
      var1.setHandler(new HelpHandler(this, this.builtinCommands));
      var1.setHelpStatement("Usage: help [internalCmd]");
      BuiltinCommand var2 = new BuiltinCommand("clear");
      var2.setHandler(new ClearHandler(this));
      var2.setHelpStatement("Usage: clear\n");
      BuiltinCommand var3 = new BuiltinCommand("foreground");
      var3.setHandler(new ForegroundCommandHandler(this));
      var3.setHelpStatement("Usage: foreground [cmdId]");
      BuiltinCommand var4 = new BuiltinCommand("comment");
      var4.setHandler(new CommentHandler(this));
      var4.setHelpStatement("Usage: comment [comment text]");
      BuiltinCommand var5 = new BuiltinCommand("set");
      var5.setHandler(new SetHandler(this));
      var5.setHelpStatement("Usage: set <variable>[=value]");
      BuiltinCommand var6 = new BuiltinCommand("title");
      var6.setHandler(new TitleHandler(this));
      var6.setHelpStatement("Usage: title <new name>");
      this.builtinCommands.put("help", var1);
      this.builtinCommands.put("clear", var2);
      this.builtinCommands.put("foreground", var3);
      this.builtinCommands.put("comment", var4);
      this.builtinCommands.put("set", var5);
      this.builtinCommands.put("title", var6);
      this.setupBuiltinCommands2();
   }

   public void processBuiltinCommand(String var1) {
      String var2 = var1.trim();
      String var3 = null;
      String var4 = null;
      int var5 = var2.indexOf(32);
      if (var5 == -1) {
         var3 = var2.substring(".".length());
      } else {
         var3 = var2.substring(".".length(), var5);
         var4 = var2.substring(var5).trim();
      }

      if (this.builtinCommands.containsKey(var3)) {
         BuiltinHandler var6 = ((BuiltinCommand)this.builtinCommands.get(var3)).getHandler();
         var6.executeBuiltinCommand(var3, var4);
      } else {
         this.appendOutputMessage("Unrecognized internal command\n", ConsoleOutputPane.OutputLevel.WARNING);
      }

   }

   public void helpBuiltinCommand(String var1) {
      if (var1 != null && var1.length() != 1) {
         String var2 = var1.toLowerCase().trim().substring(1);
         Set var3 = this.builtinCommands.keySet();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (var2.startsWith(var5.toLowerCase())) {
               BuiltinCommand var6 = (BuiltinCommand)this.builtinCommands.get(var5);
               this.appendOutputMessage(var6.getHelpStatement(), ConsoleOutputPane.OutputLevel.DEFAULT);
            }
         }

      }
   }

   public void completeBuiltinCommand(String var1, String var2) {
      Set var3 = this.builtinCommands.keySet();
      String var4 = var1.substring(".".length());
      List var5 = StringCompletor.complete(var4, var3);
      switch(var5.size()) {
      case 0:
         return;
      case 1:
         this.commandLine.setText("." + var5.get(0) + " " + var2);
         int var6 = ".".length() + var5.get(0).toString().length() + 1;
         this.commandLine.setCaretPosition(var6);
         break;
      default:
         StringBuilder var13 = new StringBuilder();
         int var7 = 0;
         Iterator var8 = var5.iterator();

         while(var8.hasNext()) {
            Object var9 = var8.next();
            String var10 = var9.toString();
            var13.append(var10 + "\t");
            ++var7;
            if (var7 % 3 == 0) {
               var13.append("\n");
            }
         }

         synchronized(this) {
            this.appendOutputMessage("Internal commands:\n", ConsoleOutputPane.OutputLevel.NOTICE);
            this.appendToOutput(var13.toString());
            this.appendToOutput("\n");
         }
      }

   }

   public void startNewPluginWithInitArgs(String var1, List<String> var2) {
      Class var3;
      try {
         var3 = Class.forName(var1, false, Console.class.getClassLoader());
      } catch (ClassNotFoundException var5) {
         this.appendOutputMessage("Unable to locate class " + var1 + "\n", ConsoleOutputPane.OutputLevel.WARNING);
         return;
      }

      this.core.startNewPlugin(var3, var3.getSimpleName(), var2, false, false);
   }

   public void startNewPluginWithInitArgs(String var1) {
      if (var1 == null) {
         this.printBuiltinCommandHelp("newplugin");
      } else {
         int var2 = var1.indexOf(32);
         if (var2 == -1) {
            this.startNewPluginWithInitArgs(var1, (List)null);
         } else {
            String var3 = var1.substring(0, var2);
            Vector var4 = new Vector();
            var4.add(var1.substring(var2 + 1));
            this.startNewPluginWithInitArgs(var3, var4);
         }
      }
   }

   public void clearOutputScreen(String var1) {
      this.outputPane.clearAndReplace(var1);
   }

   public void addOutputComment(String var1) {
      this.outputPane.appendToOutput(var1);
   }

   public void foregroundLast() {
      this.foregroundCommand(this.lastBackgroundedTask);
   }

   public void foregroundCommand(Task task) {
      if (task != null) {
         this.foregroundCommand(task.getParentTask());
         synchronized(this.currentTasks) {
            if (!this.currentTasks.contains(task)) {
               this.currentTasks.push(task);
            }

            this.silentTasks.remove(task);
         }

         this.setStatus(task.getTypedCommand());
         this.setWaitingForSyncResponse(true);
         this.setWorkingState(Console.ConsoleState.BUSY);
         if (task.isInPromptMode()) {
            this.pendingCmdId = task.getId();
            EventQueue.invokeLater(new Console.RestorePrompt(this.core.claimPrompt(task)));
         }

         try {
            this.core.restartCommandOutput(task);
         } catch (DispatcherException var4) {
            this.appendOutputMessage("Exception caught while trying to restart command output", ConsoleOutputPane.OutputLevel.ERROR);
            this.currentTasks.remove(task);
            this.evalutateWaitingForSyncTask();
            return;
         }

         switch(task.getState()) {
         case FAILED:
         case SUCCEEDED:
         case KILLED:
            if (!this.currentTasks.contains(task)) {
               return;
            } else {
               this.currentTasks.remove(task);

               while(this.silentTasks.contains(task)) {
                  this.silentTasks.remove(task);
               }

               this.removeChildTasks(task);
               Task var2 = task.getParentTask();
               if (var2 != null && !this.silentTasks.contains(var2)) {
                  this.setStatus(var2.getTypedCommand());
               }

               if (this.currentTasks.size() == 0 && this.isWaitingForSyncResponse()) {
                  this.setStatus("");
                  super.fireContentsChanged();
               }

               this.evalutateWaitingForSyncTask();
            }
         case INITIALIZED:
         case PAUSED:
         case RUNNING:
         case TASKED:
         default:
         }
      }
   }

   public void foregroundCommand(int var1) {
      TaskId var2 = TaskId.GenerateTaskId(var1, this.core.getOperation());
      Task var3 = this.core.getTaskById(var2);
      if (var3 == null) {
         this.outputPane.appendOutputMessage("Command " + var1 + " not found.  Unable to foreground unknown command\n", ConsoleOutputPane.OutputLevel.ERROR);
      } else {
         this.foregroundCommand(var3);
      }
   }

   public void printString(String var1) {
      this.printString(var1, ConsoleOutputPane.OutputLevel.DEFAULT);
   }

   public void printString(String var1, ConsoleOutputPane.OutputLevel var2) {
      this.appendOutputMessage(var1, var2);
   }

   public void printBuiltinCommandHelp(String var1) {
      BuiltinCommand var2 = (BuiltinCommand)this.builtinCommands.get(var1);
      if (var2 != null) {
         if (var2.getHelpStatement() != null) {
            this.printString(var2.getHelpStatement() + "\n", ConsoleOutputPane.OutputLevel.DEFAULT);
         }

      } else {
         this.printString("No such internal command\n", ConsoleOutputPane.OutputLevel.DEFAULT);
      }
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      super.connectionChanged(connectionChangeEvent);
      if (connectionChangeEvent instanceof LpTerminatedEvent) {
         this.inPromptMode = false;
         this.waitingForHelpStatement = false;
         this.currentTasks.clear();
         this.evalutateWaitingForSyncTask();
      }

   }

   @Override
   public JComponent getHeader() {
      return this.header;
   }

   public int getCurrentFontSize() {
      return this.outputPane.getCurrentFontSize();
   }

   public Font getFont() {
      return this.outputPane.getFont();
   }

   public void setTheme(ColorTheme var1, Font var2, int var3) {
      this.setTheme(var1);
      this.outputPane.setCurrentFontSize(var3);
   }

   public ConsoleOutputPane getOutputPane() {
      return this.outputPane;
   }

   public Map<String, String> getVariables() {
      return this.variables;
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }

   public static enum ConsoleState {
      IDLE,
      BUSY;
   }

   public class SetStatus implements Runnable {
      String status;

      public SetStatus(String var2) {
         this.status = var2;
      }

      @Override
      public void run() {
         Console.super.setStatus(this.status);
      }
   }

   public class SetWorkingState implements Runnable {
      Console.ConsoleState state;

      public SetWorkingState(Console.ConsoleState var2) {
         this.state = var2;
      }

      @Override
      public void run() {
         switch(this.state) {
         case IDLE:
            Console.this.feedbackIcon.setEnabled(true);
            Console.this.setLogo(Console.this.getLogo());
            break;
         case BUSY:
            Console.this.feedbackIcon.setEnabled(false);
            Console.this.setLogo(new ImageIcon(this.getClass().getResource("/images/firefox-throbber.gif")));
         }

         Console.this.updateDisplayLayout();
      }
   }

   public class StartPromptReceived implements Runnable {
      protected CommandEvent e;

      public StartPromptReceived(CommandEvent var2) {
         this.e = var2;
      }

      @Override
      public void run() {
         if (Console.this.currentTasks.contains(Console.this.core.getTaskById(this.e.getId()))) {
            Console.this.pendingCmdId = this.e.getId();
            Console.this.pendingReqId = this.e.getReqId();
            Console.this.inPromptMode = true;
            Console.this.promptMessageLabel.setText(this.e.getText());
            Console.this.promptMessageLabel.setVisible(true);
            Console.super.fireContentsChanged();
            Console.this.updateDisplayLayout();
         }

      }
   }

   public class RestorePrompt implements Runnable {
      String text;

      RestorePrompt(String var2) {
         this.text = var2;
      }

      @Override
      public void run() {
         Console.this.inPromptMode = true;
         Console.this.promptMessageLabel.setText(this.text);
         Console.this.promptMessageLabel.setVisible(true);
         Console.super.fireContentsChanged();
         Console.this.updateDisplayLayout();
      }
   }
}
