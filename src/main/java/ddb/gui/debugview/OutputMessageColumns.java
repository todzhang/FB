package ddb.gui.debugview;

import java.util.Calendar;

public enum OutputMessageColumns {
   TIME("Time", Calendar.class, "44:44:44", true),
   THREAD("Thread", Integer.class, "44444", true),
   PRIORITY("Priority", Importance.class, "Very Verbose", true),
   SECTION("Section", String.class, "WWWWWWWWW", false),
   MESSAGE("Message", String.class, (String)null, false);

   boolean binding;
   String text;
   Class<?> clazz;
   String defaultValue;

   private OutputMessageColumns(String text, Class<?> clazz, String defaultValue, boolean binding) {
      this.text = text;
      this.clazz = clazz;
      this.defaultValue = defaultValue;
      this.binding = binding;
   }

   public String getColumnName() {
      return this.text;
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

   public String toString() {
      return this.text;
   }
}
