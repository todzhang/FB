package ddb.targetmodel.filemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringData extends Data<String> {
   public StringData(String var1) {
      super(var1);
   }

   public void retreive(ResultSet var1, int var2) throws SQLException {
      this.setValue(var1.getString(var2), false);
   }

   public void prepare(PreparedStatement var1, int var2) throws SQLException {
      var1.setString(var2, (String)this.data);
   }

   protected boolean isValid(Object var1) {
      return var1 instanceof String;
   }

   public int compareTo(Data<String> var1) {
      if (this.data == null && var1.data == null) {
         return 0;
      } else if (this.data == null) {
         return 1;
      } else {
         return var1.data == null ? -1 : ((String)this.data).compareTo((String)var1.data);
      }
   }
}
