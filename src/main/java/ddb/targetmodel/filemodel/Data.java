package ddb.targetmodel.filemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Data<E extends Comparable<?>> implements Comparable<Data<E>> {
   private boolean modified = false;
   private String name = null;
   protected E data = null;

   public final String getName() {
      return this.name;
   }

   public final boolean isModified() {
      return this.modified && this.data != null;
   }

   public final void setModified(boolean var1) {
      this.modified = var1;
   }

   protected Data(String var1) {
      this.name = var1;
      this.modified = false;
   }

   public abstract void retreive(ResultSet var1, int var2) throws SQLException;

   public final boolean hasValue() {
      return this.data != null;
   }

   public final void setValue(Object var1) {
      this.setValue(var1, true);
   }

   public final void setValue(Object var1, boolean var2) {
      if (var1 != null && !this.isValid(var1)) {
         this.adapt(var1, var2);
      } else {
         this.data = (E) var1;
         if (var2) {
            this.modified = true;
         }
      }

   }

   public final E getValue() {
      return this.data;
   }

   protected abstract boolean isValid(Object var1);

   protected void adapt(Object var1, boolean var2) {
   }

   public abstract void prepare(PreparedStatement var1, int var2) throws SQLException;

   public final String toString() {
      String var1 = "null";
      if (this.data != null) {
         var1 = this.data.toString();
      }

      return String.format("%s=%s", this.name, var1);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Data var2 = (Data)var1;
         return this.data == var2.data || this.data != null && this.data.equals(var2.data);
      }
   }

   public int hashCode() {
      byte var1 = 7;
      int var2 = 67 * var1 + (this.name != null ? this.name.hashCode() : 0);
      var2 = 67 * var2 + (this.data != null ? this.data.hashCode() : 0);
      return var2;
   }
}
