package ddb.targetmodel.filemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarData extends Data<Calendar> {
   public CalendarData(String var1) {
      super(var1);
   }

   public void retreive(ResultSet var1, int var2) throws SQLException {
      Timestamp var3 = var1.getTimestamp(var2);
      if (var3 != null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTimeInMillis(var3.getTime());
         this.setValue(var4, false);
      }
   }

   public void prepare(PreparedStatement var1, int var2) throws SQLException {
      if (this.data != null) {
         var1.setTimestamp(var2, new Timestamp(((Calendar)this.data).getTimeInMillis()), (Calendar)this.data);
      } else {
         var1.setNull(var2, 93);
      }

   }

   protected boolean isValid(Object var1) {
      return var1 instanceof Calendar;
   }

   public int compareTo(Data<Calendar> var1) {
      if (this.data == null && var1.data == null) {
         return 0;
      } else if (this.data == null) {
         return 1;
      } else {
         return var1.data == null ? -1 : ((Calendar)this.data).compareTo((Calendar)var1.data);
      }
   }
}
