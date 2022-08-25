package ddb.writequeue;

public abstract class AbstractWritable implements Writable {
   public boolean resets() {
      return false;
   }

   public boolean combine(Writable var1) {
      return false;
   }
}
