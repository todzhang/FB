package ddb.actions.tabnav;

import javax.swing.ActionMap;

public enum NavigationDirection {
   PREVIOUS("prev tab"),
   NEXT("next tab");

   String name;

   private NavigationDirection(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public NavigationAction generate(NavigationListener var1) {
      return new NavigationAction(var1, this);
   }

   public void generate(NavigationListener var1, ActionMap var2) {
      var2.put(this.getName(), this.generate(var1));
   }

   public static void fill(NavigationListener var0, ActionMap var1) {
      NavigationDirection[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         NavigationDirection var5 = var2[var4];
         var5.generate(var0, var1);
      }

   }
}
