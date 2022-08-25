package ddb.dsz;

public enum Icons {
   LOCAL_HOST("images/home.png"),
   REMOTE_HOST("images/internet.png");

   String path;

   private Icons(String var3) {
      this.path = var3;
   }

   public String getIcon() {
      return this.path;
   }
}
