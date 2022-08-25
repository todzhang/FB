package ddb.detach;

public enum Alignment implements Comparable<Alignment> {
   LEFT,
   CENTER,
   RIGHT,
   DEFAULT;

   public static Alignment getAlignment(String var0) {
      if (var0 != null) {
         if (var0.equalsIgnoreCase("left")) {
            return LEFT;
         }

         if (var0.equalsIgnoreCase("right")) {
            return RIGHT;
         }
      }

      return CENTER;
   }
}
