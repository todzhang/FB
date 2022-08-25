package ddb.bcb;

import javax.swing.Icon;
import javax.swing.JComponent;

public final class BreadcrumbItem {
   protected String[] value;
   private int index = 0;
   private JComponent jc = null;
   private Icon icon;

   public BreadcrumbItem(String[] var1) {
      assert var1 != null;

      this.value = var1;
   }

   public BreadcrumbItem(String var1) {
      assert var1 != null;

      this.value = new String[1];
      this.value[0] = var1;
   }

   public String[] getValue() {
      return this.value;
   }

   public String getName() {
      return this.value == null ? null : this.value[0];
   }

   public void setName(String var1) {
      if (this.value != null && var1 != null) {
         this.value[0] = var1;
      }

   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int var1) {
      this.index = var1;
   }

   public JComponent getComponent() {
      return this.jc;
   }

   public void setComponent(JComponent var1) {
      this.jc = var1;
   }

   @Override
   public String toString() {
      return this.getName();
   }

   public Icon getIcon() {
      return this.icon;
   }

   public void setIcon(Icon var1) {
      this.icon = var1;
   }
}
