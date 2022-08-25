package ddb.dsz.plugin.multitarget;

import ddb.detach.AbstractTabbable;
import ddb.detach.Tabbable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import java.util.Comparator;
import javax.swing.JComponent;
import org.apache.commons.collections.Predicate;

public abstract class SingleTargetImpl extends AbstractTabbable implements SingleTargetInterface {
   protected HostInfo target;
   protected CoreController core;
   protected JComponent display = null;

   public SingleTargetImpl(HostInfo var1, CoreController var2) {
      this.target = var1;
      this.core = var2;
      if (var1 != null) {
         this.setName(var1.getId());
      }

   }

   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }

   public void setDisplay(JComponent var1) {
      this.display = var1;
   }

   @Override
   public final JComponent getDisplay() {
      return this.display;
   }

   public HostInfo getTarget() {
      return this.target;
   }

   public int compareTo(Tabbable var1) {
      return var1 instanceof SingleTargetInterface ? this.compareTo((SingleTargetInterface)SingleTargetInterface.class.cast(var1)) : super.compareTo(var1);
   }

   public int compareTo(SingleTargetInterface var1) {
      return var1 == null ? -1 : HostInfo.COMPARE.compare(this.target, var1.getTarget());
   }

   @Override
   public final void fini() {
      this.fini2();
   }

   public void fini2() {
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }

   @Override
   public boolean isHideable() {
      return true;
   }

   @Override
   public boolean isClosable() {
      return false;
   }

   @Override
   public boolean isUnhideable() {
      return true;
   }

   @Override
   public boolean isDetachable() {
      return true;
   }

   @Override
   public JComponent getDefaultElement() {
      return null;
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      return false;
   }

   @Override
   public boolean caresAboutLocalEvents() {
      return this.target != null && this.target.isLocal();
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return false;
   }

   @Override
   public Comparator<CommandEvent> getComparator() {
      return null;
   }

   @Override
   public String getDetachedTitle() {
      String var1 = super.getDetachedTitle();
      String var2 = this.core.getTitle();
      if (this.target != null) {
         var1 = String.format("%s - %s", var1, this.target.getId());
      }

      if (var2 != null) {
         var1 = String.format("%s [%s]", var1, var2);
      }

      return var1;
   }

   protected Predicate getDataPredicate() {
      return new Predicate() {
         public boolean evaluate(Object var1) {
            if (!(var1 instanceof DataEvent)) {
               return false;
            } else {
               DataEvent var2 = (DataEvent)var1;
               if (!DataEvent.DataEventType.DATA.equals(var2.getDataType())) {
                  return false;
               } else {
                  TaskId var3 = var2.getTaskId();
                  if (var3 == null) {
                     return false;
                  } else {
                     Task var4 = SingleTargetImpl.this.core.getTaskById(var3);
                     if (var4 == null) {
                        return false;
                     } else {
                        return var4.getHost().sameHost(SingleTargetImpl.this.target);
                     }
                  }
               }
            }
         }
      };
   }
}
