package ddb.writequeue;

public interface Writable {
   boolean resets();

   boolean combine(Writable var1);
}
