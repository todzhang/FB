package ds.core.commandevents;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventDemultiplexor;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.task.Task;
import ddb.util.UtilityConstants;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class CommandEventDemultiplexorImpl extends CommandEventDemultiplexor {
   private boolean caresAboutLocal;
   private boolean caresAboutRepeat;
   private ReadWriteLock LOCK = UtilityConstants.createReadWriteLock();
   final Map<String, Collection<CommandEventListener>> handlers = new HashMap();
   final Collection<CommandEventListener> allHandlers = new HashSet();
   final Collection<CommandEventListener> defaultHandlers = new HashSet();

   private CommandEventListener generate(CommandEventListener var1) {
      return var1;
   }

   @Override
   public void addCommandEventListenerDefault(CommandEventListener commandEventListener) {
      this.LOCK.writeLock().lock();

      try {
         this.defaultHandlers.add(commandEventListener);
      } finally {
         this.LOCK.writeLock().unlock();
      }

   }

   @Override
   public void addCommandEventListenerAll(CommandEventListener commandEventListener) {
      this.LOCK.writeLock().lock();

      try {
         this.allHandlers.add(this.generate(commandEventListener));
      } finally {
         this.LOCK.writeLock().unlock();
      }

   }

   @Override
   public void removeCommandEventListenerDefault(CommandEventListener commandEventListener) {
      this.LOCK.writeLock().lock();

      try {
         Iterator var2 = this.defaultHandlers.iterator();

         CommandEventListener var3;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (CommandEventListener)var2.next();
         } while(!var3.equals(commandEventListener));

         this.defaultHandlers.remove(var3);
      } finally {
         this.LOCK.writeLock().unlock();
      }

   }

   @Override
   public void removeCommandEventListenerAll(CommandEventListener commandEventListener) {
      this.LOCK.writeLock().lock();

      try {
         Iterator var2 = this.allHandlers.iterator();

         while(var2.hasNext()) {
            CommandEventListener var3 = (CommandEventListener)var2.next();
            if (var3.equals(commandEventListener)) {
               this.allHandlers.remove(var3);
               return;
            }
         }
      } finally {
         this.LOCK.writeLock().unlock();
      }

   }

   @Override
   public void addCommandEventListener(String var1, CommandEventListener commandEventListener) {
      if (var1 != null) {
         this.LOCK.writeLock().lock();

         try {
            Collection var3 = (Collection)this.handlers.get(var1);
            if (var3 == null) {
               var3 = new HashSet();
               this.handlers.put(var1, var3);
            }

            Iterator var4 = ((Collection)var3).iterator();

            CommandEventListener var5;
            do {
               if (!var4.hasNext()) {
                  ((Collection)var3).add(this.generate(commandEventListener));
                  return;
               }

               var5 = (CommandEventListener)var4.next();
            } while(!var5.equals(commandEventListener));
         } finally {
            this.LOCK.writeLock().unlock();
         }

      }
   }

   @Override
   public void removeCommandEventListener(String var1, CommandEventListener commandEventListener) {
      if (var1 != null) {
         this.LOCK.writeLock().lock();

         try {
            Collection var3 = (Collection)this.handlers.get(var1);
            if (var3 == null) {
               return;
            }

            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               CommandEventListener var5 = (CommandEventListener)var4.next();
               if (var5.equals(commandEventListener)) {
                  var3.remove(var5);
               }
            }
         } finally {
            this.LOCK.writeLock().unlock();
         }

      }
   }

   @Override
   public void removeCommandEventListener(CommandEventListener commandEventListener) {
      this.LOCK.writeLock().lock();

      try {
         this.removeCommandEventListener((String)null, commandEventListener);
         Iterator var2 = this.handlers.keySet().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            this.removeCommandEventListener(var3, commandEventListener);
         }
      } finally {
         this.LOCK.writeLock().unlock();
      }

   }

   private Collection<CommandEventListener> getHandlers(String var1) {
      HashSet var2 = new HashSet();
      this.LOCK.readLock().lock();

      try {
         var2.addAll(this.allHandlers);
         Collection var3 = (Collection)this.handlers.get(var1);
         if (var3 == null || var3.size() == 0) {
            var3 = this.defaultHandlers;
         }

         var2.addAll(var3);
      } finally {
         this.LOCK.readLock().unlock();
      }

      return var2;
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      Iterator var2 = this.getHandlers(commandEvent.getTargetAddress()).iterator();

      while(var2.hasNext()) {
         CommandEventListener var3 = (CommandEventListener)var2.next();

         try {
            var3.commandEventReceived(commandEvent);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      Iterator var3 = this.getHandlers(task.getTargetId()).iterator();

      while(var3.hasNext()) {
         CommandEventListener var4 = (CommandEventListener)var3.next();

         try {
            if (var4.handlesPromptsForTask(task, var2)) {
               return true;
            }
         } catch (Exception var6) {
         }
      }

      return false;
   }

   @Override
   public boolean caresAboutLocalEvents() {
      return this.caresAboutLocal;
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return this.caresAboutRepeat;
   }

   @Override
   public Comparator<CommandEvent> getComparator() {
      return null;
   }

   public void setCaresAboutLocal(boolean var1) {
      this.caresAboutLocal = var1;
   }

   public void setCaresAboutRepeat(boolean var1) {
      this.caresAboutRepeat = var1;
   }

   public void stop() {
   }

   @Override
   public void addCommandEventListener(CommandEventListener commandEventListener) {
      this.addCommandEventListenerAll(commandEventListener);
   }
}
