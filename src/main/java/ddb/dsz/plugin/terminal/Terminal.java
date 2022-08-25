package ddb.dsz.plugin.terminal;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.GuiCommand;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.events.DisconnectEvent;
import ddb.dsz.core.connection.events.NewHostEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.controller.CoreController.OperationState;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.library.console.Console;
import ddb.dsz.library.console.ParsedCommandLine;
import ddb.dsz.library.console.Console.ConsoleState;
import ddb.dsz.library.console.ConsoleOutputPane.OutputLevel;
import ddb.dsz.library.console.builtins.BuiltinCommand;
import ddb.dsz.library.console.builtins.BuiltinHandlerAdapter;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.terminal.builtins.ListTargetHandler;
import ddb.dsz.plugin.terminal.builtins.NewTerm;
import ddb.dsz.plugin.terminal.builtins.NotifyHandler;
import ddb.dsz.plugin.terminal.builtins.QuitDanderspritz;
import ddb.dsz.plugin.terminal.builtins.ResetStatus;
import ddb.dsz.plugin.terminal.builtins.SetStatusBuiltin;
import ddb.dsz.plugin.terminal.builtins.SetTabTitle;
import ddb.dsz.plugin.terminal.builtins.SetWindowTitle;
import ddb.dsz.plugin.terminal.builtins.TargetHandler;
import ddb.imagemanager.ImageManager;
import ddb.util.FileManips;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import org.apache.commons.collections.Predicate;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/terminal.png")
@DszName("Terminal")
@DszDescription("A command console")
public class Terminal extends Console implements Plugin, IdCallback, FocusListener, InternalCommandHandler {
   private static final Object HISTORYLOCK = new Object();
   private static final String HISTORYFILE = "Term.History";
   private static OutputStreamWriter fw = null;
   private static Pattern DETACH_LOCATION_SIZE = Pattern.compile("(\\d+)x(\\d+)@(\\d+),(\\d+)");
   private static Pattern DETACH_SIZE = Pattern.compile("(\\d+)x(\\d+)");
   private static Pattern DETACH_LOCATION = Pattern.compile("(\\d+),(\\d+)");
   Set<String> terminalSuffix = new HashSet();
   private Terminal.CompleteBehavior onComplete;
   private boolean canRunCommands;
   private boolean stealFocus;
   private Queue<String> pendingCommands;
   private Runnable executePending;
   NewTerm newTerminalHandler;
   private String specificStatus;

   private static List<String> getHistoryItems(CoreController var0) {
      ArrayList var1 = new ArrayList();
      File var2 = new File(String.format("%s/%s", var0.getLogDirectory(), "Term.History"));
      if (var2.exists() && var2.isFile()) {
         synchronized(HISTORYLOCK) {
            try {
               LineNumberReader var4 = new LineNumberReader(FileManips.createFileReader(var2));

               for(String var5 = ""; var5 != null; var5 = var4.readLine()) {
                  if (var5.length() > 0) {
                     var1.add(var5);
                  }
               }

               var4.close();
            } catch (Exception var7) {
               var0.logEvent(Level.INFO, "Could not load history", var7);
            }

            return var1;
         }
      } else {
         return var1;
      }
   }

   private static void addItemToHistory(String var0, CoreController var1) {
      synchronized(HISTORYLOCK) {
         if (fw == null) {
            try {
               fw = FileManips.createFileWriter(new File(var1.getLogDirectory(), "Term.History"), true);
            } catch (Exception var6) {
               var1.logEvent(Level.WARNING, "Unable to open history file", var6);
               return;
            }
         }

         try {
            fw.write(String.format("%s\r\n", var0));
            fw.flush();
         } catch (Exception var5) {
            var1.logEvent(Level.WARNING, "Unable to save history item", var5);
         }

      }
   }

   protected int init3() {
      this.setGuiCommandPredicate(new Predicate() {
         public boolean evaluate(Object var1) {
            if (!(var1 instanceof GuiCommand)) {
               return false;
            } else {
               GuiCommand var2 = (GuiCommand)GuiCommand.class.cast(var1);
               if (var2.getId() != null) {
                  Task var3 = Terminal.this.core.getTaskById(var2.getId());
                  if (var3 == null) {
                     return false;
                  }

                  if (!Terminal.this.currentTasks.contains(var3)) {
                     return false;
                  }
               }

               Iterator var5 = Terminal.this.builtinCommands.keySet().iterator();

               String var4;
               do {
                  if (!var5.hasNext()) {
                     return false;
                  }

                  var4 = (String)var5.next();
               } while(!var2.getGuiCommand().toLowerCase().startsWith(String.format(".%s", var4)));

               return true;
            }
         }
      });
      List var1 = getHistoryItems(this.core);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.addHistoryItem(var3);
      }

      this.core.submit(this.executePending);
      if (this.getTarget() == null) {
         var2 = this.core.getHosts().iterator();

         while(var2.hasNext()) {
            HostInfo var4 = (HostInfo)var2.next();
            if (var4.isLocal() && this.onComplete.equals(Terminal.CompleteBehavior.Continue)) {
               this.setHost(var4);
               break;
            }
         }
      }

      this.status.setStatusIcon(ImageManager.getIcon(super.getLogo(), this.core.getLabelImageSize()));
      this.status.notifyObservers();
      if (this.stealFocus) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               Terminal.this.core.StealFocus(Terminal.this);
            }
         });
      }

      this.canRunCommands = true;
      return 0;
   }

   @Override
   protected boolean parseArgument3(String var1, String var2) {
      if (var1.equalsIgnoreCase("-cmd") && var2 != null) {
         this.pendingCommands.offer(var2);
         this.core.submit(this.executePending);
         return true;
      } else if (var1.equalsIgnoreCase(InternalCommandHandler.CLOSE_ON_COMPLETE)) {
         this.onComplete = Terminal.CompleteBehavior.Exit;
         return true;
      } else if (var1.equalsIgnoreCase(InternalCommandHandler.DISABLE_ON_COMPLETE)) {
         this.onComplete = Terminal.CompleteBehavior.Disable;
         return true;
      } else if (var1.equalsIgnoreCase(InternalCommandHandler.FOCUS_ON_START)) {
         this.stealFocus = true;
         return true;
      } else if (var1.equalsIgnoreCase("-special") && var2 != null) {
         this.terminalSuffix.add(var2);
         return true;
      } else if (var1.equalsIgnoreCase("-detach") && var2 != null) {
         Matcher var3 = DETACH_LOCATION_SIZE.matcher(var2);
         final Dimension var4 = new Dimension(450, 200);
         final Point var5 = new Point(0, 0);
         if (var3.matches()) {
            try {
               var4.width = Integer.parseInt(var3.group(1));
               var4.height = Integer.parseInt(var3.group(2));
               var5.x = Integer.parseInt(var3.group(3));
               var5.y = Integer.parseInt(var3.group(4));
            } catch (NumberFormatException var7) {
               return false;
            }
         }

         this.core.submit(new Runnable() {
            public void run() {
               Terminal.this.core.detachPlugin(Terminal.this, var4, var5);
            }
         });
         return true;
      } else if (var1.equalsIgnoreCase("-dst") && var2 != null) {
         this.setTarget(var2);
         return true;
      } else if (var1.equalsIgnoreCase("-name") && var2 != null) {
         this.setName(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getHistory() {
      return "Terminal/history.xml";
   }

   public Terminal() {
      this.onComplete = Terminal.CompleteBehavior.Continue;
      this.canRunCommands = false;
      this.stealFocus = false;
      this.pendingCommands = new LinkedList();
      this.executePending = new Runnable() {
         public void run() {
            synchronized(this) {
               label48: {
                  if (Terminal.this.canRunCommands && Terminal.this.currentTasks.size() <= 0) {
                     String var2 = (String)Terminal.this.pendingCommands.poll();
                     if (var2 != null) {
                        Terminal.this.setWaitingForSyncResponse(true);
                        Terminal.this.setStatus(var2);
                        Terminal.this.setWorkingState(ConsoleState.BUSY);

                        try {
                           Terminal.this.core.startCommand(String.format("%s%s", Terminal.this.getPrefix(), var2), Terminal.this, Terminal.this, Terminal.this.target);
                        } catch (DispatcherException var5) {
                           Terminal.this.setWaitingForSyncResponse(false);
                           Terminal.this.setCurrentSynchTask((Task)null);
                           Terminal.this.setWorkingState(ConsoleState.IDLE);
                           Terminal.this.core.logEvent(Level.INFO, var5.getMessage(), var5);
                           Terminal.this.appendOutputMessage("*\n* Command Failed To Start\n*\n\n", OutputLevel.ERROR);
                        }
                        break label48;
                     }

                     return;
                  }

                  Terminal.this.core.schedule(this, 100L, TimeUnit.MILLISECONDS);
                  return;
               }
            }

            if (Terminal.this.stealFocus) {
               Terminal.this.stealFocus = false;
               Terminal.this.core.StealFocus(Terminal.this);
            }

         }
      };
      this.newTerminalHandler = new NewTerm(this);
      this.specificStatus = null;
      super.setName("Terminal");
      super.setCareAboutLocalEvents(true);
      this.setupBuiltinCommands();
   }

   @Override
   public boolean startCommand(final String var1) {
      this.core.submit(new Runnable() {
         public void run() {
            if (OperationState.Inactive.equals(Terminal.this.core.getOperationState())) {
               Terminal.this.appendOutputMessage("*\n* Cannot start command\n* Operation has concluded\n*\n\n", OutputLevel.ERROR);
            } else {
               Terminal.addItemToHistory(var1, Terminal.this.core);
               Terminal.this.setWaitingForSyncResponse(true);
               Terminal.this.setStatus(var1);
               Terminal.this.setWorkingState(ConsoleState.BUSY);
               Terminal.this.outputPane.setPaused(false);

               try {
                  Terminal.this.core.startCommand(String.format("%s%s", Terminal.this.getPrefix(), var1), Terminal.this, Terminal.this, Terminal.this.target);
               } catch (DispatcherException var2) {
                  Terminal.this.setWaitingForSyncResponse(false);
                  Terminal.this.setCurrentSynchTask((Task)null);
                  Terminal.this.setWorkingState(ConsoleState.IDLE);
                  Terminal.this.core.logEvent(Level.INFO, var2.getMessage(), var2);
                  Terminal.this.appendOutputMessage("*\n* Command Failed To Start\n*\n\n", OutputLevel.ERROR);
               }

            }
         }
      });
      return true;
   }

   @Override
   protected String getConsoleLogName() {
      return "Terminal-screen";
   }

   @Override
   protected void setupCommandLineSpecificActionMaps() {
      super.setupCommandLineSpecificActionMaps();
      ActionMap var1 = this.commandLine.getActionMap();
      var1.put("complete", new TerminalCompletionAction(this, this.commandSet));
      var1.put("background command", new AbstractAction() {
         public void actionPerformed(ActionEvent var1) {
            if (Terminal.this.onComplete.equals(Terminal.CompleteBehavior.Continue)) {
               Terminal.this.backgroundCommand();
            }

         }
      });
      var1.put("foreground command", new AbstractAction() {
         public void actionPerformed(ActionEvent var1) {
            if (Terminal.this.onComplete.equals(Terminal.CompleteBehavior.Continue)) {
               Terminal.this.foregroundLast();
            }

         }
      });
   }

   protected void backgroundCommand() {
      if (this.isWaitingForSyncResponse()) {
         if (this.isInPromptMode()) {
            this.core.showPrompt((Task)this.currentTasks.peek(), super.pendingReqId, super.promptMessageLabel.getText());
            EventQueue.invokeLater(this.stopPromptMode);
         }

         try {
            synchronized(this.currentTasks) {
               Task var2 = (Task)this.currentTasks.peek();
               if (var2 != null) {
                  Vector var3 = new Vector();
                  var3.add("background");

                  try {
                     this.core.addPrefixesToTask(var2.getId(), var3);
                  } catch (DispatcherException var6) {
                     this.core.logEvent(Level.SEVERE, "Unable to background a command", var6);
                  }
               }

               String var9 = var2.getCommandName();
               this.appendOutputMessage(String.format("\n[Background send to command %d: %s]\n", var2.getId().getId(), var9), OutputLevel.BOLD);
            }
         } catch (EmptyStackException var8) {
         }
      }
   }

   @Override
   public JComponent getDefaultElement() {
      return this.commandLine;
   }

   @Override
   public boolean runInternalCommand(List<String> commands, TaskId taskId, InternalCommandCallback internalCommandCallback) {
      if (super.runInternalCommand(commands, taskId, internalCommandCallback)) {
         return true;
      } else if (commands.size() == 0) {
         this.core.logEvent(Level.INFO, "Command not handled because it is empty");
         return false;
      } else {
         Task var4 = this.core.getTaskById(taskId);
         if (!this.currentTasks.contains(var4)) {
            this.core.logEvent(Level.INFO, "Command not handled because it is not one of mine");
            return false;
         } else if (((String) commands.get(0)).equalsIgnoreCase(".newterm")) {
            this.newTerminal(commands.subList(1, commands.size()));
            return true;
         } else {
            if (((String) commands.get(0)).toLowerCase().startsWith(".newterm")) {
               String[] var5 = ((String) commands.get(0)).split(" +", 2);
               if (var5.length == 2) {
                  this.newTerminalHandler.executeBuiltinCommand(var5[0], var5[1]);
                  return true;
               }
            }

            Iterator var11 = this.builtinCommands.keySet().iterator();

            while(var11.hasNext()) {
               String var6 = (String)var11.next();

               try {
                  if (((String) commands.get(0)).toLowerCase().startsWith(String.format(".%s", var6.toLowerCase()))) {
                     String var7 = (String) commands.get(0);
                     String var8 = null;
                     String[] var9 = ((String) commands.get(0)).split(" +", 2);
                     if (var9.length == 2) {
                        var7 = var9[0];
                        var8 = var9[1];
                     }

                     this.core.logEvent(Level.INFO, "New Terminal Command:  " + commands);
                     ((BuiltinCommand)this.builtinCommands.get(var6)).getHandler().executeBuiltinCommand(var7, var8);
                     return true;
                  }
               } catch (Exception var10) {
                  var10.printStackTrace();
               }
            }

            this.core.logEvent(Level.INFO, "Command not handled because it is not recognized");
            return false;
         }
      }
   }

   @Override
   protected void handleGuiCommand(GuiCommand guiCommand) {
      super.commandGui(guiCommand);
      String var2 = guiCommand.getGuiCommand();
      Iterator var3 = this.builtinCommands.keySet().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();

         try {
            if (var2.toLowerCase().startsWith(String.format(".%s", var4.toLowerCase()))) {
               String var5 = var2;
               String var6 = null;
               String[] var7 = var2.split(" +", 2);
               if (var7.length == 2) {
                  var5 = var7[0];
                  var6 = var7[1];
               }

               this.core.logEvent(Level.INFO, "New Terminal Command:  " + guiCommand);
               this.core.sendGuiCommandResponse(guiCommand.getReqId(), ((BuiltinCommand)this.builtinCommands.get(var4)).getHandler().executeBuiltinCommand(var5, var6));
               return;
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }

      this.core.sendGuiCommandResponse(guiCommand.getReqId(), false);
      this.core.logEvent(Level.INFO, "Command not handled because it is not recognized");
   }

   @Override
   public boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback) {
      if (super.runInternalCommand(commands, internalCommandCallback)) {
         return true;
      } else {
         Iterator var3;
         String var4;
         try {
            var3 = this.terminalSuffix.iterator();

            while(var3.hasNext()) {
               var4 = (String)var3.next();
               if (((String) commands.get(0)).equalsIgnoreCase(String.format("terminal-%s", var4))) {
                  Vector var5 = new Vector();
                  Iterator var6 = commands.subList(1, commands.size()).iterator();

                  while(var6.hasNext()) {
                     String var7 = (String)var6.next();
                     if (var7.startsWith("-")) {
                        var5.add(var7);
                     } else {
                        var5.add(String.format("-cmd=%s", var7));
                     }
                  }

                  var5.add(InternalCommandHandler.FOCUS_ON_START);
                  return this.core.startNewPlugin(Class.forName("ddb.dsz.plugin.terminal.Terminal"), "Term", var5, false, true);
               }
            }
         } catch (Throwable var8) {
            this.core.logEvent(Level.SEVERE, "Unable to create new terminal", var8);
         }

         if (super.currentTasks.size() > 0) {
            return false;
         } else if (commands.size() < 2) {
            return false;
         } else if (!this.onComplete.equals(Terminal.CompleteBehavior.Continue)) {
            return false;
         } else {
            if (this.target != null && !this.target.isLocal()) {
               var3 = commands.iterator();

               while(var3.hasNext()) {
                  var4 = (String)var3.next();
                  if (var4.equalsIgnoreCase(InternalCommandHandler.LOCAL_ONLY)) {
                     return false;
                  }
               }
            }

            if (((String) commands.get(0)).equalsIgnoreCase("terminal")) {
               this.execute(commands.subList(1, commands.size()));
               return true;
            } else if (((String) commands.get(0)).equalsIgnoreCase("terminal-focus")) {
               this.execute(commands.subList(1, commands.size()));
               this.core.StealFocus(this);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void execute(List<String> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.startsWith("/")) {
            this.pendingCommands.offer(var3);
            this.core.submit(this.executePending);
         }
      }

   }

   @Override
   protected void evalutateWaitingForSyncTask() {
      super.evalutateWaitingForSyncTask();
      if (super.currentTasks.size() == 0) {
         this.setStatusTextClear();
         if (this.pendingCommands.size() == 0) {
            if (this.onComplete.equals(Terminal.CompleteBehavior.Disable)) {
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     Terminal.this.commandLine.setEnabled(false);
                     Terminal.this.southPanel.setVisible(false);
                  }
               });
            }

            if (this.onComplete.equals(Terminal.CompleteBehavior.Exit)) {
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     Terminal.this.core.closePlugin(Terminal.this);
                  }
               });
            }
         }

         this.core.submit(this.executePending);
      }

   }

   protected void setStatusTextClear() {
      if (this.target != null && !this.target.isConnected()) {
         SimpleDateFormat var1 = new SimpleDateFormat("HH:mm:ss");
         this.setStatus(String.format("<html>Disconnected from <b>%s</b> at <b>%s</b>", this.target.getId(), var1.format(this.target.getModifiedTime().getTime())));
      } else {
         this.setStatus("");
      }

   }

   @Override
   protected void setupBuiltinCommands() {
      super.setupBuiltinCommands();
      this.addBuiltinCommand("dst", new TargetHandler(this), "Usage: dst [CP ID]\n");
      this.addBuiltinCommand("listdestinations", new ListTargetHandler(this), "Usage: listdestinations\n");
      this.addBuiltinCommand("notify", new NotifyHandler(this), "Usage: notify [id]\n");
      this.addBuiltinCommand("newterm", this.newTerminalHandler, "Usage:  newterm [parameters]\n\t[-cmd=x]\n\t\tStart the window with the given command.  Use \"\" to delimit the command.\n\t[-focus]\n\t\tFocus on the window immediately\n\t[-close]\n\t\tClose the window when the command is complete\n\t[-disable]\n\t\tDisable the window so that no further commands can be run\n\t[-name=x]\n\t\tName the window\n\t[-dst=x]\n\t\tSpecify the default destination of the window\n\t[-detach[=WxH@X,Y]]\n\t\tSpecify a window of WxH pixel at location X,Y.\n\t\tSize and location are optional\n\t[-location=X,Y]\n\t\tSpecify that the window should be at X,Y\n\t[-size=WxH]\n\t\tSpecify that the window should be WxH pixels in size\n");
      this.addBuiltinCommand("setStatus", new SetStatusBuiltin(this), "Usage: setstatus <status>\n");
      this.addBuiltinCommand("resetStatus", new ResetStatus(this), "Usage: resetstatus\n");
      this.addBuiltinCommand("settabtitle", new SetTabTitle(this), "Usage: settabtitle <title>\n");
      this.addBuiltinCommand("setwindowtitle", new SetWindowTitle(this), "Usage: setwindowtitle <title>\n");
      this.addBuiltinCommand("quit", new QuitDanderspritz(this), "Usage:  quit\n");
   }

   private void addBuiltinCommand(String var1, BuiltinHandlerAdapter var2, String var3) {
      BuiltinCommand var4 = new BuiltinCommand(var1);
      var4.setHandler(var2);
      var4.setHelpStatement(var3);
      this.builtinCommands.put(var4.getName(), var4);
   }

   public boolean setTarget(String var1) {
      if (var1 == null) {
         var1 = "127.0.0.1";
      }

      HostInfo var2 = this.core.getHostById(var1);
      if (var2 == null) {
         this.appendOutputMessage(String.format("Unable to find host with id '%s'.\n", var1), OutputLevel.WARNING);
         return false;
      } else {
         this.setHost(var2);
         this.setStatusTextClear();
         return true;
      }
   }

   public boolean notify(String var1) {
      boolean var2 = false;

      int var6;
      try {
         var6 = Integer.parseInt(var1);
      } catch (NumberFormatException var5) {
         this.appendOutputMessage(String.format("Unable to command id '%s'.\n", var1), OutputLevel.WARNING);
         return false;
      }

      Task var3 = this.core.getRunningTaskById(TaskId.GenerateTaskId(var6, this.core.getOperation()));
      if (var3 == null) {
         this.appendOutputMessage(String.format("Unable to find running command id '%d'.\n", var6), OutputLevel.WARNING);
         return false;
      } else {
         String var4 = var3.getCommandName();
         if (this.core.internalCommand((InternalCommandCallback)null, new String[]{"notify", var1})) {
            this.appendOutputMessage(String.format("\n[Notification set on command %d]\n", var6, var4), OutputLevel.BOLD);
            return true;
         } else {
            this.appendOutputMessage(String.format("Unable to set notify on command id '%d'.\n", var6), OutputLevel.WARNING);
            return false;
         }
      }
   }

   public boolean resetStatus() {
      this.specificStatus = null;
      this.setStatus("");
      return false;
   }

   public boolean lockStatus(String var1) {
      this.specificStatus = var1;
      this.setStatus(var1);
      return false;
   }

   @Override
   protected void setStatus(String status) {
      String var2 = this.specificStatus;
      if (var2 != null) {
         status = var2;
      }

      super.setStatus(status);
   }

   public boolean newTerminal(List<String> var1) {
      String var2 = "Term";
      Vector var3 = new Vector();
      Vector var4 = new Vector();
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      Dimension var10 = new Dimension(640, 480);
      Point var11 = new Point(0, 0);
      HostInfo var12 = this.target;
      String var13 = null;
      Iterator var14 = var1.iterator();

      while(true) {
         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           while(true) {
                              while(true) {
                                 while(true) {
                                    String var15;
                                    String var17;
                                    String var18;
                                    do {
                                       if (!var14.hasNext()) {
                                          var3.add(String.format("-dst=%s", var12.getId()));
                                          if (var5) {
                                             var3.add(String.format("-name=%s", var2));
                                          }

                                          if (var7) {
                                             var3.add(String.format("-detach=%dx%d@%d,%d", var10.width, var10.height, var11.x, var11.y));
                                          }

                                          if (var6) {
                                             var3.add(InternalCommandHandler.FOCUS_ON_START);
                                          }

                                          if (var8) {
                                             var3.add(InternalCommandHandler.CLOSE_ON_COMPLETE);
                                          }

                                          if (var9) {
                                             var3.add(InternalCommandHandler.DISABLE_ON_COMPLETE);
                                          }

                                          if (var13 != null) {
                                             var3.add(String.format("-theme=%s", var13));
                                          }

                                          var14 = this.terminalSuffix.iterator();

                                          while(var14.hasNext()) {
                                             var15 = (String)var14.next();
                                             var3.add(String.format("-special=%s", var15));
                                          }

                                          var14 = var4.iterator();

                                          while(var14.hasNext()) {
                                             var15 = (String)var14.next();
                                             var3.add(String.format("-cmd=%s", var15));
                                          }

                                          return this.core.startNewPlugin(Terminal.class, var2, var3, false, true);
                                       }

                                       var15 = (String)var14.next();
                                       String[] var16 = var15.split("=", 2);
                                       var17 = null;
                                       var18 = null;
                                       if (var16.length > 0) {
                                          var17 = var16[0];
                                       }

                                       if (var16.length > 1) {
                                          var18 = var16[1];
                                       }
                                    } while(var17 == null);

                                    if (!var17.equalsIgnoreCase("-cmd") || var18 == null) {
                                       if (!var17.equalsIgnoreCase("-focus")) {
                                          if (!var17.equalsIgnoreCase("-close")) {
                                             if (!var17.equalsIgnoreCase("-disable")) {
                                                if (!var17.equalsIgnoreCase("-name") || var18 == null) {
                                                   if (!var17.equalsIgnoreCase("-dst") || var18 == null) {
                                                      Matcher var19;
                                                      if (!var17.equalsIgnoreCase("-detach")) {
                                                         if (!var17.equalsIgnoreCase("-location") || var18 == null) {
                                                            if (!var17.equalsIgnoreCase("-size") || var18 == null) {
                                                               if (!var17.equalsIgnoreCase("-theme") || var18 == null) {
                                                                  this.appendOutputMessage(String.format("Argument not recognized:  %s\n", var15), OutputLevel.ERROR);
                                                                  return false;
                                                               }

                                                               var13 = var18;
                                                            } else {
                                                               var7 = true;
                                                               var19 = DETACH_SIZE.matcher(var18);
                                                               if (var19.matches()) {
                                                                  try {
                                                                     var10.width = Integer.parseInt(var19.group(1));
                                                                     var10.height = Integer.parseInt(var19.group(2));
                                                                  } catch (NumberFormatException var21) {
                                                                  }
                                                               }
                                                            }
                                                         } else {
                                                            var7 = true;
                                                            var19 = DETACH_LOCATION.matcher(var18);
                                                            if (var19.matches()) {
                                                               try {
                                                                  var11.x = Integer.parseInt(var19.group(1));
                                                                  var11.y = Integer.parseInt(var19.group(2));
                                                               } catch (NumberFormatException var22) {
                                                               }
                                                            }
                                                         }
                                                      } else {
                                                         var7 = true;
                                                         if (var18 != null) {
                                                            var19 = DETACH_LOCATION_SIZE.matcher(var18);
                                                            if (var19.matches()) {
                                                               try {
                                                                  var10.width = Integer.parseInt(var19.group(1));
                                                                  var10.height = Integer.parseInt(var19.group(2));
                                                                  var11.x = Integer.parseInt(var19.group(3));
                                                                  var11.y = Integer.parseInt(var19.group(4));
                                                               } catch (NumberFormatException var23) {
                                                               }
                                                            }
                                                         }
                                                      }
                                                   } else {
                                                      var12 = this.core.getHostById(var18);
                                                   }
                                                } else {
                                                   var5 = true;
                                                   var2 = var18;
                                                }
                                             } else {
                                                var9 = true;
                                             }
                                          } else {
                                             var8 = true;
                                          }
                                       } else {
                                          var6 = true;
                                       }
                                    } else {
                                       var4.add(var18);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void quitDanderspritz() {
      this.core.requestShutdown();
   }

   public void listTargets() {
      List var1 = this.core.getHosts();
      if (var1.size() == 0) {
         this.appendOutputMessage("No hosts known.\r\n", OutputLevel.WARNING);
      }

      this.appendOutputMessage("    Host ID         Arch        OS      Version    Implant\n", OutputLevel.DEFAULT);
      this.appendOutputMessage("---------------  ---------- ---------- ---------- ----------\n", OutputLevel.DEFAULT);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         HostInfo var3 = (HostInfo)var2.next();
         if (var3.isConnected()) {
            this.appendOutputMessage(String.format("%15s  %10s %10s %10s %10s\n", var3.getId(), var3.getArch(), var3.getPlatform(), var3.getVersion(), var3.getImplantType()), OutputLevel.DEFAULT);
         }
      }

      this.appendOutputMessage("---------------  ---------- ---------- ---------- ----------\n", OutputLevel.DEFAULT);
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
      super.connectionChanged(connectionChangeEvent);
      if (connectionChangeEvent instanceof NewHostEvent) {
         this.canRunCommands = true;
         if (!this.onComplete.equals(Terminal.CompleteBehavior.Continue)) {
            return;
         }

         NewHostEvent var2 = (NewHostEvent)NewHostEvent.class.cast(connectionChangeEvent);
         if (this.target == null) {
            this.setHost(var2.getHost());
            return;
         }

         TaskId var3 = var2.getHost().getTask();
         synchronized(this.currentTasks) {
            if (this.currentTasks.empty()) {
               this.setStatusTextClear();
               return;
            }

            Iterator var5 = this.currentTasks.iterator();

            while(var5.hasNext()) {
               Task var6 = (Task)var5.next();
               if (var6.getId().equals(var3)) {
                  this.setHost(var2.getHost());
                  return;
               }
            }
         }
      } else if (connectionChangeEvent instanceof DisconnectEvent) {
         DisconnectEvent var9 = (DisconnectEvent) connectionChangeEvent;
         if (this.target == null) {
            return;
         }

         if (!this.target.sameHost(var9.getHost())) {
            return;
         }

         this.setStatusTextClear();
      }

   }

   public void requestHelpStatement(ParsedCommandLine var1, String var2) {
      HostInfo var3 = this.target;
      if (var2 != null) {
         var3 = this.core.getHostById(var2);
      }

      super.requestHelpStatement(var1, var3);
   }

   @Override
   protected void setHost(HostInfo hostInfo) {
      super.setHost(hostInfo);
      this.setStatusTextClear();
      if (hostInfo != null && hostInfo.getHostname() != null && this.workbench != null) {
      }

      if (hostInfo != null && hostInfo.getHostname() != null) {
         super.setName(hostInfo.getHostname());
      }

   }

   public void setName(String name) {
      super.setName(name);
   }

   public boolean setWindowTitle(String var1) {
      this.core.setTitle(var1);
      return true;
   }

   public String getTargetForPrefix(String var1) {
      List var2 = this.core.getHosts();
      var1 = var1.toLowerCase();
      int var3 = 0;

      while(true) {
         while(var3 < var2.size()) {
            if (((HostInfo)var2.get(var3)).isConnected() && ((HostInfo)var2.get(var3)).getId().toLowerCase().startsWith(var1)) {
               ++var3;
            } else {
               var2.remove(var3);
            }
         }

         if (var2.size() == 1) {
            return ((HostInfo)var2.get(0)).getId();
         }

         if (var2.size() == 0) {
            this.appendOutputMessage("No hosts matched.\r\n", OutputLevel.WARNING);
            this.appendOutputMessage("List of all hosts:\r\n", OutputLevel.DEFAULT);
            var2 = this.core.getHosts();
         } else {
            this.appendOutputMessage("Possible hosts:\r\n", OutputLevel.DEFAULT);
         }

         this.appendOutputMessage("    Host ID         Arch        OS      Version    Implant\n", OutputLevel.DEFAULT);
         this.appendOutputMessage("---------------  ---------- ---------- ---------- ----------\n", OutputLevel.DEFAULT);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            HostInfo var5 = (HostInfo)var4.next();
            if (var5.isConnected()) {
               this.appendOutputMessage(String.format("%15s", var5.getId()), OutputLevel.NOTICE);
               this.appendOutputMessage(String.format("  %10s %10s %10s %10s\n", var5.getArch(), var5.getPlatform(), var5.getVersion(), var5.getImplantType()), OutputLevel.DEFAULT);
            }
         }

         this.appendOutputMessage("---------------  ---------- ---------- ---------- ----------\n", OutputLevel.DEFAULT);
         return null;
      }
   }

   public static void main(String[] var0) {
      try {
         Class var1 = Class.forName("ds.plugin.live.DSClientApp");
         Method var2 = var1.getMethod("main", String[].class);
         var2.invoke((Object)null, var0);
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

   }

   static enum CompleteBehavior {
      Continue,
      Disable,
      Exit;
   }
}
