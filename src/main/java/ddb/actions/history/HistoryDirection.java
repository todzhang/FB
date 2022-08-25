package ddb.actions.history;

import javax.swing.ActionMap;

public enum HistoryDirection {
   FIRST("history first"),
   PREVIOUS("history prev"),
   NEXT("history next"),
   LAST("history last"),
   SEARCH("history search");

   String name;

   private HistoryDirection(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public HistoryAction generate(HistoryListener var1) {
      return new HistoryAction(var1, this);
   }

   public void generate(HistoryListener var1, ActionMap var2) {
      var2.put(this.getName(), this.generate(var1));
   }

   public static void fill(HistoryListener var0, ActionMap var1) {
      HistoryDirection[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         HistoryDirection var5 = var2[var4];
         var5.generate(var0, var1);
      }

   }
}
