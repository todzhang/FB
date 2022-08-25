package ddb.dsz.plugin.filemanager.ver3.search;

public enum SearchResultColumns {
   Name("Name", true),
   Size("Size", true),
   Path("Path", true),
   Created("Created", false),
   Accessed("Accessed", false),
   Modified("Modified", false);

   String name;
   boolean show;

   private SearchResultColumns(String var3, boolean var4) {
      this.name = var3;
      this.show = var4;
   }

   public boolean isShow() {
      return this.show;
   }
}
