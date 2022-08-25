package ddb.dsz.plugin.notify;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.GuiCommand;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.AbstractPlugin;
import ddb.imagemanager.ImageManager;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.UniquePredicate;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/knotify.png")
@DszName("Notify")
@DszDescription("A table of commands started with the notify prefix")
@DszUserStartable(false)
public class NotifyWindow extends AbstractPlugin implements Observer {
   private static String[] GUI_COMMANDS = new String[]{".notify"};
   private JTable notifyTable = new JTable();
   Predicate isNewNotifyCommand = AllPredicate.getInstance(new Predicate[]{new Predicate() {
      @Override
      public boolean evaluate(Object o) {
         if (!(o instanceof Task)) {
            return false;
         } else {
            Task var2 = (Task)Task.class.cast(o);
            return var2.getGuiFlagValue("notify") != null;
         }
      }
   }, UniquePredicate.getInstance()});
   Set<Task> watchedCommands = new HashSet();

   public NotifyWindow() {
      super.setDisplay(this.notifyTable);
      super.setName("Notify");
      super.prefferedSize = new Dimension(400, 400);
      super.setCareAboutLocalEvents(true);
   }

   @Override
   protected int init2() {
      this.setGuiCommandPredicate(new Predicate() {
         @Override
         public boolean evaluate(Object var1) {
            if (!(var1 instanceof GuiCommand)) {
               return false;
            } else {
               GuiCommand var2 = (GuiCommand)GuiCommand.class.cast(var1);
               if (var2.getId() != null) {
                  Task var3 = NotifyWindow.this.core.getTaskById(var2.getId());
                  if (var3 == null) {
                     return false;
                  }
               }

               String[] var7 = NotifyWindow.GUI_COMMANDS;
               int var4 = var7.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  String var6 = var7[var5];
                  if (var2.getGuiCommand().toLowerCase().startsWith(var6)) {
                     return true;
                  }
               }

               return false;
            }
         }
      });
      return 0;
   }

   @Override
   protected void handleGuiCommand(GuiCommand guiCommand) {
      super.commandGui(guiCommand);
      String var2 = guiCommand.getGuiCommand();
      if (!var2.toLowerCase().startsWith(".notify")) {
         this.core.sendGuiCommandResponse(guiCommand.getReqId(), false);
      } else {
         String var3 = null;
         String[] var4 = var2.split(" +", 2);
         if (var4.length == 2) {
            var3 = var4[1];
         }

         this.core.logEvent(Level.INFO, "Notify Command:  " + guiCommand);
         boolean var5 = false;

         try {
            if (var3 == null) {
               return;
            }

            try {
               int var6 = Integer.parseInt(var3);
               TaskId var7 = TaskId.GenerateTaskId(var6, this.core.getOperation());
               if (var7 != null) {
                  Task var8 = this.core.getRunningTaskById(var7);
                  if (var8 == null) {
                     return;
                  }

                  this.watchedCommands.add(var8);
                  var5 = true;
                  return;
               }
            } catch (NumberFormatException var12) {
               this.core.logEvent(Level.WARNING, "Gui Command had invalid number: " + var3);
               return;
            }
         } finally {
            this.core.sendGuiCommandResponse(guiCommand.getReqId(), var5);
         }

      }
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      super.commandEnded(var1);
      Task var2 = this.core.getTaskById(var1.getId());
      if (this.isNewNotifyCommand.evaluate(var2) || this.watchedCommands.contains(var2)) {
         (new NotifyDisplayWindow(var2)).setVisible(true);
         this.watchedCommands.remove(var2);
      }

   }

   @Override
   public boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback) {
      if (commands.size() == 2 && ((String) commands.get(0)).equalsIgnoreCase("notify")) {
         try {
            int var3 = Integer.parseInt((String) commands.get(1));
            Task var4 = this.core.getRunningTaskById(TaskId.GenerateTaskId(var3, this.core.getOperation()));
            if (var4 != null) {
               this.watchedCommands.add(var4);
               return true;
            }
         } catch (NumberFormatException var5) {
         }
      }

      return super.runInternalCommand(commands, internalCommandCallback);
   }

   public void update(Observable var1, Object var2) {
      Task var3 = (Task)var1;
      String var4 = null;
      switch(var3.getState()) {
      case FAILED:
         var4 = "has ended in failure";
         break;
      case KILLED:
         var4 = "was killed";
         break;
      case SUCCEEDED:
         var4 = "succeeded";
         break;
      default:
         return;
      }

      JOptionPane.showMessageDialog(access$201(this), String.format("<html><b>%s</b> %s</html>", var3.getTypedCommand(), var4), "Command Notification", 1, ImageManager.getIcon(access$301(this), ImageManager.BANNER_SIZE));
   }

   // $FF: synthetic method
   static JComponent access$201(NotifyWindow var0) {
      return var0.parentDisplay;
   }

   // $FF: synthetic method
   static String access$301(NotifyWindow var0) {
      return var0.getLogo();
   }
}
