package ds.util.datatransforms;

public class IncompleteDataException extends RuntimeException {
   Exception e;

   public IncompleteDataException(Exception var1) {
      this.e = var1;
   }

   public String toString() {
      return "IncompleteDataException (" + this.e.getMessage() + ")";
   }
}
