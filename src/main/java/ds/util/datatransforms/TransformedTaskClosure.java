package ds.util.datatransforms;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class TransformedTaskClosure extends DocumentTaskClosure implements Closure {
   public TransformedTaskClosure(Task var1, Closure var2) {
      super(var1, var2);
   }

   public TransformedTaskClosure(String var1, Closure var2) {
      super(var1, var2);
   }

   public void handleDocument(TaskDataAccess var1, Document var2) {
      if (var2 != null) {
         Node var3 = this.getFirstElementNamed(var2, "CommandTasking");
         if (var3 == null) {
            var3 = this.getFirstElementNamed(var2, "CommandData");
            if (var3 != null) {
               this.handleCommandData(var1);
            }
         } else {
            this.handleCommandTasking(var1);
         }

         if (var3 != null) {
            for(Node var4 = var3.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               if (var4.getNodeType() == 1) {
                  try {
                     this.handleNode(var4, var1);
                  } catch (Exception var6) {
                     this.handleError(var6);
                  }
               }
            }

         }
      }
   }

   private Node getFirstElementNamed(Document var1, String var2) {
      NodeList var3 = var1.getElementsByTagName(var2);
      return var3.getLength() > 0 ? var3.item(0) : null;
   }

   protected void handleCommandData(TaskDataAccess var1) {
   }

   protected void handleCommandTasking(TaskDataAccess var1) {
   }

   protected abstract void handleNode(Node var1, TaskDataAccess var2);
}
