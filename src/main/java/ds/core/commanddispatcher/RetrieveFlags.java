package ds.core.commanddispatcher;

import ddb.dsz.core.task.MutableTask;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class RetrieveFlags implements Runnable {
   MutableTask task;
   String logDir;
   AbstractCommandDispatcher dispatcher;

   public RetrieveFlags(AbstractCommandDispatcher var1, MutableTask var2, String var3) {
      this.dispatcher = var1;
      this.task = var2;
      this.logDir = var3;
   }

   public void run() {
   }

   String getNodeText(XMLStreamReader var1) {
      StringBuilder var2 = new StringBuilder();
      if (!var1.isStartElement()) {
         return var2.toString();
      } else {
         try {
            var1.next();
         } catch (XMLStreamException var4) {
            return var2.toString();
         }

         while(var1.getEventType() == 4) {
            var2.append(var1.getText());

            try {
               var1.next();
            } catch (XMLStreamException var5) {
               break;
            }
         }

         return var2.toString();
      }
   }
}
