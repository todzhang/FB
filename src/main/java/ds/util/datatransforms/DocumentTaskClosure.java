package ds.util.datatransforms;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ds.core.impl.task.TaskStateAccess;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.w3c.dom.Document;

public abstract class DocumentTaskClosure extends TaskClosure implements Closure {
   protected Predicate handlesData = PredicateUtils.falsePredicate();
   private Task task;

   public DocumentTaskClosure(Task var1, Closure var2) {
      super(var2);
      this.task = var1;
      this.handlesData = PredicateUtils.equalPredicate(var1);
   }

   protected Task getTask() {
      return this.task;
   }

   public DocumentTaskClosure(final String var1, Closure var2) {
      super(var2);
      if (var1 == null) {
         this.handlesData = PredicateUtils.truePredicate();
      } else {
         this.handlesData = new Predicate() {
            public boolean evaluate(Object var1x) {
               if (!(var1x instanceof Task)) {
                  return false;
               } else {
                  Task var2 = (Task)var1x;
                  return var2.getCommandName().equalsIgnoreCase(var1);
               }
            }
         };
      }

   }

   public void execute(Object var1) {
      if (var1 != null && var1 instanceof TaskDataAccess) {
         if (!(var1 instanceof TaskStateAccess)) {
            TaskDataAccess var2 = (TaskDataAccess)var1;
            if (this.handlesData.evaluate(var2.getTask())) {
               if (var2.getLocation() != null) {
                  this.executeDirectly(var2);
               }
            }
         }
      }
   }

   private void executeDirectly(TaskDataAccess var1) {
      try {
         this.handleDocument(var1, AbstractDataTransformer.parseDocument(var1));
      } catch (IncompleteDataException var3) {
         throw var3;
      } catch (Exception var4) {
         this.handleError(var4);
      }

   }

   protected abstract void handleDocument(TaskDataAccess var1, Document var2);
}
