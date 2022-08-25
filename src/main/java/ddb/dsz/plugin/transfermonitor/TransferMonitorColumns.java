package ddb.dsz.plugin.transfermonitor;

import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import java.util.Calendar;

public enum TransferMonitorColumns {
   ID("Id", Integer.class),
   STATE("State", TransferState.class),
   REMOTE("Remote file name", String.class),
   LOCAL("Local file name", String.class),
   SIZE("Size", TransferRecord.class),
   TYPE("Type", String.class),
   TIME_ACCESSED("Accessed", Calendar.class),
   TIME_CREATED("Created", Calendar.class),
   TIME_MODIFIED("Modified", Calendar.class);

   String name;
   Class<?> clazz;

   private TransferMonitorColumns(String var3, Class<?> var4) {
      this.name = var3;
      this.clazz = var4;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public String toString() {
      return this.name;
   }
}
