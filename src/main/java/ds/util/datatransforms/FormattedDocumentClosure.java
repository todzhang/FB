package ds.util.datatransforms;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Document;

public class FormattedDocumentClosure extends DocumentTaskClosure {
   protected Closure handleData;

   protected FormattedDocumentClosure(CoreController var1, Task var2, Closure var3, Closure var4) {
      super(var2, var4);
      this.handleData = var3;
   }

   protected FormattedDocumentClosure(CoreController var1, String var2, String var3, Closure var4, Closure var5) {
      super(var2, var5);
      this.handleData = var4;
   }

   protected void handleDocument(TaskDataAccess var1, Document var2) {
      if (var2 != null) {
         try {
            try {
               var2.normalize();
            } catch (Exception var5) {
               this.handleError(var5);
            }

            Transformer var3 = this.getTransformerFactory().newTransformer();
            var3.setOutputProperty("indent", "yes");
            var3.setOutputProperty("method", "xml");
            var3.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StringWriter var4 = new StringWriter();
            var3.transform(new DOMSource(var2), new StreamResult(var4));
            this.handleData.execute(var4.toString());
         } catch (Exception var6) {
            this.handleError(var6);
         }

      }
   }
}
