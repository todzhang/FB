package ddb.splash;

import org.apache.commons.collections.Closure;

public class ProgressItem implements Closure {
   private String icon;
   private String text;
   private Closure closure;

   public ProgressItem(String var1, String var2, Closure var3) {
      this.icon = var1;
      this.text = var2;
      this.closure = var3;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getText() {
      return this.text;
   }

   public void execute(Object var1) {
      this.closure.execute(var1);
   }
}
