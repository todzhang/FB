package ddb.targetmodel.filemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongData extends Data<Long> {
   public LongData(String var1) {
      super(var1);
   }

   protected void adapt(Object var1, boolean var2) {
      if (var1 instanceof Integer) {
         long var3 = (long)(Integer)Integer.class.cast(var1);
         this.setValue(var3, var2);
      }

   }

   public void retreive(ResultSet var1, int var2) throws SQLException {
      long var3 = var1.getLong(var2);
      boolean var5 = var1.wasNull();
      if (!var5) {
         this.setValue(var3, false);
      }

   }

   public void prepare(PreparedStatement var1, int var2) throws SQLException {
      var1.setLong(var2, (Long)this.data);
   }

   protected boolean isValid(Object var1) {
      return var1 instanceof Long;
   }

   public int compareTo(Data<Long> var1) {
      if (this.data == null && var1.data == null) {
         return 0;
      } else if (this.data == null) {
         return 1;
      } else {
         return var1.data == null ? -1 : ((Long)this.data).compareTo((Long)var1.data);
      }
   }
}
