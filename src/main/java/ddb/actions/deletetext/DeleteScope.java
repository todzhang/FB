package ddb.actions.deletetext;

import javax.swing.text.JTextComponent;

public enum DeleteScope {
   CURSOR_TO_END("cut to end"),
   WHOLE_LINE("cut line");

   String name;

   private DeleteScope(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public void generate(JTextComponent var1) {
      var1.getActionMap().put(this.getName(), new DeleteAction(var1, this));
   }

   public static void fill(JTextComponent var0) {
      DeleteScope[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DeleteScope var4 = var1[var3];
         var4.generate(var0);
      }

   }
}
