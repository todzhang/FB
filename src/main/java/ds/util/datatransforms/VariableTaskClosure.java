package ds.util.datatransforms;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskId;
import ds.util.datatransforms.transformers.ObjectValueImpl;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VariableTaskClosure extends CommandMetaDataClosure {
   protected Collection<TaskId> knownChildren = new HashSet();
   protected Reference<Transformer> transformerRef = new WeakReference((Object)null);
   protected final String taskName;
   protected final String resourceDirectory;

   protected VariableTaskClosure(CoreController var1, Task var2, Closure var3, Closure var4) {
      super(var1, var2, var3, var4);
      this.taskName = var2.getCommandName().toLowerCase();
      this.resourceDirectory = var2.getResourceDirectory();
   }

   protected VariableTaskClosure(CoreController var1, String var2, String var3, Closure var4, Closure var5) {
      super(var1, var2, var3, var4, var5);
      this.taskName = var2;
      this.resourceDirectory = var3;
   }

   private synchronized Transformer getTransformer() {
      Transformer var1 = (Transformer)this.transformerRef.get();
      if (var1 == null) {
         String var2 = String.format("%s_storage.xsl", this.taskName.toLowerCase());
         if (super.getTask() != null) {
            var2 = super.getTask().getStorageTransform();
         }

         String var3 = String.format("%s/%s/Commands/Storage/%s", this.core.getResourceDirectory(), this.resourceDirectory, var2);
         var1 = this.createTransformer(var3, this.core.getResourceDirectory(), this.core.getResourcePackages());
         if (var1 == null) {
            this.core.logEvent(Level.INFO, String.format("Unable to create transformer with input '%s'", var3));
         }

         this.transformerRef = new SoftReference(var1);
      }

      return var1;
   }

   protected void handleNode(Node var1, TaskDataAccess var2) {
      Transformer var3 = this.getTransformer();
      if (var3 != null && this.handleData != null) {
         final DataTransformsDataEvent var4 = new DataTransformsDataEvent(DataEventType.DATA, (DataTransformer)null, var2.getTask().getId());
         final String var5 = getLpTimestamp(var1);
         final String var6 = getDataTimestamp(var1);
         var4.setTimestamp(var5);
         ObjectValueImpl.AddData(var4.getData(), DataType.INTEGER, "Ordinal", var2.getOrdinal());
         ObjectValueImpl.AddData(var4.getData(), DataType.INTEGER, "Size", var2.getTask().getNextOrdinal());

         try {
            var3.transform(new DOMSource(var1), new SAXResult(new DefaultHandler() {
               Stack<ObjectValueImpl> objects = new Stack();
               StringBuilder sb = new StringBuilder();
               String dataName;

               public void startElement(String var1, String var2, String var3, Attributes var4x) throws SAXException {
                  this.sb.setLength(0);
                  String var5x = var4x.getValue("name");
                  if (var5x == null) {
                  }

                  if (DataType.OBJECT.isElement(var3)) {
                     ObjectValueImpl var6x = new ObjectValueImpl();
                     if (this.objects.isEmpty()) {
                        var4.addObject(var5x, var6x);
                     } else {
                        ((ObjectValueImpl)this.objects.peek()).addValue(DataType.OBJECT, var5x, var6x);
                     }

                     this.objects.push(var6x);
                     if (this.objects.size() == 1) {
                        var6x.addValue(DataType.STRING, "gui_timestamp", var5);
                        var6x.addValue(DataType.STRING, "gui_LpTimestamp", var5);
                        var6x.addValue(DataType.STRING, "gui_DataTimestamp", var6);
                     }
                  } else {
                     this.dataName = var5x;
                  }

               }

               public void endElement(String var1, String var2, String var3) throws SAXException {
                  if (!this.objects.isEmpty()) {
                     DataType var4x = DataType.getTypeForName(var3);
                     if (var4x == DataType.OBJECT) {
                        this.objects.pop();
                        return;
                     }

                     if (var4x != null) {
                        try {
                           ((ObjectValueImpl)this.objects.peek()).addValue(var4x, this.dataName, this.sb.toString());
                        } catch (Exception var6x) {
                           VariableTaskClosure.this.core.logEvent(Level.WARNING, var6x.getMessage());
                        }
                     }
                  }

               }

               public void characters(char[] var1, int var2, int var3) throws SAXException {
                  this.sb.append(var1, var2, var3);
               }
            }));
            this.handleData.execute(var4);
            var3.reset();
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }
   }

   private static final String getLpTimestamp(Node var0) {
      try {
         return var0.getAttributes().getNamedItem("lptimestamp").getTextContent();
      } catch (Exception var2) {
         return null;
      }
   }

   private static final String getDataTimestamp(Node var0) {
      try {
         return var0.getAttributes().getNamedItem("dataTimestamp").getTextContent();
      } catch (Exception var2) {
         return null;
      }
   }
}
