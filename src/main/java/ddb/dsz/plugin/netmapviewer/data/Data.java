package ddb.dsz.plugin.netmapviewer.data;

import ddb.dsz.core.data.ObjectValue;
import ddb.util.GeneralUtilities;
import java.util.Calendar;

public abstract class Data {
   protected long dataTimestamp;
   protected long lpTimestamp;

   public Data(ObjectValue var1) {
      this.setLpTimestamp(GeneralUtilities.stringToCalendar(var1.getString(Data.DataConstants.Gui_LpTimestamp.text), (Calendar)null));
      this.setDataTimestamp(GeneralUtilities.stringToCalendar(var1.getString(Data.DataConstants.Gui_DataTimestamp.text), (Calendar)null));
   }

   public final long getLpTimestamp() {
      return this.lpTimestamp;
   }

   public final Calendar getLpTimestampAsCalendar() {
      Calendar var1 = Calendar.getInstance();
      var1.setTimeInMillis(this.lpTimestamp);
      return var1;
   }

   public final void setLpTimestamp(long var1) {
      this.lpTimestamp = var1;
   }

   public final void setLpTimestamp(Calendar var1) {
      if (var1 != null) {
         this.lpTimestamp = var1.getTimeInMillis();
      }
   }

   public final long getDataTimestamp() {
      return this.dataTimestamp;
   }

   public final Calendar getDataTimestampAsCalendar() {
      Calendar var1 = Calendar.getInstance();
      var1.setTimeInMillis(this.dataTimestamp);
      return var1;
   }

   public final void setDataTimestamp(long var1) {
      this.dataTimestamp = var1;
   }

   public final void setDataTimestamp(Calendar var1) {
      if (var1 != null) {
         this.dataTimestamp = var1.getTimeInMillis();
      }
   }

   public static enum DataConstants {
      Gui_LpTimestamp("gui_LpTimestamp"),
      Gui_DataTimestamp("gui_DataTimestamp");

      public final String text;

      private DataConstants() {
         this.text = this.name();
      }

      private DataConstants(String var3) {
         this.text = var3;
      }
   }
}
