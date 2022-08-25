package ddb.detach;

import javax.swing.JComponent;

public abstract class TabbableOption extends AbstractTabbable {
   private Class<? extends Tabbable> clazz = null;
   private Tabbable parent = null;

   public TabbableOption() {
   }

   public TabbableOption(Tabbable var1) {
      this.parent = var1;
   }

   public TabbableOption(Class<? extends Tabbable> var1) {
      this.clazz = var1;
   }

   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }

   @Override
   public String getLogo() {
      String var1 = super.getLogo();
      return var1 != null ? var1 : (this.parent != null ? this.parent.getLogo() : null);
   }

   public String getName() {
      String var1 = super.getName();
      return var1 != null ? var1 : (this.parent != null ? this.parent.getName() : null);
   }

   @Override
   public String getDockedTitle() {
      return this.getName();
   }

   @Override
   public String getDetachedTitle() {
      return this.getName();
   }

   @Override
   public String getShortDescription() {
      String var1 = super.getShortDescription();
      return var1 != null ? var1 : (this.parent != null ? this.parent.getShortDescription() : null);
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }
}
