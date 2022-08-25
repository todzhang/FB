package ddb.gui.javalogviewer;

import java.util.Calendar;
import java.util.logging.LogRecord;

public enum LogRecordTableColumns {
   TIME("Time", Calendar.class, "44:44:44", true),
   LEVEL("Level", LogRecord.class, "WARNING   ", true),
   MESSAGE("Message", String.class, "", false),
   SOURCECLASS("Class", String.class, "", false),
   SOURCEMETHOD("Method", String.class, "", false),
   LOGGER("Logger", String.class, "", false);

   String name;
   Class<?> clazz;
   String defaultValue;
   boolean binding;

   public String getName() {
      return this.name;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isBinding() {
      return this.binding;
   }

   private LogRecordTableColumns(String var3, Class<?> var4, String var5, boolean var6) {
      this.name = var3;
      this.clazz = var4;
      this.defaultValue = var5;
      this.binding = var6;
   }

   public String toString() {
      return this.name;
   }
}
