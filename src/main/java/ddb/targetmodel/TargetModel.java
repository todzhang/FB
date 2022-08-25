package ddb.targetmodel;

import ddb.dsz.annotations.DszQueuableMethod;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.targetmodel.filemodel.FileSystemModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

public class TargetModel implements CommandEventListener {
   private final HostInfo host;
   private final CoreController core;
   private final List<Task> relevantTasks = new Vector();
   private final List<TargetDetail> details = new Vector();
   private FileSystemModel fileSystem;

   TargetModel(CoreController var1, HostInfo var2) {
      this.host = var2;
      this.core = var1;
      Iterator var3 = var1.getTaskList().iterator();

      while(var3.hasNext()) {
         Task var4 = (Task)var3.next();
         if (var4.getHost() != null && var4.getHost().sameHost(var2)) {
            this.relevantTasks.add(var4);
         }
      }

      Collections.sort(this.relevantTasks, Task.TaskComparator);
      var1.addCommandEventListener(this);
   }

   @Override
   @DszQueuableMethod
   public synchronized void commandEventReceived(CommandEvent commandEvent) {
      Task var2 = this.core.getTaskById(commandEvent.getId());
      if (var2 != null) {
         if (var2.getHost() != null) {
            if (var2.getHost().sameHost(this.host)) {
               int var3 = Collections.binarySearch(this.relevantTasks, var2, Task.TaskComparator);
               if (var3 < 0) {
                  ++var3;
                  var3 = -var3;
                  this.relevantTasks.add(var3, var2);
               }

               Iterator var4 = this.details.iterator();

               while(var4.hasNext()) {
                  TargetDetail var5 = (TargetDetail)var4.next();

                  try {
                     var5.addTask(var2);
                  } catch (Exception var7) {
                     this.core.logEvent(Level.SEVERE, "Exception while parsing command", var7);
                  }
               }

            }
         }
      }
   }

   @Override
   public Comparator<CommandEvent> getComparator() {
      return null;
   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      return false;
   }

   @Override
   public boolean caresAboutLocalEvents() {
      return true;
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return true;
   }

   public HostInfo getHost() {
      return this.host;
   }

   public synchronized FileSystemModel getFileSystemModel() {
      if (this.fileSystem == null) {
         try {
            this.fileSystem = new FileSystemModel(this.core, this.host.getId());
            this.details.add(this.fileSystem);
         } catch (Exception var5) {
            this.core.logEvent(Level.SEVERE, "Unable to instansiate FileSystemModel", var5);
         }

         Iterator var1 = this.relevantTasks.iterator();

         while(var1.hasNext()) {
            Task var2 = (Task)var1.next();

            try {
               this.fileSystem.addTask(var2);
            } catch (Exception var4) {
               this.core.logEvent(Level.SEVERE, "Unable to load task '" + var2.getFullCommandLine() + "'", var4);
            }
         }
      }

      return this.fileSystem;
   }
}
