package ds.plugin.replay;

import ddb.util.Guid;
import java.io.File;
import java.util.Calendar;

public enum ReplayTableModelColumns {
   LOADED(Boolean.class, "Loaded"),
   FILE(File.class, "Filename"),
   START(Calendar.class, "Start Time"),
   GUID(Guid.class, "GUID");

   private Class<?> clazz;
   private String name;

   private ReplayTableModelColumns(Class<?> var3, String var4) {
      this.clazz = var3;
      this.name = var4;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public String getName() {
      return this.name;
   }
}
