package ddb.dsz.plugin.mirror;

public class FileInformation {
   String name;
   Long size;
   Long soFar;

   public final String getName() {
      return this.name;
   }

   public final Long getSize() {
      return this.size;
   }

   public final Long getSoFar() {
      return this.soFar;
   }

   public final void setName(String var1) {
      this.name = var1;
   }

   public final void setSize(Long var1) {
      this.size = var1;
   }

   public final void setSoFar(Long var1) {
      this.soFar = var1;
   }
}
