package ddb.dsz.plugin.logviewer.gui.screenlog;

import java.io.File;
import java.util.Calendar;

public enum ScreenLogColumns {
   FILENAME("Filename", File.class),
   TIMESTAMP("Timestamp", Calendar.class),
   SIZE("Size", Long.class);

   String name;
   Class<?> type;

   private ScreenLogColumns(String name, Class<?> type) {
      this.name = name;
      this.type = type;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getType() {
      return this.type;
   }
}
