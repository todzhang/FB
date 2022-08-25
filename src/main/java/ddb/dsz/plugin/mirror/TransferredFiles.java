package ddb.dsz.plugin.mirror;

public enum TransferredFiles {
   FILENAME("Filename", String.class),
   SIZE("Size", Long.class),
   TODATE("Total Transfered", Long.class);

   String caption;
   Class<?> clazz;

   private TransferredFiles(String var3, Class<?> var4) {
      this.caption = var3;
      this.clazz = var4;
   }

   public final Class<?> getClazz() {
      return this.clazz;
   }

   public final String getCaption() {
      return this.caption;
   }
}
