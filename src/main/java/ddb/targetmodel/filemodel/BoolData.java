package ddb.targetmodel.filemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoolData extends Data<Boolean> {
   public BoolData(String var1) {
      super(var1);
   }

   public void retreive(ResultSet var1, int var2) throws SQLException {
      this.setValue(var1.getObject(var2), false);
   }

   public void prepare(PreparedStatement var1, int var2) throws SQLException {
      var1.setBoolean(var2, (Boolean)this.data);
   }

   protected boolean isValid(Object var1) {
      return var1 instanceof Boolean;
   }

   protected void adapt(Object var1, boolean var2) {
      if (var1 instanceof Integer) {
         int var3 = (Integer)Integer.class.cast(var1);
         if (var3 != 0) {
            this.setValue(true, var2);
         } else {
            this.setValue(false, var2);
         }
      }

   }

   public int compareTo(Data<Boolean> var1) {
      if (this.data == null && var1.data == null) {
         return 0;
      } else if (this.data == null) {
         return 1;
      } else {
         return var1.data == null ? -1 : ((Boolean)this.data).compareTo((Boolean)var1.data);
      }
   }
}
