package ddb.actions.cursor;

import javax.swing.text.JTextComponent;

public enum CursorScope {
   START("line start"),
   END("line end");

   String name;

   private CursorScope(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public void generate(JTextComponent var1) {
      var1.getActionMap().put(this.getName(), new CursorAction(var1, this));
   }

   public static void fill(JTextComponent var0) {
      CursorScope[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CursorScope var4 = var1[var3];
         var4.generate(var0);
      }

   }
}
