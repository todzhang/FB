package ddb.dsz.plugin.netmapviewer;

public class Resource {
   private boolean bAdmin = false;
   private String description;
   private String name;
   private String path;
   private String caption;
   private Resource.TYPE type;

   public boolean isAdmin() {
      return this.bAdmin;
   }

   public void setAdmin(boolean var1) {
      this.bAdmin = var1;
   }

   public String getCaption() {
      return this.caption;
   }

   public void setCaption(String var1) {
      this.caption = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String var1) {
      this.path = var1;
   }

   public Resource.TYPE getNodeType() {
      return this.type;
   }

   public void setNodeType(Resource.TYPE var1) {
      this.type = var1;
   }

   public void setNodeType(String var1) {
      Resource.TYPE[] var2 = Resource.TYPE.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Resource.TYPE var5 = var2[var4];
         if (var5.match(var1)) {
            this.setNodeType(var5);
            return;
         }
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.name);
      var1.append(String.format(" (%s/%s)\n", this.type.toString(), this.description));
      var1.append("    ");
      var1.append(this.path);
      return var1.toString();
   }

   public static enum TYPE {
      DEVICE(new String[0]),
      DISK(new String[0]),
      IPC(new String[0]),
      PRINT(new String[0]);

      String[] possibleMatches;

      private TYPE(String... var3) {
         this.possibleMatches = var3;
      }

      boolean match(String var1) {
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
}
