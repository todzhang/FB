package ddb.dsz.plugin.logviewer.gui.detail;

public enum ValueTableColumns {
   TYPE("Type"),
   NAME("Name"),
   VALUE("Value");

   String name;

   private ValueTableColumns(String str) {
      this.name = str;
   }

   public String getName() {
      return this.name;
   }
}
