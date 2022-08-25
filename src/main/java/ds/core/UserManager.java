package ds.core;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventAdapter;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ds.core.controller.MutableCoreController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.Closure;

public class UserManager {
   private static final Collection<String> INTERESTING_COMMANDS;
   private final MutableCoreController mutableCore;
   private final DataTransformer transformer = DataTransformer.newInstance("UserManager");
   private final List<UserManager.UserInstance> ValidInstances = new ArrayList();

   public UserManager(MutableCoreController mutableCore) {
      this.mutableCore = mutableCore;
      this.transformer.addClosure(ClosureFactory.newVariableClosure(mutableCore, "logonasuser", "ZBng", new Closure() {
         @Override
         public void execute(Object var1) {
            if (var1 != null && var1 instanceof DataEvent) {
               DataEvent var2 = (DataEvent)var1;
               if (var2.getDataType().equals(DataEventType.DATA)) {
                  UserManager.this.add(UserManager.this.new UserInstance(var2.getTaskId(), var2.getData().getString("logon::alias")));
               }
            }
         }
      }));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(mutableCore, "duplicatetoken", "ZBng", new Closure() {
         @Override
         public void execute(Object var1) {
            if (var1 != null && var1 instanceof DataEvent) {
               DataEvent var2 = (DataEvent)var1;
               if (var2.getDataType().equals(DataEventType.DATA)) {
                  UserManager.this.add(UserManager.this.new UserInstance(var2.getTaskId(), var2.getData().getString("token::alias")));
               }
            }
         }
      }));
      mutableCore.addCommandEventListener(new CommandEventAdapter() {
         @Override
         public boolean caresAboutLocalEvents() {
            return true;
         }

         @Override
         public boolean caresAboutRepeatedEvents() {
            return true;
         }

         @Override
         public void commandEventReceived(CommandEvent commandEvent) {
            Task var2 = UserManager.this.mutableCore.getTaskById(commandEvent.getId());
            if (var2 != null) {
               if (UserManager.INTERESTING_COMMANDS.contains(var2.getCommandName().toLowerCase())) {
                  UserManager.this.transformer.addTask(var2);
               }

            }
         }
      });
   }

   private void add(UserManager.UserInstance var1) {
      if (var1 != null && var1.alias != null && var1.task != null) {
         synchronized(this.ValidInstances) {
            Iterator var3 = this.ValidInstances.iterator();

            UserManager.UserInstance var4;
            do {
               if (!var3.hasNext()) {
                  this.ValidInstances.add(var1);
                  return;
               }

               var4 = (UserManager.UserInstance)var3.next();
            } while(!var4.task.equals(var1.task));

         }
      }
   }

   public List<String> usersByHost(HostInfo var1) {
      ArrayList var2 = new ArrayList();
      synchronized(this.ValidInstances) {
         Iterator var4 = this.ValidInstances.iterator();

         while(true) {
            if (!var4.hasNext()) {
               break;
            }

            UserManager.UserInstance var5 = (UserManager.UserInstance)var4.next();
            if (var5 != null) {
               Task var6 = this.mutableCore.getTaskById(var5.task);
               if (!var6.isAlive()) {
                  var4.remove();
               } else if (var6.getHost().sameHost(var1)) {
                  var2.add(var5.alias);
               }
            }
         }
      }

      Collections.sort(var2);
      return var2;
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("logonasuser");
      var0.add("duplicatetoken");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(var0);
   }

   private class UserInstance {
      public final TaskId task;
      public final String alias;

      public UserInstance(TaskId task, String alias) {
         this.task = task;
         this.alias = alias;
      }

      @Override
      public String toString() {
         return String.format("%d: %s", this.task.getId(), this.alias);
      }
   }
}
