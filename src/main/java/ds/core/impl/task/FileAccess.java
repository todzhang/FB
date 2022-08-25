package ds.core.impl.task;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.FileManips;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileAccess extends AbstractDataAccess implements TaskDataAccess {
   final File file;
   final String relativeLocation;
   long length = -1L;

   public FileAccess(Task var1, DataType var2, File var3, String var4, int var5) {
      super(var1, var2, var5);
      this.file = var3;
      this.relativeLocation = var4;
   }

   @Override
   public InputStreamReader getReader() {
      try {
         return FileManips.createFileReader(this.file);
      } catch (Exception var2) {
         return null;
      }
   }

   /** @deprecated */
   @Override
   @Deprecated
   public InputStream getStream() {
      try {
         return FileManips.createFileStream(this.file);
      } catch (Exception var2) {
         return null;
      }
   }

   @Override
   public long getSize() {
      if (this.length < 0L) {
         this.length = this.file.length();
      }

      return this.length;
   }

   public String toString() {
      return this.file == null ? "null" : String.format("File:  %s", this.file.getAbsolutePath());
   }

   @Override
   public String getLocation() {
      return this.file == null ? "" : this.file.getAbsolutePath();
   }

   @Override
   public String getLocationType() {
      return "File";
   }

   @Override
   public String getRelativeLocation() {
      return this.relativeLocation;
   }

   public File getFile() {
      return this.file;
   }
}
