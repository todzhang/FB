package ddb.dsz.plugin.multitarget;

import ddb.detach.TabbableStatus;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventDemultiplexor;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public abstract class MultipleTargetPlugin extends NoHostAbstractPlugin {
   protected boolean notifyOnNewHost = false;
   protected final CommandEventDemultiplexor demulti = CommandEventDemultiplexor.newInstance();
   protected final Collection<SingleTargetInterface> children = new HashSet();
   protected final MultipleTargetWorkbench tabWorkbench = this.generateWorkbench();

   protected MultipleTargetPlugin() {
      this.tabWorkbench.addPropertyChangeListener("WORKBENCH_CONTENTS_CHANGED", this.contentsChanged);
      this.tabWorkbench.addPropertyChangeListener("WORKBENCH_CONTENTS_CHANGED_REQUEST_FOCUS", this.contentsChangedRequestFocus);
   }

   @Override
   protected final int init2() {
      if (this.demulti == null) {
         return -1;
      } else {
         this.demulti.addCommandEventListenerDefault(new CommandEventListener() {
            final Predicate unique = PredicateUtils.uniquePredicate();

            @Override
            public void commandEventReceived(final CommandEvent commandEvent) {
               if (commandEvent.getTargetAddress() != null) {
                  switch(commandEvent.getType()) {
                  case STARTED:
                  case ENDED:
                  case OUTPUT:
                  case INFO:
                     final HostInfo var2 = MultipleTargetPlugin.this.core.getHostById(commandEvent.getTargetAddress());
                     if (var2 == null) {
                        return;
                     } else {
                        final MultipleTargetPlugin.LocalHostState var3 = MultipleTargetPlugin.this.getLocalHostState();
                        if (var3 == MultipleTargetPlugin.LocalHostState.IGNORE && var2.isLocal()) {
                           return;
                        } else {
                           if (!this.unique.evaluate(var2)) {
                              return;
                           }

                           try {
                              EventQueue.invokeAndWait(new Runnable() {
                                 public void run() {
                                    SingleTargetInterface var1x = MultipleTargetPlugin.this.newHost(var2);
                                    if (var1x != null) {
                                       MultipleTargetPlugin.this.children.add(var1x);
                                       MultipleTargetPlugin.this.demulti.addCommandEventListener(var2.getId(), var1x);
                                       var1x.commandEventReceived(commandEvent);
                                       MultipleTargetPlugin.this.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var1x});
                                       if (var3 == MultipleTargetPlugin.LocalHostState.HIDE && var2.isLocal()) {
                                          MultipleTargetPlugin.this.tabWorkbench.enqueAction(WorkbenchAction.HIDETAB, new Object[]{var1x});
                                       }

                                       if (!var2.isLocal() && MultipleTargetPlugin.this.notifyOnNewHost) {
                                          MultipleTargetPlugin.this.fireContentsChanged();
                                       }

                                    }
                                 }
                              });
                           } catch (Exception var5) {
                              var5.printStackTrace();
                           }

                           return;
                        }
                     }
                  default:
                  }
               }
            }

            @Override
            public boolean handlesPromptsForTask(Task task, int var2) {
               return false;
            }

            @Override
            public boolean caresAboutLocalEvents() {
               return false;
            }

            @Override
            public boolean caresAboutRepeatedEvents() {
               return false;
            }

            @Override
            public Comparator<CommandEvent> getComparator() {
               return null;
            }
         });
         return this.init3();
      }
   }

   protected abstract int init3();

   @Override
   public final void commandEventReceived(CommandEvent commandEvent) {
      this.demulti.commandEventReceived(commandEvent);
   }

   @Override
   public void close() {
      this.demulti.stop();
      Iterator var1 = this.children.iterator();

      while(var1.hasNext()) {
         SingleTargetInterface var2 = (SingleTargetInterface)var1.next();
         var2.close();
         var2.fini();
         this.workbench.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{var2});
      }

      super.close();
   }

   @Override
   protected final void fini2() {
      this.fini3();
   }

   @Override
   protected final boolean parseArgument2(String var1, String var2) {
      return this.parseArgument3(var1, var2);
   }

   protected boolean parseArgument3(String var1, String var2) {
      return false;
   }

   protected void fini3() {
   }

   protected final SingleTargetInterface newHostWrapper(HostInfo var1) {
      SingleTargetInterface var2 = this.newHost(var1);
      return var2;
   }

   protected abstract SingleTargetInterface newHost(HostInfo var1);

   protected void destroy(SingleTargetInterface var1) {
   }

   protected MultipleTargetWorkbench generateWorkbench() {
      return new MultipleTargetWorkbench(this);
   }

   protected abstract MultipleTargetPlugin.LocalHostState getLocalHostState();

   public abstract String newItemName();

   public void newItem(HostInfo var1) {
      SingleTargetInterface var2 = this.newHost(var1);
      this.children.add(var2);
      this.demulti.addCommandEventListener(var1.getId(), var2);
      this.tabWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var2});
   }

   CoreController getCoreController() {
      return this.core;
   }

   @Override
   public TabbableStatus getStatus() {
      return this.tabWorkbench.getStatus();
   }

   public static enum LocalHostState {
      IGNORE,
      HIDE,
      SHOW;
   }
}
