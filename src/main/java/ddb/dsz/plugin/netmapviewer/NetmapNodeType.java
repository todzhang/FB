package ddb.dsz.plugin.netmapviewer;

public enum NetmapNodeType {
   GENERIC(new String[0]),
   DOMAIN(new String[0]),
   SERVER(new String[0]),
   SHARE(new String[0]),
   DISK(new String[0]),
   PRINT(new String[0]),
   OTHER(new String[0]);

   String[] possibleMatches;

   private NetmapNodeType(String... var3) {
      this.possibleMatches = var3;
   }

   public boolean match(String var1) {
      if (var1 == null) {
         return false;
      } else {
         var1 = var1.toLowerCase();
         if (this.name().toLowerCase().equals(var1)) {
            return true;
         } else {
            String[] var2 = this.possibleMatches;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               if (var5.toLowerCase().equals(var1)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
