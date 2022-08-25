package ds.util.datatransforms;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Node;

public class DisplayTaskClosure extends TransformedTaskClosure {
   protected Transformer transformer;
   protected Closure handleData;

   protected DisplayTaskClosure(CoreController var1, Task var2, Closure var3, Closure var4) {
      super(var2, var4);
      this.handleData = var3;
      this.transformer = this.createTransformer(String.format("%s/%s/Commands/Display/%s", var1.getResourceDirectory(), var2.getResourceDirectory(), var2.getDisplayTransform()), var1.getResourceDirectory(), var1.getResourcePackages());
   }

   protected DisplayTaskClosure(CoreController var1, String var2, String var3, Closure var4, Closure var5) {
      super(var2, var5);
      this.handleData = var4;
      this.transformer = this.createTransformer(String.format("%s/%s/Commands/Display/%s_display.xsl", var1.getResourceDirectory(), var3, var2), var1.getResourceDirectory(), var1.getResourcePackages());
   }

   protected void handleNode(Node var1, TaskDataAccess var2) {
      if (this.transformer != null && this.handleData != null) {
         StringWriter var3 = new StringWriter();

         try {
            this.transformer.transform(new DOMSource(var1), new StreamResult(var3));
         } catch (TransformerException var5) {
            this.handleError(var5);
            return;
         }

         String var4 = var3.toString();
         this.handleData.execute(var4);
      }
   }
}
