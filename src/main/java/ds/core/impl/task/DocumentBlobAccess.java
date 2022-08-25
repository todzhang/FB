package ds.core.impl.task;

import ddb.dsz.core.task.TaskDataAccess;
import java.io.InputStream;
import java.io.StringReader;
import org.w3c.dom.Document;

public class DocumentBlobAccess extends AbstractDataAccess {
   private static final String MEMORY = "Memory";
   final String relativeLocation;
   final Document document;

   public DocumentBlobAccess(TaskDataAccess var1, Document var2) {
      super(var1.getTask(), var1.getType(), -1);
      this.document = var2;
      this.relativeLocation = var1.getRelativeLocation();
   }

   @Override
   public StringReader getReader() {
      return null;
   }

   /** @deprecated */
   @Override
   @Deprecated
   public InputStream getStream() {
      return null;
   }

   public Document getDocument() {
      return this.document;
   }

   @Override
   public long getSize() {
      return 1L;
   }

   @Override
   public String getLocation() {
      return "Memory";
   }

   @Override
   public String getLocationType() {
      return "Memory";
   }

   @Override
   public String getRelativeLocation() {
      return this.relativeLocation;
   }

   public String toString() {
      return String.format("DocumentBlob");
   }

   @Override
   public boolean isGenerated() {
      return true;
   }
}
